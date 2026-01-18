package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAWorldInfo;
import dev.natowb.natosatlas.core.platform.PlatformWorldProvider;
import dev.natowb.natosatlas.core.utils.LogUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFile;
import net.modificationstation.stationapi.impl.world.chunk.FlattenedWorldChunkLoader;

import java.io.File;
import java.util.Set;

import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.*;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_BUTTON_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_FIRE_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_FLOWER_RED_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_FLOWER_YELLOW_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_LAVA_MOVING_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_LAVA_STILL_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_MUSHROOM_BROWN_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_MUSHROOM_RED_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_REDSTONE_TORCH_ACTIVE_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_REDSTONE_TORCH_IDLE_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_REDSTONE_WIRE_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_REED_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_SAPLING_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_SIGN_POST_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_SIGN_WALL_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_TORCH_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_WATER_MOVING_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_WATER_STILL_ID;
import static dev.natowb.natosatlas.core.utils.ColorMapperUtil.BLOCK_WEB_ID;
import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class NacWorldProviderST implements PlatformWorldProvider {
    Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

    @Override
    public NAWorldInfo getWorldInfo() {
        int dimension = mc.world.dimension.id;
        boolean isServer = mc.isWorldRemote();
        long time = mc.world.getTime();

        String name;
        if (mc.isWorldRemote()) {
            name = mc.options.lastServer;
        } else {
            name = mc.world.getProperties().getName();
        }

        File worldDir = NatosAtlas.get().platform.getMinecraftDirectory().resolve("saves/" + name).toFile();
        return new NAWorldInfo(name, isServer, time, dimension, worldDir);
    }

    @Override
    public void generateExistingChunks() {
        File regionDir = new File(getWorldInfo().worldDirectory, "region");

        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mcr"));
        if (regionFiles == null || regionFiles.length == 0) {
            LogUtil.info("ChunkScanner", "No region files found.");
            return;
        }

        int totalRegions = regionFiles.length;
        int regionIndex = 0;

        for (File regionFile : regionFiles) {
            regionIndex++;

            String[] parts = regionFile.getName()
                    .substring(2, regionFile.getName().length() - 4)
                    .split("\\.");

            int rx = Integer.parseInt(parts[0]);
            int rz = Integer.parseInt(parts[1]);

            LogUtil.info(
                    "ChunkScanner",
                    "Scanning region {} of {} -> r({}, {})",
                    regionIndex, totalRegions, rx, rz
            );

            RegionFile rf = new RegionFile(regionFile);

            int processed = 0;
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {


                    if (!rf.hasChunkData(x, z)) {
                        continue;
                    }
                    processed++;

                    int worldChunkX = rx * 32 + x;
                    int worldChunkZ = rz * 32 + z;

                    NAChunk chunk = buildFromStorage(NACoord.from(worldChunkX, worldChunkZ));
                    NatosAtlas.get().regionManager.updateChunk(worldChunkX, worldChunkZ, chunk);
                }
            }
            LogUtil.info("ChunkScanner", "Finished region r({}, {})  ({} chunks found)", rx, rz, processed);
            rf.close();
        }

        LogUtil.info("ChunkScanner", "All regions scanned.");
    }


    @Override
    public NABiome getBiome(NACoord blockCoord) {
        Biome biome = mc.world.method_1781().getBiome(blockCoord.x, blockCoord.z);
        return new NABiome(biome.grassColor, biome.foliageColor);
    }

    @Override
    public NAChunk buildSurface(NACoord chunkCoord) {
        Chunk chunk = mc.world.getChunk(chunkCoord.x, chunkCoord.z);
        NAChunk nac = new NAChunk();
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
    public NAChunk buildFromStorage(NACoord chunkCoord) {
        FlattenedWorldChunkLoader chunkLoader = new FlattenedWorldChunkLoader(getWorldInfo().worldDirectory);
        Chunk chunk = chunkLoader.loadChunk(mc.world, chunkCoord.x, chunkCoord.z);

        NAChunk nac = new NAChunk();
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
