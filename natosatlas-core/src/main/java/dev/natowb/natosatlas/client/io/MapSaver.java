package dev.natowb.natosatlas.client.io;

import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.client.cache.NARegionPixelCache;
import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.storage.NARegionStorage;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MapSaver {

    private static MapSaver instance;

    public static MapSaver get() {
        if (instance == null) instance = new MapSaver();
        return instance;
    }

    private static final int BATCH_SIZE = 64;

    private final NARegionPixelCache cache = NARegionPixelCache.get();
    private final NARegionStorage storage = NARegionStorage.get();

    private final BlockingQueue<SaveTask> queue = new LinkedBlockingQueue<>();
    private final Map<String, SaveTask> pending = new HashMap<>();

    private Thread workerThread;
    private volatile boolean running = false;
    private volatile boolean shutdownRequested = false;

    private MapSaver() {
    }

    public void start() {
        if (running) return;
        running = true;
        shutdownRequested = false;

        workerThread = new Thread(this::processTasks, "NA-SaveWorker");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    public void stop() {
        shutdownRequested = true;
        running = false;

        if (workerThread != null) {
            workerThread.interrupt();
            try {
                workerThread.join();
            } catch (InterruptedException ignored) {
            }
        }

        workerThread = null;
        pending.clear();
    }

    public void saveNextBatch() {
        if (!running) return;

        for (int i = 0; i < BATCH_SIZE; i++) {
            Long key = cache.pollDirty();
            if (key == null) break;
            enqueueRegion(NACoord.fromKey(key));
        }
    }

    public int getPendingCount() {
        return pending.size();
    }

    private void enqueueRegion(NACoord coord) {
        int dim = ClientWorldAccess.get().getWorldInfo().getDimensionId();
        boolean isMultiplayer = ClientWorldAccess.get().getWorldInfo().isMultiplayer();

        for (NALayer layer : LayerRegistry.getLayers()) {
            NARegionPixelData region = cache.getRegion(layer.id, coord);
            if (region == null) continue;

            Path baseDir = NAPaths.getWorldMapStoragePath(layer.id, dim, !isMultiplayer);
            File file = baseDir.resolve("region_" + coord.x + "_" + coord.z + ".png").toFile();

            enqueueTask(new SaveTask(storage, coord, region, file));
        }
    }

    private synchronized void enqueueTask(SaveTask task) {
        if (shutdownRequested) return;

        String key = task.regionFile.getAbsolutePath();
        SaveTask existing = pending.get(key);

        if (existing != null) {
            existing.region = task.region;
            return;
        }

        pending.put(key, task);
        queue.offer(task);
    }

    private void processTasks() {
        while (true) {
            SaveTask task;

            try {
                if (running) {
                    task = queue.take();
                } else {
                    task = queue.poll();
                    if (task == null) break;
                }
            } catch (InterruptedException e) {
                if (!running) break;
                continue;
            }

            task.storage.saveRegionBlocking(task.coord, task.region, task.regionFile);
            pending.remove(task.regionFile.getAbsolutePath());
        }
    }

    private static final class SaveTask {
        final NARegionStorage storage;
        final NACoord coord;
        final File regionFile;
        NARegionPixelData region;

        SaveTask(NARegionStorage storage, NACoord coord, NARegionPixelData region, File regionFile) {
            this.storage = storage;
            this.coord = coord;
            this.region = region;
            this.regionFile = regionFile;
        }
    }
}
