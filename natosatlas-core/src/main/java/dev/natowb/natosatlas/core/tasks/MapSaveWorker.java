package dev.natowb.natosatlas.core.tasks;


import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRegion;
import dev.natowb.natosatlas.core.map.MapStorage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapSaveWorker {
    private static final BlockingQueue<SaveTask> QUEUE = new LinkedBlockingQueue<>();
    private static volatile boolean running = false;
    private static volatile boolean shutdownRequested = false;
    private static final Map<String, SaveTask> pending = new HashMap<>();

    public static int getPendingCount() {
        return pending.size();
    }

    private static Thread workerThread;

    public static synchronized void enqueue(MapStorage storage, NACoord coord, MapRegion region, File regionFile) {
        if (shutdownRequested) return;

        String key = regionFile.getAbsolutePath();
        SaveTask existing = pending.get(key);
        if (existing != null) {
            existing.region = region;
            return;
        }

        SaveTask task = new SaveTask(storage, coord, region, regionFile);
        pending.put(key, task);
        QUEUE.offer(task);
    }

    public static void start() {
        if (running) return;
        running = true;
        shutdownRequested = false;

        workerThread = new Thread(() -> {
            while (true) {
                try {
                    SaveTask task;

                    if (running) {
                        task = QUEUE.take();
                    } else {
                        task = QUEUE.poll();
                        if (task == null) break;
                    }

                    task.storage.saveRegionBlocking(task.coord, task.region, task.regionFile);
                    pending.remove(task.regionFile.getAbsolutePath());

                } catch (InterruptedException ignored) {
                    // ignore if we are  interrupted while we are stopping,
                    // as the loop will save all remaining queue items before stopping.
                }
            }
        }, "NatosAtlas-RegionSaveWorker");


        workerThread.setDaemon(true);
        workerThread.start();
    }

    public static void stop() {
        shutdownRequested = true;

        running = false;

        try {
            if (workerThread != null) {
                workerThread.join();
            }
        } catch (InterruptedException ignored) {
        }

        workerThread = null;
        pending.clear();
    }

    private static final class SaveTask {
        final MapStorage storage;
        final NACoord coord;
        MapRegion region;
        final File regionFile;

        SaveTask(MapStorage storage, NACoord coord, MapRegion region, File regionFile) {
            this.storage = storage;
            this.coord = coord;
            this.region = region;
            this.regionFile = regionFile;
        }
    }
}

