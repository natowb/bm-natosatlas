package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;

import java.util.*;

public class MapCache {

    private final Queue<Long> dirtyQueue = new ArrayDeque<>();
    private final Set<Long> dirtySet = new HashSet<>();

    private static final int PNG_CACHE_SIZE = 256;

    private final MapStorage storage;
    private final Map<Long, MapRegion[]> regions = new LinkedHashMap<>();

    private final Map<Long, int[][]> pngCache =
            new LinkedHashMap<Long, int[][]>(PNG_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, int[][]> eldest) {
                    return size() > PNG_CACHE_SIZE;
                }
            };

    public MapCache(MapStorage storage) {
        this.storage = storage;
    }

    private long makeDimKey(int dimension, NACoord coord) {
        return (((long) dimension) << 48) ^ coord.toKey();
    }

    public MapRegion getRegion(int layerId, NACoord coord) {
        int dim = NatosAtlas.get().getCurrentWorld().getDimensionId();
        long key = makeDimKey(dim, coord);

        MapRegion[] arr = regions.get(key);
        if (arr != null && arr[layerId] != null)
            return arr[layerId];

        int[][] cachedLayers = pngCache.get(key);
        if (cachedLayers != null && cachedLayers[layerId] != null) {
            MapRegion region = new MapRegion();
            System.arraycopy(cachedLayers[layerId], 0, region.getPixels(), 0, cachedLayers[layerId].length);

            if (arr == null) {
                arr = new MapRegion[NatosAtlas.get().layers.getLayers().size()];
                regions.put(key, arr);
            }

            arr[layerId] = region;
            return region;
        }

        Optional<MapRegion> loaded = storage.loadRegion(layerId, coord);
        if (loaded.isPresent()) {
            if (arr == null) {
                arr = new MapRegion[NatosAtlas.get().layers.getLayers().size()];
                regions.put(key, arr);
            }

            arr[layerId] = loaded.get();

            int[][] layersPixels = pngCache.computeIfAbsent(key, k -> new int[NatosAtlas.get().layers.getLayers().size()][]);
            layersPixels[layerId] = arr[layerId].getPixels().clone();

            return arr[layerId];
        }

        return null;
    }

    public void put(int layerId, NACoord coord, MapRegion region) {
        int dim = NatosAtlas.get().getCurrentWorld().getDimensionId();
        long key = makeDimKey(dim, coord);

        MapRegion[] arr = regions.computeIfAbsent(key, k -> new MapRegion[NatosAtlas.get().layers.getLayers().size()]);
        arr[layerId] = region;
    }

    public void markDirty(NACoord coord) {
        int dim = NatosAtlas.get().getCurrentWorld().getDimensionId();
        long key = makeDimKey(dim, coord);

        if (dirtySet.add(key)) {
            dirtyQueue.add(key);
        }
    }

    public Long pollDirty() {
        Long key = dirtyQueue.poll();
        if (key != null) dirtySet.remove(key);
        return key;
    }

    public void clear() {
        for (MapRegion[] arr : regions.values()) {
            if (arr == null) continue;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null) {
                    arr[i].clearTexture();
                    arr[i] = null;
                }
            }
        }
        regions.clear();
        dirtyQueue.clear();
        dirtySet.clear();
        pngCache.clear();
    }

    public int getCacheSize() {
        return regions.size();
    }

    public int getDirtyQueueSize() {
        return dirtyQueue.size();
    }

    public int getPngCacheSize() {
        return pngCache.size();
    }
}
