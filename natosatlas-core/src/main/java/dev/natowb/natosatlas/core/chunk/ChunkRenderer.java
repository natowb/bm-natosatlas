package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionPixelData;

public interface ChunkRenderer {
    void applyChunkToRegion(NARegionPixelData tile, NACoord chunkCoord, boolean useBlockLight, boolean fromDisk);
}

