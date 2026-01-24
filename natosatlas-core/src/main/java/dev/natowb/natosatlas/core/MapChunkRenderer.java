package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRegion;

public interface MapChunkRenderer {
    void applyChunkToRegion(MapRegion tile, NACoord chunkCoord, boolean useBlockLight);
}

