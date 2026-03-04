package dev.natowb.natosatlas.core.data;

import dev.natowb.natosatlas.client.texture.TextureUtils;

import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_CANVAS_REGION;

public class NARegionPixelData {

    private final int[] pixels = new int[BLOCKS_PER_CANVAS_REGION * BLOCKS_PER_CANVAS_REGION];
    private int textureId = -1;

    public NARegionPixelData() {
    }

    public int[] getPixels() {
        return pixels;
    }


    public int getTextureId() {
        if (textureId == -1) {
            textureId = TextureUtils.createBlankTexture(BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION);
            TextureUtils.updateTexture(textureId, BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION, pixels);
        }
        return textureId;
    }

    public void updateTexture() {
        if (textureId == -1) {
            textureId = TextureUtils.createBlankTexture(BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION);
        }

        TextureUtils.updateTexture(textureId, BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION, pixels);
    }

    public void deleteTexture() {
        if (textureId != -1) {
            TextureUtils.deleteTexture(textureId);
            textureId = -1;
        }
    }
}

