package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;

public interface NAChunkBuilder {
    NAChunk build(NACoord chunkCoord, ChunkWrapper wrapper);
}
