package dev.natowb.natosatlas.client.cache;

import dev.natowb.natosatlas.client.texture.NARegionTexture;
import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.cache.NARegionPixelCache;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionPixelData;

import java.util.HashMap;
import java.util.Map;

public class NARegionTextureCache {

    private static final Map<Long, NARegionTexture[]> textures = new HashMap<>();

    public static NARegionTexture get(NACoord coord, int layerId) {
        long key = coord.toKey();
        NARegionTexture[] arr = textures.computeIfAbsent(key, k -> new NARegionTexture[LayerRegistry.getLayers().size()]);

        if (arr[layerId] == null) {
            NARegionPixelData data = NARegionPixelCache.get().getRegion(layerId, coord);
            if (data == null) return null;
            arr[layerId] = new NARegionTexture(data);
        }

        return arr[layerId];
    }

    public static void clear() {
        for (NARegionTexture[] arr : textures.values()) {
            if (arr == null) continue;
            for (NARegionTexture tex : arr) {
                if (tex != null) tex.delete();
            }
        }
        textures.clear();
    }
}
