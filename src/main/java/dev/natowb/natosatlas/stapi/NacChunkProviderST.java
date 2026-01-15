package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.providers.INacChunkProvider;
import dev.natowb.natosatlas.core.models.NacChunk;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class NacChunkProviderST implements INacChunkProvider {

    private boolean isBlockWater(int blockId) {
        return blockId == 8 || blockId == 9;
    }

    @Override
    public NacChunk buildChunk(int chunkX, int chunkZ) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        Chunk chunk = mc.world.getChunk(chunkX, chunkZ);

        NacChunk nac = new NacChunk();

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {

                int y = chunk.getHeight(x, z);
                int blockId = chunk.getBlockId(x, y - 1, z);

                int waterDepth = computeWaterDepth(x, y - 1, z, chunk);

                nac.set(x, z, blockId, y, waterDepth);
            }
        }

        return nac;
    }

    private int computeWaterDepth(int x, int y, int z, Chunk chunk) {
        int depth = 0;
        while (y > 0) {
            int id = chunk.getBlockId(x, y, z);
            if (!isBlockWater(id)) break;
            depth++;
            y--;
        }
        return depth;
    }
}
