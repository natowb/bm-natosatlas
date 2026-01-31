package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NARegionFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface ServerPlatform {

    String getLevelName();

    NARegionFile getRegionFile(File regionFile);

    List<NARegionFile> getRegionFiles(int dim);

    Path getRegionDirectory(int dim);

    List<NAEntity> getPlayers(int dim);

    ChunkWrapper getChunk(int dim, NACoord chunkCoord);
}
