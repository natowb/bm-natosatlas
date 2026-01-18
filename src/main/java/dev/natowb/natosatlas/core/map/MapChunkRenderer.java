package dev.natowb.natosatlas.core.map;

public interface MapChunkRenderer {
    void applyChunkToRegion(MapRegion tile, int chunkX, int chunkZ, MapChunk chunk, boolean useBlockLight);
}

