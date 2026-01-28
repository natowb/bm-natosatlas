package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionFile;

import java.io.File;
import java.util.List;

public interface NAServerPlatform {

    String getLevelName();

    List<NARegionFile> getRegionFiles();

    ChunkWrapper getChunk(NACoord chunkCoord);
}
