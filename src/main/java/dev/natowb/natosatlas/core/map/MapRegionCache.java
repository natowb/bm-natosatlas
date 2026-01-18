package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.utils.LogUtil;

import java.util.*;

public class MapRegionCache {

    private static final int PNG_CACHE_SIZE = 256;

    private final MapStorage storage;
    private final Map<Long, MapRegion> regions = new HashMap<>();
    private final Queue<Long> dirtyQueue = new ArrayDeque<>();
    private final Set<Long> dirtySet = new HashSet<>();

    private final Map<Long, int[]> pngCache = new LinkedHashMap<>(PNG_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, int[]> eldest) {
            return size() > PNG_CACHE_SIZE;
        }
    };

    public MapRegionCache(MapStorage storage) {
        this.storage = storage;
    }

    public MapRegion getRegion(MapRegionCoord coord) {
        long key = coord.toKey();

        MapRegion region = regions.get(key);
        if (region != null) return region;

        int[] cached = pngCache.get(key);
        if (cached != null) {
            region = new MapRegion();
            System.arraycopy(cached, 0, region.getPixels(), 0, cached.length);
            regions.put(key, region);
            return region;
        }

        Optional<MapRegion> loaded = storage.loadRegion(coord);
        if (loaded.isPresent()) {
            region = loaded.get();
            pngCache.put(key, region.getPixels().clone());
            regions.put(key, region);
            return region;
        }

        return null;
    }

    public void put(MapRegionCoord coord, MapRegion tile) {
        regions.put(coord.toKey(), tile);
    }

    public void markDirty(MapRegionCoord coord) {
        long key = coord.toKey();
        if (dirtySet.add(key)) {
            dirtyQueue.add(key);
        }
    }

    public void saveOneRegion() {
        Long key = dirtyQueue.poll();
        if (key == null) {
            return;
        }

        dirtySet.remove(key);

        MapRegion region = regions.get(key);
        if (region == null) {
            LogUtil.warn("RegionCache", "Dirty region {} missing from memory", key);
            return;
        }

        MapRegionCoord coord = MapRegionCoord.fromKey(key);
        storage.saveRegion(coord, region);
    }

    public void syncLoadedRegions(Set<Long> keep) {
        Iterator<Map.Entry<Long, MapRegion>> it = regions.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<Long, MapRegion> entry = it.next();
            long key = entry.getKey();

            if (!keep.contains(key)) {
                entry.getValue().clearTexture();
                it.remove();
            }
        }
    }

    public void clear() {
        LogUtil.warn("RegionCache", "Clearing all cached regions and PNG cache");
        for (MapRegion region : regions.values()) {
            region.clearTexture();
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

    public int getPngCacheCapacity() {
        return PNG_CACHE_SIZE;
    }
}
