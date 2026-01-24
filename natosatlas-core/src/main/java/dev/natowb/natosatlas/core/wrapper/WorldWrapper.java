package dev.natowb.natosatlas.core.wrapper;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;

import java.util.List;

public interface WorldWrapper {
    String getName();

    String getSaveName();

    long getTime();

    long getSeed();

    int getDimensionId();

    boolean isServer();

    NABiome getBiome(NACoord blockCoord);

    List<NAEntity> getEntities();

    List<NAEntity> getPlayers();

    NAEntity getPlayer();

    ChunkWrapper getChunk(NACoord chunkCoord);
    ChunkWrapper getChunkFromDisk(NACoord chunkCoord);
}