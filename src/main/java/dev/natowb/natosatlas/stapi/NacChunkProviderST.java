package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformChunkProvider;
import dev.natowb.natosatlas.core.map.MapChunk;
import dev.natowb.natosatlas.core.utils.LogUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.modificationstation.stationapi.impl.world.chunk.FlattenedWorldChunkLoader;

import java.io.File;
import java.util.Set;

import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.*;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class NacChunkProviderST implements PlatformChunkProvider {

    private final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();


    @Override
    public MapChunk buildSurface(int chunkX, int chunkZ) {
        Chunk chunk = mc.world.getChunk(chunkX, chunkZ);
        MapChunk nac = new MapChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int height = getBlockHeight(chunk, x, z);
                int blockId = chunk.getBlockId(x, height, z);
                int depth = computeFluidDepth(chunk, x, height, z);
                int blockLight = safeBlockLight(chunk, x, height + 1, z);
                int meta = chunk.getBlockMeta(x, height, z);
                nac.set(x, z, height, blockId, depth, blockLight, meta);
            }
        }
        return nac;
    }

    @Override
    public MapChunk buildFromStorage(int chunkX, int chunkZ) {
        File worldDir = NatosAtlas.get().platform.getMinecraftDirectory().resolve("saves/" + NatosAtlas.get().platform.worldProvider.getName()).toFile();
        FlattenedWorldChunkLoader chunkLoader = new FlattenedWorldChunkLoader(worldDir);
        Chunk chunk = chunkLoader.loadChunk(mc.world, chunkX, chunkZ);

        MapChunk nac = new MapChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int height = getBlockHeight(chunk, x, z);
                int blockId = chunk.getBlockId(x, height, z);
                int depth = computeFluidDepth(chunk, x, height, z);
                int blockLight = safeBlockLight(chunk, x, height + 1, z);
                int meta = chunk.getBlockMeta(x, height, z);
                nac.set(x, z, height, blockId, depth, blockLight, meta);
            }
        }

        return nac;
    }

    private static final Set<Integer> SURFACE_BLACKLIST = Set.of(
            BLOCK_AIR_ID,
            BLOCK_TALL_GRASS_ID,
            BLOCK_DEAD_BUSH_ID,
            BLOCK_FLOWER_YELLOW_ID,
            BLOCK_FLOWER_RED_ID,
            BLOCK_MUSHROOM_BROWN_ID,
            BLOCK_MUSHROOM_RED_ID,
            BLOCK_TORCH_ID,
            BLOCK_REDSTONE_TORCH_IDLE_ID,
            BLOCK_REDSTONE_TORCH_ACTIVE_ID,
            BLOCK_REDSTONE_WIRE_ID,
            BLOCK_BUTTON_ID,
            BLOCK_SIGN_POST_ID,
            BLOCK_SIGN_WALL_ID,
            BLOCK_REED_ID,
            BLOCK_SAPLING_ID,
            BLOCK_WEB_ID,
            BLOCK_FIRE_ID
    );


    private int getBlockHeight(Chunk chunk, int x, int z) {
        int y = chunk.getHeight(x, z);
        int id = chunk.getBlockId(x, y, z);
        if (!SURFACE_BLACKLIST.contains(id)) {
            return y;
        }

        while (y > 0) {
            y--;
            id = chunk.getBlockId(x, y, z);
            if (SURFACE_BLACKLIST.contains(id)) {
                continue;
            }
            return y;
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
