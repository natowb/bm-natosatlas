package dev.natowb.natosatlas.core.models;

import dev.natowb.natosatlas.core.painter.NacPainterUtils;

import static dev.natowb.natosatlas.core.utils.NacConstants.BLOCKS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.utils.NacConstants.BLOCKS_PER_MINECRAFT_CHUNK;

public class NacRegionData {

    // TODO: remember idiot this is argb format. not rgba
    private final int[] pixels;
    private int textureId = -1;

    public NacRegionData() {
        this.pixels = new int[BLOCKS_PER_CANVAS_REGION * BLOCKS_PER_CANVAS_REGION];
    }

    public int getTextureId() {
        return textureId;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void ensureTexture() {
        if (textureId != -1) {
            return;
        }

        textureId = NacPainterUtils.createBlankTexture(BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION);
    }

    public void updateTexture() {
        ensureTexture();
        NacPainterUtils.updateTexture(textureId, BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION, pixels);
    }

    public void clearTexture() {
        if (textureId == -1) {
            return;
        }
        NacPainterUtils.deleteTexture(textureId);
        textureId = -1;
    }


    public void writeChunk(int cx, int cz, int[] chunkPixels) {
        int offsetX = cx * BLOCKS_PER_MINECRAFT_CHUNK;
        int offsetZ = cz * BLOCKS_PER_MINECRAFT_CHUNK;

        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            int destRow = (offsetZ + z) * BLOCKS_PER_CANVAS_REGION;
            int srcRow = z * BLOCKS_PER_MINECRAFT_CHUNK;
            System.arraycopy(chunkPixels, srcRow, pixels, destRow + offsetX, BLOCKS_PER_MINECRAFT_CHUNK);
        }
    }
}
