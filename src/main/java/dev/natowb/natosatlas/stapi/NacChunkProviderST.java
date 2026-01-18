package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.platform.PlatformChunkProvider;
import dev.natowb.natosatlas.core.map.MapChunk;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;

import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.*;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class NacChunkProviderST implements PlatformChunkProvider {

    private final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();


    @Override
    public MapChunk buildSurface(int chunkX, int chunkZ) {
        Chunk chunk = mc.world.getChunk(chunkX, chunkZ);
        MapChunk nac = new MapChunk();

        mc.getWorldStorageSource().getName();

        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {


                int y = findTopOpaqueBlock(chunk, x, z);
                int blockId = chunk.getBlockId(x, y, z);
                int waterDepth = computeFluidDepth(chunk, x, y, z);
                int blockLight = safeBlockLight(chunk, x, y + 1, z);

                nac.set(x, z, blockId, y, waterDepth, blockLight);
            }
        }

        return nac;
    }

    private int findTopOpaqueBlock(Chunk chunk, int x, int z) {
        int y = chunk.getHeight(x, z);

        while (y > 0) {
            int id = chunk.getBlockId(x, y - 1, z);

            if (id == BLOCK_WATER_STILL_ID ||
                    id == BLOCK_WATER_MOVING_ID ||
                    id == BLOCK_LAVA_STILL_ID ||
                    id == BLOCK_LAVA_MOVING_ID) {
                return y - 1;
            }

            if (Block.BLOCKS_OPAQUE[id]) {
                return y - 1;
            }

            y--;
        }

        return 0;
    }

    private int computeFluidDepth(Chunk chunk, int x, int y, int z) {
        if (y < 0) return 0;

        int depth = 0;

        while (y > 0) {
            int id = chunk.getBlockId(x, y, z);

            if (id != BLOCK_WATER_STILL_ID &&
                    id != BLOCK_WATER_MOVING_ID &&
                    id != BLOCK_LAVA_MOVING_ID &&
                    id != BLOCK_LAVA_STILL_ID) {
                break;
            }

            depth++;
            y--;
        }

        return depth;
    }


    private int safeBlockLight(Chunk chunk, int x, int y, int z) {
        if (y < 0) return 0;
        if (y > 127) y = 127;

        return chunk.getLight(LightType.BLOCK, x, y, z);
    }
}
