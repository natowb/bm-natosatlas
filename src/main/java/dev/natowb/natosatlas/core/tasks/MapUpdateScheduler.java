package dev.natowb.natosatlas.core.tasks;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRenderer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapUpdateScheduler {

    private static final int MAX_UPDATES_PER_TICK = 64;

    private static final BlockingQueue<ChunkTask> QUEUE = new LinkedBlockingQueue<>();
    private static final Set<Long> scheduled = new HashSet<>();

    public static void enqueue(MapRenderer renderer, NACoord coord, NAChunk chunk) {
        long key = coord.toKey();

        synchronized (scheduled) {
            if (scheduled.contains(key)) return;
            scheduled.add(key);
        }

        QUEUE.offer(new ChunkTask(renderer, coord, chunk));
    }

    public static void run() {
        int processed = 0;

        while (processed < MAX_UPDATES_PER_TICK) {
            ChunkTask task = QUEUE.poll();
            if (task == null) break;

            long key = task.coord.toKey();
            synchronized (scheduled) {
                scheduled.remove(key);
            }

            task.renderer.renderChunk(task.coord, task.chunk);
            processed++;
        }
    }

    private static final class ChunkTask {
        final MapRenderer renderer;
        final NACoord coord;
        final NAChunk chunk;

        ChunkTask(MapRenderer renderer, NACoord coord, NAChunk chunk) {
            this.renderer = renderer;
            this.coord = coord;
            this.chunk = chunk;
        }
    }
}
