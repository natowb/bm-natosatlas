package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.chunk.NAChunkBuilder;
import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;

public class NAChunkBuilderCave implements NAChunkBuilder {
    @Override
    public NAChunk build(NACoord chunkCoord, ChunkWrapper wrapper) {
        int playerY = (int) ClientWorldAccess.get().getPlayer().y;
        NAChunk chunk = new NAChunk();

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {

                int floorY = findTopmostCaveFloor(wrapper, x, z, playerY);

                if (floorY < 0) {
                    chunk.set(x, z, -1, 0, 0, 0, 0, null);
                    continue;
                }

                int blockId = wrapper.getBlockId(x, floorY, z);
                int blockLight = wrapper.getBlockLight(x, floorY, z);
                int meta = wrapper.getBlockMeta(x, floorY, z);

                int worldX = chunkCoord.x * 16 + x;
                int worldZ = chunkCoord.z * 16 + z;
                NABiome biome = ClientWorldAccess.get().getBiome(NACoord.from(worldX, worldZ));

                chunk.set(x, z, floorY, blockId, 0, blockLight, meta, biome);
            }
        }
        return chunk;
    }


    private static int findTopmostCaveFloor(ChunkWrapper chunk, int x, int z, int playerY) {

        int startY = Math.min(playerY, ClientWorldAccess.get().getWorldInfo().getWorldHeight() - 1);

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
