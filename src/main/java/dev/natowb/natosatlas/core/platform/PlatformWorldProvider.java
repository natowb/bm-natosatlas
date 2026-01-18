package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;

public interface PlatformWorldProvider {
    String getName();

    boolean isRemote();

    int getDimension();

    boolean isDaytime();

    void generateExistingChunks();

    NABiome getBiome(NACoord blockCoord);

    NAChunk buildSurface(NACoord chunkCoord);

    NAChunk buildFromStorage(NACoord chunkCoord);
}
