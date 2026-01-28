package dev.natowb.natosatlas.client.map;

import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionPixelData;
import dev.natowb.natosatlas.core.io.NARegionStorage;

import java.util.*;

public class NARegionPixelCache {

    private final Queue<Long> dirtyQueue = new ArrayDeque<>();
    private final Set<Long> dirtySet = new HashSet<>();

    private static final int CACHE_SIZE = 256;

    private static NARegionPixelCache instance;

    private NARegionPixelCache() {
    }

    public static NARegionPixelCache get() {
        if (instance == null) {
            instance = new NARegionPixelCache();
        }
        return instance;
    }

    private final Map<Long, NARegionPixelData[]> regions =
            new LinkedHashMap<Long, NARegionPixelData[]>(CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, NARegionPixelData[]> eldest) {
                    return size() > CACHE_SIZE;
                }
            };

    public NARegionPixelData getRegion(int layerId, NACoord coord) {
        long key = coord.toKey();

        NARegionPixelData[] arr = regions.get(key);
        if (arr != null && arr[layerId] != null)
            return arr[layerId];

        Optional<NARegionPixelData> loaded = NARegionStorage.get().loadRegion(layerId, coord);
        if (loaded.isPresent()) {
            if (arr == null) {
                arr = new NARegionPixelData[LayerRegistry.getLayers().size()];
                regions.put(key, arr);
            }

            arr[layerId] = loaded.get();
            return arr[layerId];
        }

        return null;
    }

    public void put(int layerId, NACoord coord, NARegionPixelData region) {
        long key = coord.toKey();
        NARegionPixelData[] arr = regions.computeIfAbsent(key, k -> new NARegionPixelData[LayerRegistry.getLayers().size()]);
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

    public void clear() {
        for (NARegionPixelData[] arr : regions.values()) {
            if (arr == null) continue;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null) {
                    arr[i].deleteTexture();
                    arr[i] = null;
                }
            }
        }
        regions.clear();
        dirtyQueue.clear();
        dirtySet.clear();
    }

    public int getRegionCount() {
        return regions.size();
    }

    public int getTotalCount() {
        int total = 0;
        for (NARegionPixelData[] arr : regions.values()) {
            if (arr == null) continue;
            for (NARegionPixelData region : arr) {
                if (region != null) total++;
            }
        }
        return total;
    }

    public int getDirtyQueueSize() {
        return dirtyQueue.size();
    }
}
