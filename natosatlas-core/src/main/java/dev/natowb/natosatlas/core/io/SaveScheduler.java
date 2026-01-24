package dev.natowb.natosatlas.core.io;


import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapCache;
import dev.natowb.natosatlas.core.layers.MapLayer;
import dev.natowb.natosatlas.core.map.MapRegion;
import dev.natowb.natosatlas.core.map.MapStorage;


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
        MapCache cache = NatosAtlasCore.get().cache;
        for (int i = 0; i < MAX_SAVES_PER_TICK; i++) {
            Long key = cache.pollDirty();
            if (key == null) break;

            NACoord coord = NACoord.fromKey(key);
            MapStorage storage = NatosAtlasCore.get().storage;

            for (MapLayer layer : NatosAtlasCore.get().layers.getLayers()) {
                MapRegion region = cache.getRegion(layer.id, coord);
                if (region != null) {
                    SaveWorker.enqueue(storage, coord, region, storage.getRegionPngFile(layer.id, coord));
                }
            }
        }
    }
}
