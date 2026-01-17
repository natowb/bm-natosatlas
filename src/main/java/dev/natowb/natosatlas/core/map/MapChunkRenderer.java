package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NAChunk;

public interface MapChunkRenderer {
    void applyChunkToRegion(MapRegion tile, int chunkX, int chunkZ, NAChunk chunk, boolean useBlockLight);
}

