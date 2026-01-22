package dev.natowb.natosatlas.core.tasks;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapCache;
import dev.natowb.natosatlas.core.map.MapLayer;
import dev.natowb.natosatlas.core.map.MapRegion;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapUpdateScheduler {

    private static final int MAX_UPDATES_PER_TICK = 64;

    private static final BlockingQueue<ChunkTask> QUEUE = new LinkedBlockingQueue<>();
    private static final Map<Long, ChunkTask> pending = new HashMap<>();
    private static boolean running = false;

    public static int getPendingCount() {
        return pending.size();
    }

    public static void stop() {
        running = false;
        pending.clear();
        QUEUE.clear();
    }

    public static void start() {
        running = true;
    }

    public static synchronized void enqueue(NACoord coord, NAChunk chunk) {
        if (!running) return;
        long key = coord.toKey();

        ChunkTask existing = pending.get(key);
        if (existing != null) {
            existing.chunk = chunk;
            return;
        }

        ChunkTask task = new ChunkTask(coord, chunk);
        pending.put(key, task);
        QUEUE.offer(task);
    }

    public static void tick() {
        if (!running) return;
        int processed = 0;

        while (processed < MAX_UPDATES_PER_TICK) {
            ChunkTask task = QUEUE.poll();
            if (task == null) break;

            long key = task.coord.toKey();
            pending.remove(key);

            generateChunk(task.coord, task.chunk);
            processed++;
        }
    }

    private static void generateChunk(NACoord chunkCoord, NAChunk chunk) {
        if (chunk == null) return;

        NACoord regionCoord = new NACoord(chunkCoord.x >> 5, chunkCoord.z >> 5);

        for (MapLayer layer : NatosAtlas.get().layers.getLayers()) {
            generateChunkForLayer(regionCoord, chunkCoord, layer, chunk);
        }

        NatosAtlas.get().cache.markDirty(regionCoord);
    }

    private static void generateChunkForLayer(
            NACoord regionCoord,
            NACoord chunkCoord,
            MapLayer layer,
            NAChunk chunk
    ) {
        MapCache cache = NatosAtlas.get().cache;
        MapRegion region = cache.getRegion(layer.id, regionCoord);

        if (region == null) {
            region = new MapRegion();
            cache.put(layer.id, regionCoord, region);

            MapRegion diskLoaded = cache.getRegion(layer.id, regionCoord);
            if (diskLoaded != null) region = diskLoaded;
        }

        layer.renderer.applyChunkToRegion(region, chunkCoord, chunk, layer.usesBlockLight);
    }

    private static final class ChunkTask {
        final NACoord coord;
        NAChunk chunk;

        ChunkTask(NACoord coord, NAChunk chunk) {
            this.coord = coord;
            this.chunk = chunk;
        }
    }
}
