package dev.natowb.natosatlas.core.map;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RegionSaveWorker {
    private static final BlockingQueue<SaveTask> QUEUE = new LinkedBlockingQueue<>();
    private static volatile boolean running = false;
    public static void start() {
        if (running) return;
        running = true;

        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    SaveTask task = QUEUE.take();
                    task.storage.saveRegionBlocking(task.coord, task.region);
                } catch (InterruptedException ignored) {}
            }
        }, "NatosAtlas-RegionSaveWorker");

        worker.setDaemon(true);
        worker.start();
    }

    public static void stop() {
        running = false;
    }

    public static void enqueue(MapStorage storage, MapRegionCoord coord, MapRegion region) {
        QUEUE.offer(new SaveTask(storage, coord, region));
    }

    private static final class SaveTask {
        final MapStorage storage;
        final MapRegionCoord coord;
        final MapRegion region;

        SaveTask(MapStorage storage, MapRegionCoord coord, MapRegion region) {
            this.storage = storage;
            this.coord = coord;
            this.region = region;
        }
    }
}
