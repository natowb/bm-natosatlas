package dev.natowb.natosatlas.core.map;

public final class MapLayer {
    private final int id;
    private final String name;
    private final MapRegionCache cache;
    private final MapChunkRenderer renderer;
    private final boolean usesBlockLight;
    private final int minY;
    private final int maxY;

    public MapLayer(int id, String name, MapRegionCache cache,
                    MapChunkRenderer renderer, boolean usesBlockLight,
                    int minY, int maxY) {
        this.id = id;
        this.name = name;
        this.cache = cache;
        this.renderer = renderer;
        this.usesBlockLight = usesBlockLight;
        this.minY = minY;
        this.maxY = maxY;
    }

    public int id() { return id; }
    public String name() { return name; }
    public MapRegionCache cache() { return cache; }
    public MapChunkRenderer renderer() { return renderer; }
    public boolean usesBlockLight() { return usesBlockLight; }
    public int min() { return minY; }
    public int max() { return maxY; }
}

