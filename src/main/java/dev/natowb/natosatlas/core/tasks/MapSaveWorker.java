package dev.natowb.natosatlas.core.tasks;


import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRegion;
import dev.natowb.natosatlas.core.map.MapStorage;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapSaveWorker {
    private static final BlockingQueue<SaveTask> QUEUE = new LinkedBlockingQueue<>();
    private static volatile boolean running = false;

    public static void start() {
        if (running) return;
        running = true;

        Thread worker = new Thread(() -> {
            while (running) {
                try {
                    SaveTask task = QUEUE.take();
                    task.storage.saveRegionBlocking(task.coord, task.region, task.regionFile);
                } catch (InterruptedException ignored) {
                }
            }
        }, "NatosAtlas-RegionSaveWorker");

        worker.setDaemon(true);
        worker.start();
    }

    public static void stop() {
        running = false;
    }

    public static void enqueue(MapStorage storage, NACoord coord, MapRegion region, File regionFile) {
        QUEUE.offer(new SaveTask(storage, coord, region, regionFile));
    }

    private static final class SaveTask {
        final MapStorage storage;
        final NACoord coord;
        final MapRegion region;
        final File regionFile;

        SaveTask(MapStorage storage, NACoord coord, MapRegion region, File regionFile) {
            this.storage = storage;
            this.coord = coord;
            this.region = region;
            this.regionFile = regionFile;
        }
    }
}
