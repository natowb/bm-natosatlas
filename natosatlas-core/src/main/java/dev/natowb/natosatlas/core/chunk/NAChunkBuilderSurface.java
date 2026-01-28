package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.client.access.BlockAccess;
import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;

import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_MINECRAFT_CHUNK;

public class NAChunkBuilderSurface implements NAChunkBuilder {


    @Override
    public NAChunk build(NACoord chunkCoord, ChunkWrapper wrapper) {
        NAChunk nac = new NAChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int height = wrapper.getTopSolidBlockY(x, z) - 1;
                int aboveId = wrapper.getBlockId(x, height + 1, z);
                if (BlockAccess.get().isBlock(aboveId, BlockAccess.BlockIdentifier.SNOW)) {
                    height = height + 1;
                }

                int blockId = wrapper.getBlockId(x, height, z);
                int depth = wrapper.computeFluidDepth(x, height, z);
                int blockLight = wrapper.getBlockLight(x, height + 1, z);
                int meta = wrapper.getBlockMeta(x, height, z);
                // FIXME: reintroduce biome detection without client related code
//                NABiome biome = WorldAccess.get().getBiome(NACoord.from(worldBlockX, worldBlockZ));
//                int worldBlockX = chunkCoord.x * BLOCKS_PER_MINECRAFT_CHUNK + x;
//                int worldBlockZ = chunkCoord.z * BLOCKS_PER_MINECRAFT_CHUNK + z;
                nac.set(x, z, height, blockId, depth, blockLight, meta, new NABiome(0, 0));
            }
        }
        return nac;
    }
}
