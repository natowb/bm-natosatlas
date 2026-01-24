package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.wrapper.BlockAccess;
import dev.natowb.natosatlas.core.wrapper.ChunkWrapper;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class ChunkBuilder {


    public static NAChunk buildChunkSurface(WorldWrapper world, NACoord chunkCoord) {
        ChunkWrapper chunk = world.getChunk(chunkCoord);
        if (chunk == null) {
            return null;
        }
        return buildSurface(world, chunkCoord, chunk);
    }


    public static NAChunk buildChunkSurfaceFromDisk(WorldWrapper world, NACoord chunkCoord) {
        ChunkWrapper chunk = world.getChunkFromDisk(chunkCoord);
        if (chunk == null) {
            return null;
        }
        return buildSurface(world, chunkCoord, chunk);
    }


    private static NAChunk buildSurface(WorldWrapper world, NACoord chunkCoord, ChunkWrapper chunk) {
        NAChunk nac = new NAChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int worldBlockX = chunkCoord.x * BLOCKS_PER_MINECRAFT_CHUNK + x;
                int worldBlockZ = chunkCoord.z * BLOCKS_PER_MINECRAFT_CHUNK + z;

                int height = chunk.getTopSolidBlockY(x, z) - 1;
                int aboveId = chunk.getBlockId(x, height + 1, z);
                if (BlockAccess.getInstance().isBlock(aboveId, BlockAccess.BlockIdentifier.SNOW)) {
                    height = height + 1;
                }

                int blockId = chunk.getBlockId(x, height, z);
                int depth = chunk.computeFluidDepth(x, height, z);
                int blockLight = chunk.getBlockLight(x, height + 1, z);
                int meta = chunk.getBlockMeta(x, height, z);
                NABiome biome = world.getBiome(NACoord.from(worldBlockX, worldBlockZ));
                nac.set(x, z, height, blockId, depth, blockLight, meta, biome);
            }
        }
        return nac;
    }

    public static NAChunk buildCaveChunk(WorldWrapper world, NACoord chunkCoord, int playerY) {

        ChunkWrapper chunk = world.getChunk(chunkCoord);
        if (chunk == null) return null;

        NAChunk caveChunk = new NAChunk();

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {

                int floorY = findTopmostCaveFloor(chunk, x, z, playerY);

                if (floorY < 0) {
                    caveChunk.set(x, z, -1, 0, 0, 0, 0, null);
                    continue;
                }

                int blockId = chunk.getBlockId(x, floorY, z);
                int blockLight = chunk.getBlockLight(x, floorY, z);
                int meta = chunk.getBlockMeta(x, floorY, z);

                int worldX = chunkCoord.x * 16 + x;
                int worldZ = chunkCoord.z * 16 + z;
                NABiome biome = world.getBiome(NACoord.from(worldX, worldZ));

                caveChunk.set(x, z, floorY, blockId, 0, blockLight, meta, biome);
            }
        }

        return caveChunk;
    }


    private static int findTopmostCaveFloor(ChunkWrapper chunk, int x, int z, int playerY) {

        int startY = Math.min(playerY, 127);

        for (int y = startY; y > 1; y--) {

            int block = chunk.getBlockId(x, y, z);

            if (block != 0) continue;

            if (chunk.getSkyLight(x, y, z) > 0) continue;

            for (int fy = y - 1; fy > 0; fy--) {
                int floorBlock = chunk.getBlockId(x, fy, z);
                if (floorBlock != 0) {
                    return fy;
                }
            }

            return -1;
        }

        return -1;
    }


}
