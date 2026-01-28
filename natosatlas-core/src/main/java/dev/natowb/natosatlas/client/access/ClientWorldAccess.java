package dev.natowb.natosatlas.client.access;

import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;

import java.io.File;
import java.util.List;

public abstract class ClientWorldAccess {

    private static ClientWorldAccess instance;

    public static void set(ClientWorldAccess worldAccess) {
        instance = worldAccess;
    }

    public static ClientWorldAccess get() {
        return instance;
    }


    public abstract NAWorldInfo getWorldInfo();

    public abstract String getSaveName();

    public abstract NABiome getBiome(NACoord blockCoord);

    public abstract List<NAEntity> getEntities();

    public abstract List<NAEntity> getPlayers();

    public abstract NAEntity getPlayer();

    public abstract ChunkWrapper getChunk(NACoord chunkCoord);

    public abstract List<NARegionFile> getRegionFiles(File dimDir);
}
