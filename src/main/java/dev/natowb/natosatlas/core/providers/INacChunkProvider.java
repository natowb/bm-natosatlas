package dev.natowb.natosatlas.core.providers;


import dev.natowb.natosatlas.core.models.NacChunk;

public interface INacChunkProvider {
    NacChunk buildChunk(int chunkX, int chunkZ);
}
