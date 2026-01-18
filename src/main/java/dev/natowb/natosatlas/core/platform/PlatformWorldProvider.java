package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAWorldInfo;

public interface PlatformWorldProvider {

    NAWorldInfo getWorldInfo();

    void generateExistingChunks();

    NABiome getBiome(NACoord blockCoord);

    NAChunk buildSurface(NACoord chunkCoord);
    NAChunk buildFromStorage(NACoord chunkCoord);
}
