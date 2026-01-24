package dev.natowb.natosatlas.core.tasks;


import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapCache;
import dev.natowb.natosatlas.core.layers.MapLayer;
import dev.natowb.natosatlas.core.map.MapRegion;
import dev.natowb.natosatlas.core.map.MapStorage;


public class MapSaveScheduler {

    private static final int MAX_SAVES_PER_TICK = 64;
    private static boolean running = false;

    public static void start() {
        MapSaveWorker.start();
        running = true;
    }

    public static void stop() {
        MapSaveWorker.stop();
        running = false;
    }

    public static void tick() {
        if (!running) return;
        MapCache cache = NatosAtlas.get().cache;
        for (int i = 0; i < MAX_SAVES_PER_TICK; i++) {
            Long key = cache.pollDirty();
            if (key == null) break;

            NACoord coord = NACoord.fromKey(key);
            MapStorage storage = NatosAtlas.get().storage;

            for (MapLayer layer : NatosAtlas.get().layers.getLayers()) {
                MapRegion region = cache.getRegion(layer.id, coord);
                if (region != null) {
                    MapSaveWorker.enqueue(storage, coord, region, storage.getRegionPngFile(layer.id, coord));
                }
            }
        }
    }
}
