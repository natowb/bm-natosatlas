package dev.natowb.natosatlas.core.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class NARegionFile {

    public static final int CHUNKS_PER_REGION = 32;

    public final File file;
    public final NACoord regionCoord;
    public final boolean[][] chunkExists = new boolean[CHUNKS_PER_REGION][CHUNKS_PER_REGION];

    public NARegionFile(File file, NACoord regionCoord) {
        this.file = file;
        this.regionCoord = regionCoord;
    }

    public boolean hasChunk(int cx, int cz) {
        return chunkExists[cx][cz];
    }

    public Iterable<NACoord> iterateExistingChunks() {
        List<NACoord> list = new ArrayList<>();

        for (int x = 0; x < CHUNKS_PER_REGION; x++) {
            for (int z = 0; z < CHUNKS_PER_REGION; z++) {
                if (chunkExists[x][z]) {
                    int worldX = regionCoord.x * CHUNKS_PER_REGION + x;
                    int worldZ = regionCoord.z * CHUNKS_PER_REGION + z;
                    list.add(NACoord.from(worldX, worldZ));
                }
            }
        }
        return list;
    }
}
