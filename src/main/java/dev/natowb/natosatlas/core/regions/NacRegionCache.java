package dev.natowb.natosatlas.core.regions;

import dev.natowb.natosatlas.core.glue.INacChunkProvider;
import dev.natowb.natosatlas.core.models.NacCache;
import dev.natowb.natosatlas.core.models.NacRegionData;
import dev.natowb.natosatlas.core.storage.INacRegionStorage;

import java.util.*;

public class NacRegionCache {

    public static final int MAX_REGIONS = 512;

    public final INacChunkProvider chunkHandler;
    private final INacRegionStorage storage;
    private final NacCache<Long, NacRegionData> tileCache;
    private final Queue<Long> dirtyQueue = new ArrayDeque<>();
    private final Set<Long> dirtySet = new HashSet<>();


    public NacRegionCache(INacChunkProvider chunkHandler, INacRegionStorage storage) {
        this.chunkHandler = chunkHandler;
        this.storage = storage;
        this.tileCache = new NacCache<>(MAX_REGIONS);
    }

    public void put(int rx, int rz, NacRegionData tile) {
        tileCache.put(createRegionKey(rx, rz), tile);
    }

    public NacCache<Long, NacRegionData> get() {
        return tileCache;
    }

    public NacRegionData getTile(int rx, int rz) {
        long key = createRegionKey(rx, rz);
        return tileCache.get(key);
    }

    public void clear() {

        System.out.printf("INFO: clearing cache of size : %d%n", tileCache.size());

        for (NacRegionData tile : tileCache.values()) {
            tile.clearTexture();
        }
        tileCache.clear();
    }

    private long createRegionKey(int rx, int rz) {
        return (((long) rx) << 32) ^ (rz & 0xffffffffL);
    }


    public void writeChunk(int chunkX, int chunkZ, int[] pixels) {
        int rx = chunkX >> 5;
        int rz = chunkZ >> 5;
        int cx = chunkX & 31;
        int cz = chunkZ & 31;

        NacRegionData tile = getTile(rx, rz);
        if (tile == null) {
            tile = new NacRegionData();
            put(rx, rz, tile);
        }

        tile.writeChunk(cx, cz, pixels);

        long key = (((long) rx) << 32) ^ (rz & 0xffffffffL);

        if (dirtySet.add(key)) {
            dirtyQueue.add(key);
        }
    }


    public void saveOneRegion() {
        Long key = dirtyQueue.poll();
        if (key == null) return;

        dirtySet.remove(key);

        NacRegionData tile = tileCache.get(key);
        if (tile == null) return;

        int rx = (int) (key >> 32);
        int rz = (int) (key & 0xFFFFFFFFL);

        storage.saveRegion(rx, rz, tile);
    }


    public void loadFromDisk() {
        Map<Long, NacRegionData> diskRegions = storage.loadAllRegions();
        for (Map.Entry<Long, NacRegionData> entry : diskRegions.entrySet()) {
            tileCache.put(entry.getKey(), entry.getValue());
        }
        System.out.printf("INFO: loaded regions from disk: %d%n", diskRegions.size());
    }


}
