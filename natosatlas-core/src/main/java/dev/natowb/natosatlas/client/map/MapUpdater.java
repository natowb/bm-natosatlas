package dev.natowb.natosatlas.client.map;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.cache.NARegionPixelCache;
import dev.natowb.natosatlas.core.chunk.ChunkRenderer;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;

import java.util.HashMap;
import java.util.List;

public class MapUpdater {

    private static final int RADIUS = 8;
    private static final int CHUNKS_PER_TICK = 5;
    private static final long REFRESH_INTERVAL_MS = 10_000;

    private int activeChunkX;
    private int activeChunkZ;

    private final HashMap<NACoord, Long> chunkUpdateTimes = new HashMap<>();
    private final List<NACoord> scanOrder = new java.util.ArrayList<>();
    private int scanIndex = 0;

    private MapUpdater() {
        buildScanOrder();
    }

    private static MapUpdater instance;

    public static MapUpdater get() {
        if (instance == null) {
            instance = new MapUpdater();
        }

        return instance;
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
        NAEntity player = NAClient.get().getPlatform().world.getPlayer();
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
            LogUtil.trace("MapUpdater: Completed full scan cycle, restarting");
        }

        NACoord offset = scanOrder.get(scanIndex++);
        NACoord coord = NACoord.from(activeChunkX + offset.x, activeChunkZ + offset.z);

        ChunkWrapper chunk = NAClient.get().getPlatform().world.getChunk(coord);
        if (chunk == null) {
            LogUtil.warn("MapUpdater: Chunk {} not loaded, skipping", coord);
            return;
        }

        long now = System.currentTimeMillis();
        Long lastUpdate = chunkUpdateTimes.get(coord);

        if (lastUpdate == null) {
            LogUtil.trace("MapUpdater: First-time scan of chunk {}, generating region", coord);
            chunkUpdateTimes.put(coord, now);
            updateChunk(coord);
            return;
        }

        if (now - lastUpdate >= REFRESH_INTERVAL_MS) {
            LogUtil.debug("MapUpdater: Chunk {} due for refresh ({} ms elapsed), regenerating", coord, now - lastUpdate);
            chunkUpdateTimes.put(coord, now);
            updateChunk(coord);
        }
    }

    private void updateChunk(NACoord chunkCoord) {
        NACoord regionCoord = new NACoord(chunkCoord.x >> 5, chunkCoord.z >> 5);

        for (NALayer layer : LayerRegistry.getLayers()) {
            updateChunkForLayer(regionCoord, chunkCoord, layer);
        }

        NARegionPixelCache.get().markDirty(regionCoord);
    }

    private void updateChunkForLayer(NACoord regionCoord, NACoord chunkCoord, NALayer layer) {
        NARegionPixelData region = NARegionPixelCache.get().getRegion(layer.id, regionCoord);

        if (region == null) {
            LogUtil.debug("MapUpdater: Creating new MapRegion for layer {} at {}", layer.id, regionCoord);
            region = new NARegionPixelData();
            NARegionPixelCache.get().put(layer.id, regionCoord, region);

            NARegionPixelData diskLoaded = NARegionPixelCache.get().getRegion(layer.id, regionCoord);
            if (diskLoaded != null) {
                region = diskLoaded;
            }
        }

        NAChunk chunk = layer.builder.build(chunkCoord, NAClient.get().getPlatform().world.getChunk(chunkCoord));
        ChunkRenderer.render(region, chunkCoord, chunk, layer.usesBlockLight);
    }
}
