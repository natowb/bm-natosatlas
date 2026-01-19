package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;

public final class MapTextureProvider {

    public int getTexture(NACoord coord) {
        MapLayer layer = NatosAtlas.get().layers.getActiveLayer();
        MapRegion region = NatosAtlas.get().cache.getRegion(layer.id, coord);
        if (region == null) return -1;

        region.updateTexture();
        return region.getTextureId();
    }
}
