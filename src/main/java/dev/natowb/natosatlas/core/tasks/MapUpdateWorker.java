package dev.natowb.natosatlas.core.tasks;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.map.MapManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapUpdateWorker {

    private static final BlockingQueue<ChunkTask> QUEUE = new LinkedBlockingQueue<>();
    private static volatile boolean running = false;

    public static void start() {
        if (running) return;
        running = true;

        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    ChunkTask task = QUEUE.take();
                    task.manager.processChunkSync(task.worldChunkX, task.worldChunkZ, task.chunk);
                } catch (InterruptedException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "NatosAtlas-ChunkUpdateWorker");

        worker.setDaemon(true);
        worker.start();
    }

    public static void stop() {
        running = false;
    }

    public static void enqueue(MapManager manager, int worldChunkX, int worldChunkZ, NAChunk chunk) {
        QUEUE.offer(new ChunkTask(manager, worldChunkX, worldChunkZ, chunk));
    }

    private record ChunkTask(MapManager manager, int worldChunkX, int worldChunkZ, NAChunk chunk) {
    }
}
