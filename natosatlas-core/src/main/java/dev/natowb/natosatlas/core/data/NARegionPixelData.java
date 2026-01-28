package dev.natowb.natosatlas.core.data;

import static dev.natowb.natosatlas.core.NAConstants.BLOCKS_PER_CANVAS_REGION;

public class NARegionPixelData {

    private final int[] pixels = new int[BLOCKS_PER_CANVAS_REGION * BLOCKS_PER_CANVAS_REGION];

    public NARegionPixelData() {
    }

    public int[] getPixels() {
        return pixels;
    }
}

