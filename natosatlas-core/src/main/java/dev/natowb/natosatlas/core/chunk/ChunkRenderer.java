package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapRegion;

public interface ChunkRenderer {
    void applyChunkToRegion(MapRegion tile, NACoord chunkCoord, boolean useBlockLight, boolean fromDisk);
}

