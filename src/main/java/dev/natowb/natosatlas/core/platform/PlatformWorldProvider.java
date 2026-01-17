package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.data.*;

import java.util.List;

public interface PlatformWorldProvider {

    NAWorldInfo getWorldInfo();

    NAChunk getChunk(NACoord chunkCoord);

    NAChunk getChunkFromDisk(NACoord chunkCoord);

    NABiome getBiome(NACoord blockCoord);

    List<NAEntity> getEntities();

    List<NAEntity> getPlayers();

    NAEntity getPlayer();

    void generateExistingChunks();
}
