package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.platform.PlatformWorldProvider;
import dev.natowb.natosatlas.core.utils.ColorMapperUtil;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.src.ModLoader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionChunkStorage;
import net.minecraft.world.chunk.storage.RegionFile;

import java.io.File;
import java.util.*;
import java.util.List;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class PlatformWorldProviderML implements PlatformWorldProvider {
    private static final Minecraft mc = ModLoader.getMinecraftInstance();

    @Override
    public NABiome getBiome(NACoord blockCoord) {
        Biome biome = mc.world.method_1781().getBiome(blockCoord.x, blockCoord.z);
        return new NABiome(biome.grassColor, biome.foliageColor);
    }

    @Override
    public int getBlockColor(int blockId, int blockMeta) {

        int overrideColor = ColorMapperUtil.getOverrideColor(Block.BLOCKS[blockId].getTranslationKey());

        if (overrideColor != -1) {
            return overrideColor;
        }

        if (blockId == Block.WOOL.id) {
            return ColorMapperUtil.getWoolColor(blockMeta);
        }

        if (blockId == Block.LEAVES.id) {
            return Block.LEAVES.getColor(blockMeta);
        }

        if (Block.BLOCKS[blockId].material == null) {
            return 0xFF800080;
        }
        return Block.BLOCKS[blockId].material.mapColor.color;
    }

    @Override
    public boolean isBlockGrass(int blockId) {
        return blockId == Block.GRASS_BLOCK.id;
    }


    @Override
    public List<NARegionFile> getRegionMetadata() {
        List<NARegionFile> result = new ArrayList<>();

        File regionDir = new File(NAPaths.getWorldSavePath().toFile(), "region");
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
                        if (rf.hasChunkData(x, z)) {
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
        Chunk chunk = mc.world.getChunk(chunkCoord.x, chunkCoord.z);
        NAChunk nac = new NAChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int worldBlockX = chunkCoord.x * BLOCKS_PER_MINECRAFT_CHUNK + x;
                int worldBlockZ = chunkCoord.z * BLOCKS_PER_MINECRAFT_CHUNK + z;

                int height = getTopSolidBlockY(chunk, x, z) - 1;
                int aboveId = chunk.getBlockId(x, height + 1, z);
                final int SNOW_LAYER_ID = Block.SNOW.id;
                if (aboveId == SNOW_LAYER_ID) {
                    height = height + 1;
                }


                int blockId = chunk.getBlockId(x, height, z);
                int depth = computeFluidDepth(chunk, x, height, z);
                int blockLight = safeBlockLight(chunk, x, height + 1, z);
                int meta = chunk.getBlockMeta(x, height, z);
                NABiome biome = getBiome(NACoord.from(worldBlockX, worldBlockZ));

                nac.set(x, z, height, blockId, depth, blockLight, meta, biome);
            }
        }

        return nac;
    }


    @Override
    public NAChunk getChunkFromDisk(NACoord chunkCoord) {
        RegionChunkStorage chunkLoader = new RegionChunkStorage(NAPaths.getWorldSavePath().toFile());
        Chunk chunk = chunkLoader.loadChunk(mc.world, chunkCoord.x, chunkCoord.z);

        if (chunk == null) {
            return null;
        }

        NAChunk nac = new NAChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int worldBlockX = chunkCoord.x * BLOCKS_PER_MINECRAFT_CHUNK + x;
                int worldBlockZ = chunkCoord.z * BLOCKS_PER_MINECRAFT_CHUNK + z;


                int height = getTopSolidBlockY(chunk, x, z) - 1;
                int aboveId = chunk.getBlockId(x, height + 1, z);
                final int SNOW_LAYER_ID = Block.SNOW.id;
                if (aboveId == SNOW_LAYER_ID) {
                    height = height + 1;
                }

                int blockId = chunk.getBlockId(x, height, z);
                int depth = computeFluidDepth(chunk, x, height, z);
                int blockLight = safeBlockLight(chunk, x, height + 1, z);
                int meta = chunk.getBlockMeta(x, height, z);
                NABiome biome = getBiome(NACoord.from(worldBlockX, worldBlockZ));

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
            int blockId = chunk.getBlockId(x, y, z0);
            Material mat = blockId == 0 ? Material.AIR : Block.BLOCKS[blockId].material;

            if (mat == Material.GLASS) {
                continue;
            }

            if (mat.blocksMovement() || mat.isFluid()) {
                return y + 1;
            }
        }

        return -1;
    }


    @Override
    public boolean isBlockFluid(int blockId) {
        Block block = Block.BLOCKS[blockId];
        if (block == null) {
            return false;
        }

        if (block.material == null) {
            return false;
        }

        return block.material.isFluid();
    }

    private int computeFluidDepth(Chunk chunk, int x, int y, int z) {
        if (y < 0) return 0;

        int depth = 0;

        while (y > 0) {
            int id = chunk.getBlockId(x, y, z);
            if (!isBlockFluid(id)) break;
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
