package dev.natowb.natosatlas.core.access;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;

import java.util.List;

public abstract class WorldAccess {
    private static WorldAccess instance;

    public static void setInstance(WorldAccess instance) {
        WorldAccess.instance = instance;
    }

    public static WorldAccess getInstance() {
        return instance;
    }



    public abstract boolean exists();

    public abstract String getSaveName();

    public abstract String getName();

    public abstract long getTime();

    public abstract long getSeed();

    public abstract int getDimensionId();

    public abstract boolean isServer();

    public abstract NABiome getBiome(NACoord blockCoord);

    public abstract List<NAEntity> getEntities();

    public abstract List<NAEntity> getPlayers();

    public abstract NAEntity getPlayer();

    public abstract ChunkWrapper getChunk(NACoord chunkCoord);

    public abstract ChunkWrapper getChunkFromDisk(NACoord chunkCoord);

    public abstract List<NARegionFile> getRegionFiles();
}