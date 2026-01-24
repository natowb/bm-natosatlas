package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.wrapper.ChunkWrapper;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;

import java.util.HashMap;

public class MapUpdater {


    private static final int RADIUS = 8;
    private static final int CHUNKS_PER_TICK = 5;

    private int activeChunkX;
    private int activeChunkZ;

    private final MapLayerManager layerManager;
    private final MapCache cache;
    private final WorldWrapper world;

    private final HashMap<NACoord, Long> chunkSaveTimes = new HashMap<>();
    private final java.util.List<NACoord> scanOrder = new java.util.ArrayList<>();
    private int scanIndex = 0;

    public MapUpdater(WorldWrapper world, MapLayerManager layerManager, MapCache cache) {
        this.world = world;
        this.layerManager = layerManager;
        this.cache = cache;
        buildScanOrder();
    }

    private void buildScanOrder() {
        scanOrder.clear();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx * dx + dz * dz > RADIUS * RADIUS) continue;
                scanOrder.add(NACoord.from(dx, dz));
            }
        }
    }

    public void tick() {
        NAEntity player = world.getPlayer();
        this.activeChunkX = player.chunkX;
        this.activeChunkZ = player.chunkZ;

        for (int i = 0; i < CHUNKS_PER_TICK; i++) {
            processNextChunk();
        }
    }

    private void processNextChunk() {
        if (scanOrder.isEmpty()) return;

        if (scanIndex >= scanOrder.size()) {
            scanIndex = 0;
            LogUtil.debug("MapUpdater: Completed full scan cycle, restarting");
        }

        NACoord offset = scanOrder.get(scanIndex++);
        NACoord coord = NACoord.from(activeChunkX + offset.x, activeChunkZ + offset.z);

        ChunkWrapper chunk = world.getChunk(coord);
        if (chunk == null) {
            LogUtil.warn("MapUpdater: Chunk {} not loaded, skipping", coord);
            return;
        }

        long latestSaveTime = chunk.getLastSaveTime();
        Long oldTime = chunkSaveTimes.get(coord);

        if (oldTime == null) {
            LogUtil.debug("MapUpdater: First-time scan of chunk {}, generating region", coord);
            chunkSaveTimes.put(coord, latestSaveTime);
            updateChunk(coord);
            return;
        }

        if (oldTime != latestSaveTime) {
            LogUtil.debug("MapUpdater: Chunk {} changed ({} -> {}), regenerating", coord, oldTime, latestSaveTime);
            chunkSaveTimes.put(coord, latestSaveTime);
            updateChunk(coord);
        }

    }

    private void updateChunk(NACoord chunkCoord) {
        NACoord regionCoord = new NACoord(chunkCoord.x >> 5, chunkCoord.z >> 5);

        for (MapLayer layer : layerManager.getLayers()) {
            updateChunkForLayer(regionCoord, chunkCoord, layer);
        }

        cache.markDirty(regionCoord);
    }

    private void updateChunkForLayer(NACoord regionCoord, NACoord chunkCoord, MapLayer layer) {
        MapRegion region = cache.getRegion(layer.id, regionCoord);

        if (region == null) {
            LogUtil.debug("MapUpdater: Creating new MapRegion for layer {} at {}", layer.id, regionCoord);
            region = new MapRegion();
            cache.put(layer.id, regionCoord, region);

            MapRegion diskLoaded = cache.getRegion(layer.id, regionCoord);
            if (diskLoaded != null) {
                region = diskLoaded;
            }
        }

        layer.renderer.applyChunkToRegion(region, chunkCoord, layer.usesBlockLight);
    }
}
