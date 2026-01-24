package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.platform.PlatformWorldProvider;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.wrapper.BlockAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkLoaderRegion;
import net.minecraft.core.world.save.mcregion.RegionFile;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class PlatformWorldProviderBTA implements PlatformWorldProvider {
    Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

    @Override
    public List<NARegionFile> getRegionMetadata() {
        List<NARegionFile> result = new ArrayList<>();

        File regionDir = new File(NAPaths.getWorldSavePath().toFile(), String.format("dimensions/%d/region", mc.currentWorld.dimension.id));
        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mcr") || name.endsWith(".mca"));
        if (regionFiles == null || regionFiles.length == 0) {
            return result;
        }

        int index = 0;

        for (File regionFile : regionFiles) {
            index++;

            boolean success = false;

            try {
                String name = regionFile.getName();
                String[] parts = name.substring(2, name.length() - 4).split("\\.");
                if (parts.length != 2) {
                    continue;
                }

                int rx = Integer.parseInt(parts[0]);
                int rz = Integer.parseInt(parts[1]);
                NACoord regionCoord = new NACoord(rx, rz);

                NARegionFile naRegion = new NARegionFile(regionFile, regionCoord);

                RegionFile rf = new RegionFile(regionFile);
                for (int x = 0; x < NARegionFile.CHUNKS_PER_REGION; x++) {
                    for (int z = 0; z < NARegionFile.CHUNKS_PER_REGION; z++) {
                        if (rf.chunkExists(x, z)) {
                            naRegion.chunkExists[x][z] = true;
                        }
                    }
                }
                rf.close();

                result.add(naRegion);
                success = true;

            } catch (Exception ignored) {
            }

            if (success) {
                LogUtil.info("[{}/{}] Successfully processed region file: {}", index, regionFiles.length, regionFile.getName());

            } else {
                LogUtil.info("[{}/{}] Failed to processed region file: {}", index, regionFiles.length, regionFile.getName());
            }
        }

        return result;
    }

    @Override
    public NAChunk getChunk(NACoord chunkCoord) {
        Chunk chunk = mc.currentWorld.getChunkFromChunkCoords(chunkCoord.x, chunkCoord.z);
        NAChunk nac = new NAChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int worldBlockX = chunkCoord.x * BLOCKS_PER_MINECRAFT_CHUNK + x;
                int worldBlockZ = chunkCoord.z * BLOCKS_PER_MINECRAFT_CHUNK + z;

                int height = getTopSolidBlockY(chunk, x, z) - 1;
                int aboveId = chunk.getBlockID(x, height + 1, z);

                final int SNOW_LAYER_ID = Blocks.LAYER_SNOW.id();
                if (aboveId == SNOW_LAYER_ID) {
                    height = height + 1;
                }


                int blockId = chunk.getBlockID(x, height, z);
                int depth = computeFluidDepth(chunk, x, height, z);
                int blockLight = safeBlockLight(chunk, x, height + 1, z);
                int meta = chunk.getBlockMetadata(x, height, z);
                NABiome biome = NatosAtlas.get().getCurrentWorld().getBiome(NACoord.from(worldBlockX, worldBlockZ));

                nac.set(x, z, height, blockId, depth, blockLight, meta, biome);
            }
        }

        return nac;
    }


    @Override
    public NAChunk getChunkFromDisk(NACoord chunkCoord) {
        ChunkLoaderRegion chunkLoader = new ChunkLoaderRegion(NAPaths.getWorldSavePath().toFile());
        Chunk chunk = null;
        try {
            chunk = chunkLoader.loadChunk(mc.currentWorld, chunkCoord.x, chunkCoord.z);
        } catch (IOException ignored) {
        }

        if (chunk == null) {
            return null;
        }

        NAChunk nac = new NAChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int worldBlockX = chunkCoord.x * BLOCKS_PER_MINECRAFT_CHUNK + x;
                int worldBlockZ = chunkCoord.z * BLOCKS_PER_MINECRAFT_CHUNK + z;

                int height = getTopSolidBlockY(chunk, x, z) - 1;
                int aboveId = chunk.getBlockID(x, height + 1, z);

                final int SNOW_LAYER_ID = Blocks.LAYER_SNOW.id();
                if (aboveId == SNOW_LAYER_ID) {
                    height = height + 1;
                }


                int blockId = chunk.getBlockID(x, height, z);
                int depth = computeFluidDepth(chunk, x, height, z);
                int blockLight = safeBlockLight(chunk, x, height + 1, z);
                int meta = chunk.getBlockMetadata(x, height, z);
                NABiome biome = NatosAtlas.get().getCurrentWorld().getBiome(NACoord.from(worldBlockX, worldBlockZ));

                nac.set(x, z, height, blockId, depth, blockLight, meta, biome);
            }
        }

        return nac;
    }

    private int getTopSolidBlockY(Chunk chunk, int x, int z) {
        int y = 127;
        x &= 15;
        int z0 = z & 15;

        for (; y > 0; --y) {
            int blockId = chunk.getBlockID(x, y, z0);
            Material mat = blockId == 0 ? Material.air : Blocks.getBlock(blockId).getMaterial();

            if (mat == Material.glass) {
                continue;
            }

            if (mat.blocksMotion() || mat.isLiquid()) {
                return y + 1;
            }
        }

        return -1;
    }


    private int computeFluidDepth(Chunk chunk, int x, int y, int z) {
        if (y < 0) return 0;

        int depth = 0;

        while (y > 0) {
            int id = chunk.getBlockID(x, y, z);
            if (!BlockAccess.getInstance().isFluid(id)) break;
            depth++;
            y--;
        }
        return depth;
    }

    private int safeBlockLight(Chunk chunk, int x, int y, int z) {
        if (y < 0) return 0;
        if (y > 127) y = 127;

        return chunk.getBrightness(LightLayer.Block, x, y, z);
    }
}
