package dev.natowb.natosatlas.client.texture;

import dev.natowb.natosatlas.core.data.NARegionPixelData;

import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_CANVAS_REGION;

public class NARegionTexture {

    private int textureId = -1;
    private final NARegionPixelData data;

    public NARegionTexture(NARegionPixelData data) {
        this.data = data;
    }

    public int getTextureId() {
        return textureId;
    }

    public void update() {
        if (textureId == -1) {
            textureId = TextureUtils.createBlankTexture(BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION);
        }

        TextureUtils.updateTexture(textureId, BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION, data.getPixels());
    }

    public void delete() {
        if (textureId != -1) {
            TextureUtils.deleteTexture(textureId);
            textureId = -1;
        }
    }
}
