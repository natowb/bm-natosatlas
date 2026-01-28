package dev.natowb.natosatlas.core.data;

import dev.natowb.natosatlas.core.chunk.ChunkRenderer;

public final class NALayer {
    public final int id;
    public final String name;
    public final ChunkRenderer renderer;
    public final boolean usesBlockLight;

    public NALayer(int id, String name, ChunkRenderer renderer, boolean usesBlockLight) {
        this.id = id;
        this.name = name;
        this.renderer = renderer;
        this.usesBlockLight = usesBlockLight;
    }
}
