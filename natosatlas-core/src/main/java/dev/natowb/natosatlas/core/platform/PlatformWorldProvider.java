package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.data.*;

import java.util.List;

public interface PlatformWorldProvider {
    NAWorldInfo getWorldInfo();

    NAChunk getChunk(NACoord chunkCoord);

    NAChunk getChunkFromDisk(NACoord chunkCoord);

    NABiome getBiome(NACoord blockCoord);

    boolean isBlockFluid(int blockId);

    boolean isBlockGrass(int blockId);

    int getBlockColor(int blockId, int blockMeta);

    List<NAEntity> getEntities();

    List<NAEntity> getPlayers();

    NAEntity getPlayer();

    List<NARegionFile> getRegionMetadata();
}
