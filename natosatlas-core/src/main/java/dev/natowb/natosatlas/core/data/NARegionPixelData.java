package dev.natowb.natosatlas.core.data;

import dev.natowb.natosatlas.client.texture.TextureUtils;

import static dev.natowb.natosatlas.core.NAConstants.*;

public class NARegionPixelData {

    private int textureId = -1;
    private final int[] pixels = new int[BLOCKS_PER_CANVAS_REGION * BLOCKS_PER_CANVAS_REGION];

    public NARegionPixelData() {

    }

    public int[] getPixels() {
        return pixels;
    }


    public int getTextureId() {
        return textureId;
    }

    public void ensureTexture() {
        if (textureId != -1) return;
        textureId = TextureUtils.createBlankTexture(BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION);
    }

    public void deleteTexture() {
        if (textureId == -1) return;
        TextureUtils.deleteTexture(textureId);
        textureId = -1;
    }

    public void updateTexture() {
        ensureTexture();
        TextureUtils.updateTexture(textureId, BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION, pixels);
    }

}

