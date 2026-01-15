package dev.natowb.natosatlas.core.regions;

import dev.natowb.natosatlas.core.models.NacCache;
import dev.natowb.natosatlas.core.models.NacChunk;
import dev.natowb.natosatlas.core.models.NacRegionData;

import java.util.*;

public class NacRegionCache {

    public static final int MAX_REGIONS = 512;

    private final NacRegionStorage storage;

    private final NacCache<Long, NacRegionData> regionCache;
    private final Queue<Long> dirtyQueue = new ArrayDeque<>();
    private final Set<Long> dirtySet = new HashSet<>();


    public NacRegionCache(NacRegionStorage storage) {
        this.storage = storage;
        this.regionCache = new NacCache<>(MAX_REGIONS);
    }

    public NacRegionData getRegion(int rx, int rz) {
        long key = regionKey(rx, rz);
        return regionCache.get(key);
    }

    public NacRegionData getOrCreateRegion(int rx, int rz) {
        long key = regionKey(rx, rz);
        NacRegionData region = regionCache.get(key);

        if (region == null) {
            region = new NacRegionData(rx, rz);
            regionCache.put(key, region);
        }

        return region;
    }

    public void writeChunk(int chunkX, int chunkZ, NacChunk chunk) {
        int rx = chunkX >> 5;
        int rz = chunkZ >> 5;
        int cx = chunkX & 31;
        int cz = chunkZ & 31;

        NacRegionData region = getOrCreateRegion(rx, rz);
        region.writeChunk(cx, cz, chunk);

        long key = regionKey(rx, rz);
        if (dirtySet.add(key)) {
            dirtyQueue.add(key);
        }
    }

    public void clear() {

        for (NacRegionData region : regionCache.values()) {
            region.clearTexture();
        }

        regionCache.clear();

        dirtyQueue.clear();
        dirtySet.clear();

        System.out.printf("INFO: cleared region cache%n");
    }

    public void markAllDirty() {

        for (Map.Entry<Long, NacRegionData> entry : regionCache.entrySet()) {
            long key = entry.getKey();
            NacRegionData region = entry.getValue();

            region.markDirty();

            if (dirtySet.add(key)) {
                dirtyQueue.add(key);
            }
        }

        System.out.printf("INFO: marked %d regions dirty due to renderer change%n", regionCache.size());
    }


    public void saveOneRegion() {
        Long key = dirtyQueue.poll();
        if (key == null) return;

        dirtySet.remove(key);

        NacRegionData region = regionCache.get(key);
        if (region == null) return;

        int rx = (int) (key >> 32);
        int rz = (int) (key & 0xFFFFFFFFL);

        storage.saveRegion(rx, rz, region);
    }

    public void loadFromDisk() {
        Map<Long, NacRegionData> loaded = storage.loadAllRegions();
        for (Map.Entry<Long, NacRegionData> entry : loaded.entrySet()) {
            regionCache.put(entry.getKey(), entry.getValue());
        }
        System.out.printf("INFO: loaded %d regions from disk%n", loaded.size());
    }

    private long regionKey(int rx, int rz) {
        return (((long) rx) << 32) ^ (rz & 0xffffffffL);
    }

    public int getCacheSize() {
        return regionCache.size();
    }
}
