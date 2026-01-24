package dev.natowb.natosatlas.core.render;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapContext;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.Constants;

import java.util.Random;
import java.util.Set;

import static dev.natowb.natosatlas.core.utils.Constants.CHUNKS_PER_MINECRAFT_REGION;
import static dev.natowb.natosatlas.core.utils.Constants.PIXELS_PER_CANVAS_CHUNK;

public class MapRenderSlimeChunks implements MapRenderStage {
    @Override
    public void render(MapContext ctx, Set<Long> visibleRegions) {
        if (!Settings.showSlimeChunks) return;
        for (long key : visibleRegions) {
            NACoord coord = NACoord.fromKey(key);
            int texId = NatosAtlas.get().textures.getTexture(coord);
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

    private boolean isSlimeChunk(int worldChunkX, int worldChunkZ) {
        long seed = NatosAtlas.get().getCurrentWorld().getSeed();
        return new Random(seed + (long) (worldChunkX * worldChunkX) * 4987142L + (long) worldChunkX * 5947611L + (long) (worldChunkZ * worldChunkZ) * 4392871L + (long) worldChunkZ * 389711L ^ 987234911L).nextInt(10) == 0;
    }

    private void drawSlimeChunkSquare(int chunkX, int chunkZ) {
        double worldX = chunkX * 16;
        double worldZ = chunkZ * 16;

        int px = (int) (worldX * Constants.PIXELS_PER_CANVAS_UNIT);
        int pz = (int) (worldZ * Constants.PIXELS_PER_CANVAS_UNIT);

        int size = PIXELS_PER_CANVAS_CHUNK;

        int x1 = px + size;
        int y1 = pz + size;
        int x2 = px;
        int y2 = pz;

        NatosAtlas.get().platform.painter.drawRect(
                x1, y1, x2, y2,
                0x8000FF00
        );
    }

}
