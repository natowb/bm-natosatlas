package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.platform.PlatformPainterUtils;

import static dev.natowb.natosatlas.core.utils.Constants.*;

public class MapRegion {

    private int textureId = -1;
    private final int[] pixels = new int[BLOCKS_PER_CANVAS_REGION * BLOCKS_PER_CANVAS_REGION];

    public MapRegion() {

    }

    public int[] getPixels() {
        return pixels;
    }


    public int getTextureId() {
        return textureId;
    }

    public void ensureTexture() {
        if (textureId != -1) return;
        textureId = PlatformPainterUtils.createBlankTexture(BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION);
    }

    public void clearTexture() {
        if (textureId == -1) return;
        PlatformPainterUtils.deleteTexture(textureId);
        textureId = -1;
    }

    public void updateTexture() {
        ensureTexture();
        PlatformPainterUtils.updateTexture(textureId, BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION, pixels);
    }

}

