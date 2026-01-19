package dev.natowb.natosatlas.core.tasks;


import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapCache;
import dev.natowb.natosatlas.core.map.MapRegion;
import dev.natowb.natosatlas.core.map.MapStorage;


public class MapSaveScheduler {

    private static final int MAX_SAVES_PER_TICK = 4;

    public static void run() {
        MapCache cache = NatosAtlas.get().cache;

        for (int i = 0; i < MAX_SAVES_PER_TICK; i++) {
            Long key = cache.pollDirty();
            if (key == null) break;

            MapRegion[] layers = cache.getRegionArray(key);
            if (layers == null) continue;

            NACoord coord = NACoord.fromKey(key);
            MapStorage storage = cache.getStorage();

            for (int layerId = 0; layerId < cache.getLayerCount(); layerId++) {
                MapRegion region = layers[layerId];
                if (region != null) {
                    MapSaveWorker.enqueue(storage, coord, region, storage.getRegionFile(layerId, coord));
                }
            }
        }
    }
}
