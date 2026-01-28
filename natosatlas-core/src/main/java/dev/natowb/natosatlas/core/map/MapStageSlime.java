package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.NatoAtlasConstants;
import dev.natowb.natosatlas.core.access.WorldAccess;
import dev.natowb.natosatlas.core.texture.TextureProvider;

import java.util.Random;
import java.util.Set;

import static dev.natowb.natosatlas.core.NatoAtlasConstants.CHUNKS_PER_MINECRAFT_REGION;
import static dev.natowb.natosatlas.core.NatoAtlasConstants.PIXELS_PER_CANVAS_CHUNK;

public class MapStageSlime implements MapStage {
    @Override
    public void draw(MapContext ctx, Set<Long> visibleRegions) {
        if (!Settings.showSlimeChunks) return;
        for (long key : visibleRegions) {
            NACoord coord = NACoord.fromKey(key);
            int texId = TextureProvider.getTexture(coord);
            if (texId != -1) {
                for (int x = 0; x < CHUNKS_PER_MINECRAFT_REGION; x++) {
                    for (int z = 0; z < CHUNKS_PER_MINECRAFT_REGION; z++) {
                        int worldChunkX = coord.x * CHUNKS_PER_MINECRAFT_REGION + x;
                        int worldChunkZ = coord.z * CHUNKS_PER_MINECRAFT_REGION + z;
                        if (!isSlimeChunk(worldChunkX, worldChunkZ)) continue;
                        drawSlimeChunkSquare(worldChunkX, worldChunkZ);
                    }
                }
            }
        }
    }

    // FIXME: move this to a global acessor at some point
    private boolean isSlimeChunk(int worldChunkX, int worldChunkZ) {
        long seed = WorldAccess.get().getSeed();
        return new Random(seed + (long) (worldChunkX * worldChunkX) * 4987142L + (long) worldChunkX * 5947611L + (long) (worldChunkZ * worldChunkZ) * 4392871L + (long) worldChunkZ * 389711L ^ 987234911L).nextInt(10) == 0;
    }

    private void drawSlimeChunkSquare(int chunkX, int chunkZ) {
        double worldX = chunkX * 16;
        double worldZ = chunkZ * 16;

        int px = (int) (worldX * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT);
        int pz = (int) (worldZ * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT);

        int size = PIXELS_PER_CANVAS_CHUNK;

        int x1 = px + size;
        int y1 = pz + size;
        int x2 = px;
        int y2 = pz;

        PainterAccess.get().drawRect(
                x1, y1, x2, y2,
                0x8000FF00
        );
    }

}
