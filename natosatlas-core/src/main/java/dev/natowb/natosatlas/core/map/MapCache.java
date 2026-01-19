package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.utils.LogUtil;

import java.util.*;

public class MapCache {

    private final Queue<Long> dirtyQueue = new ArrayDeque<>();
    private final Set<Long> dirtySet = new HashSet<>();

    private static final int PNG_CACHE_SIZE = 256;

    private final MapStorage storage;
    private final Map<Long, MapRegion[]> regions = new HashMap<>();
    // TODO: make dynamic once MapLayerManager is set up
    private final int layerCount = 2;

    private final Map<Long, int[][]> pngCache = new LinkedHashMap<Long, int[][]>(PNG_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, int[][]> eldest) {
            return size() > PNG_CACHE_SIZE;
        }
    };


    public MapCache(MapStorage storage) {
        this.storage = storage;
    }

    public MapRegion getRegion(int layerId, NACoord coord) {
        long key = coord.toKey();

        MapRegion[] arr = regions.get(key);
        if (arr != null && arr[layerId] != null)
            return arr[layerId];

        int[][] cachedLayers = pngCache.get(key);
        if (cachedLayers != null && cachedLayers[layerId] != null) {
            MapRegion region = new MapRegion();
            System.arraycopy(cachedLayers[layerId], 0, region.getPixels(), 0, cachedLayers[layerId].length);

            if (arr == null) {
                arr = new MapRegion[layerCount];
                regions.put(key, arr);
            }

            arr[layerId] = region;
            return region;
        }

        Optional<MapRegion> loaded = storage.loadRegion(layerId, coord);
        if (loaded.isPresent()) {
            if (arr == null) {
                arr = new MapRegion[layerCount];
                regions.put(key, arr);
            }

            arr[layerId] = loaded.get();

            int[][] layersPixels = pngCache.computeIfAbsent(key, k -> new int[layerCount][]);
            layersPixels[layerId] = arr[layerId].getPixels().clone();

            return arr[layerId];
        }

        return null;
    }


    public void put(int layerId, NACoord coord, MapRegion region) {
        long key = coord.toKey();
        MapRegion[] arr = regions.computeIfAbsent(key, k -> new MapRegion[layerCount]);
        arr[layerId] = region;
    }

    public void markDirty(NACoord coord) {
        long key = coord.toKey();
        if (dirtySet.add(key)) {
            dirtyQueue.add(key);
        }
    }

    public Long pollDirty() {
        Long key = dirtyQueue.poll();
        if (key != null) dirtySet.remove(key);
        return key;
    }

    public MapRegion[] getRegionArray(long key) {
        return regions.get(key);
    }

    public MapStorage getStorage() {
        return storage;
    }

    public int getLayerCount() {
        return layerCount;
    }


    public void syncLoadedRegions(Set<Long> keep) {
        Iterator<Map.Entry<Long, MapRegion[]>> it = regions.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Long, MapRegion[]> entry = it.next();
            long key = entry.getKey();

            if (!keep.contains(key)) {
                MapRegion[] arr = entry.getValue();
                if (arr != null) {
                    for (int i = 0; i < arr.length; i++) {
                        if (arr[i] != null) {
                            arr[i].clearTexture();
                            arr[i] = null;
                        }
                    }
                }
                it.remove();
            }
        }
    }

    public void clear() {
        LogUtil.warn("Clearing all cached regions and PNG cache");
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
