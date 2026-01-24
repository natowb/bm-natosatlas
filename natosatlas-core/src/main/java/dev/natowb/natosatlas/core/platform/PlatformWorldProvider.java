package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.data.*;

import java.util.List;

public interface PlatformWorldProvider {

    NAChunk getChunk(NACoord chunkCoord);

    NAChunk getChunkFromDisk(NACoord chunkCoord);

    NABiome getBiome(NACoord blockCoord);

    boolean isBlockFluid(int blockId);

    boolean isBlockGrass(int blockId);

    int getBlockColor(int blockId, int blockMeta);

    List<NARegionFile> getRegionMetadata();
}
