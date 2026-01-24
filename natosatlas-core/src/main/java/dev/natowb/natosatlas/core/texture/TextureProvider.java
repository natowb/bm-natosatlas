package dev.natowb.natosatlas.core.texture;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.layers.MapLayer;
import dev.natowb.natosatlas.core.map.MapRegion;

public final class TextureProvider {

    public int getTexture(NACoord coord) {
        MapLayer layer = NatosAtlasCore.get().layers.getActiveLayer();
        MapRegion region = NatosAtlasCore.get().cache.getRegion(layer.id, coord);
        if (region == null) return -1;

        region.updateTexture();
        return region.getTextureId();
    }
}
