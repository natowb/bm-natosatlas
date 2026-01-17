package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NACoord;

public final class MapTextureProvider {
    private final MapRenderer renderer;

    public MapTextureProvider(MapRenderer renderer) {
        this.renderer = renderer;
    }

    public int getTexture(NACoord coord) {
        return renderer.getTexture(coord);
    }
}
