package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;

public interface MapChunkRenderer {
    void applyChunkToRegion(MapRegion tile, NACoord chunkCoord, NAChunk chunk, boolean useBlockLight);
}

