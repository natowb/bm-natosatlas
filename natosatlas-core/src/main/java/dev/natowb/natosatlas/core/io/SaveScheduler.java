package dev.natowb.natosatlas.core.io;


import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.cache.NARegionPixelCache;
import dev.natowb.natosatlas.core.data.NALayer;
import dev.natowb.natosatlas.core.data.NARegionPixelData;


public class SaveScheduler {

    private static final int MAX_SAVES_PER_TICK = 64;
    private static boolean running = false;

    public static void start() {
        SaveWorker.start();
        running = true;
    }

    public static void stop() {
        SaveWorker.stop();
        running = false;
    }

    public static void tick() {
        if (!running) return;
        NARegionPixelCache cache =  NARegionPixelCache.get();
        for (int i = 0; i < MAX_SAVES_PER_TICK; i++) {
            Long key = cache.pollDirty();
            if (key == null) break;

            NACoord coord = NACoord.fromKey(key);
            NARegionStorage storage = NARegionStorage.get();

            for (NALayer layer : LayerRegistry.getLayers()) {
                NARegionPixelData region = cache.getRegion(layer.id, coord);
                if (region != null) {
                    SaveWorker.enqueue(storage, coord, region, storage.getRegionPngFile(layer.id, coord));
                }
            }
        }
    }
}
