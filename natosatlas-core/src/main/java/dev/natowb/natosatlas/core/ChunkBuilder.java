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
}
