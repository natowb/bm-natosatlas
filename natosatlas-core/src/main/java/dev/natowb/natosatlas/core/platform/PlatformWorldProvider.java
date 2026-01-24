package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.data.*;

import java.util.List;

public interface PlatformWorldProvider {

    NAChunk getChunk(NACoord chunkCoord);

    NAChunk getChunkFromDisk(NACoord chunkCoord);

    List<NARegionFile> getRegionMetadata();
}
