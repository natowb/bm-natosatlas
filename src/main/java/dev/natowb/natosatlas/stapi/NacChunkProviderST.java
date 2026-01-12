package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.glue.INacChunkProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class NacChunkProviderST implements INacChunkProvider {

    @Override
    public int[] getChunkPixels(int chunkX, int chunkZ) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        int[] pixels = new int[16 * 16];
        Chunk chunk = mc.world.getChunk(chunkX, chunkZ);

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int y = chunk.getHeight(x, z);
                int color = getColor(x, y, z, chunk);

                pixels[z * 16 + x] = color;
            }
        }
        return pixels;
    }

    private static final int[] MC_BRIGHTNESS = {180, 220, 255, 135};

    public int getColor(int x, int y, int z, Chunk chunk) {
        if (y <= 0) return 0xFF000000;

        int blockId = chunk.getBlockId(x, y - 1, z);
        Block block = Block.BLOCKS[blockId];

        if (block == null) {
            return packColor(MapColor.CLEAR, 1);
        }

        MapColor mapColor = block.material.mapColor;

        if (mapColor == MapColor.BLUE) {
            return getWaterColor(x, y, z, chunk, mapColor);
        }

        int shadeIndex = computeHeightShade(x, y, z, chunk);
        return packColor(mapColor, shadeIndex);
    }

    private int getWaterColor(int x, int y, int z, Chunk chunk, MapColor mapColor) {
        int depth = computeWaterDepth(x, y - 1, z, chunk);
        double noise = ((x + z) & 1) * 0.2;
        double d = depth * 0.1 + noise;

        int shade = 1;
        if (d < 0.5) shade = 2;
        if (d > 0.9) shade = 0;

        return packColor(mapColor, shade);
    }

    private int computeHeightShade(int x, int y, int z, Chunk chunk) {
        int prevY = chunk.getHeight(x, Math.max(0, z - 1));
        int dy = y - prevY;
        if (dy > 0) return 2;
        if (dy < 0) return 0;
        return 1;
    }

    private int computeWaterDepth(int x, int y, int z, Chunk chunk) {
        int depth = 0;
        while (y > 0) {
            int id = chunk.getBlockId(x, y, z);
            if (id != Block.WATER.id && id != Block.FLOWING_WATER.id) break;
            depth++;
            y--;
        }
        return depth;
    }

    private int packColor(MapColor color, int shadeIndex) {
        int base = color.color;
        int brightness = MC_BRIGHTNESS[shadeIndex];
        int r = (base >> 16 & 255) * brightness / 255;
        int g = (base >> 8 & 255) * brightness / 255;
        int b = (base & 255) * brightness / 255;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
