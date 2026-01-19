package dev.natowb.natosatlas.core.map;

public final class MapLayer {
    public final int id;
    public final String name;
    public final MapChunkRenderer renderer;
    public final boolean usesBlockLight;

    public MapLayer(int id, String name, MapChunkRenderer renderer, boolean usesBlockLight) {
        this.id = id;
        this.name = name;
        this.renderer = renderer;
        this.usesBlockLight = usesBlockLight;
    }
}
