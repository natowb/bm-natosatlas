package dev.natowb.natosatlas.client.layers;

import dev.natowb.natosatlas.core.chunk.ChunkRenderer;

public final class MapLayer {
    public final int id;
    public final String name;
    public final ChunkRenderer renderer;
    public final boolean usesBlockLight;

    public MapLayer(int id, String name, ChunkRenderer renderer, boolean usesBlockLight) {
        this.id = id;
        this.name = name;
        this.renderer = renderer;
        this.usesBlockLight = usesBlockLight;
    }
}
