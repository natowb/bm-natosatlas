package dev.natowb.natosatlas.core.data;

import dev.natowb.natosatlas.core.chunk.NAChunkBuilder;

public final class NALayer {
    public final int id;
    public final String name;
    public final NAChunkBuilder builder;
    public final boolean usesBlockLight;

    public NALayer(int id, String name, NAChunkBuilder builder, boolean usesBlockLight) {
        this.id = id;
        this.name = name;
        this.builder = builder;
        this.usesBlockLight = usesBlockLight;
    }
}
