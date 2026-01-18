package dev.natowb.natosatlas.core.platform;


import dev.natowb.natosatlas.core.map.MapChunk;

public interface PlatformChunkProvider {
    MapChunk buildSurface(int chunkX, int chunkZ);
}
