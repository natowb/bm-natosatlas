package dev.natowb.natosatlas.core.models;

import dev.natowb.natosatlas.core.painter.NacPainterUtils;
import dev.natowb.natosatlas.core.renderer.NacRegionRenderer;

import static dev.natowb.natosatlas.core.utils.NacConstants.*;

public class NacRegionData {

    private final int regionX;
    private final int regionZ;

    private final NacChunk[][] chunks = new NacChunk[CHUNKS_PER_MINECRAFT_REGION][CHUNKS_PER_MINECRAFT_REGION];

    private int textureId = -1;
    private final int[] pixels = new int[BLOCKS_PER_CANVAS_REGION * BLOCKS_PER_CANVAS_REGION];
    private boolean dirtyPixels = true;

    public NacRegionData(int regionX, int regionZ) {
        this.regionX = regionX;
        this.regionZ = regionZ;
    }

    public int getRegionX() {
        return regionX;
    }

    public int getRegionZ() {
        return regionZ;
    }

    public void writeChunk(int cx, int cz, NacChunk chunk) {
        chunks[cx][cz] = chunk;
        dirtyPixels = true;
    }

    public NacChunk getChunk(int cx, int cz) {
        return chunks[cx][cz];
    }

    public int getTextureId() {
        return textureId;
    }

    public void ensureTexture() {
        if (textureId != -1) return;

        textureId = NacPainterUtils.createBlankTexture(
                BLOCKS_PER_CANVAS_REGION,
                BLOCKS_PER_CANVAS_REGION
        );
    }

    public void clearTexture() {
        if (textureId == -1) return;

        NacPainterUtils.deleteTexture(textureId);
        textureId = -1;
    }

    public void updateTexture(NacRegionRenderer renderer) {
        ensureTexture();

        if (!dirtyPixels) return;

        renderer.buildPixels(this, pixels);

        NacPainterUtils.updateTexture(
                textureId,
                BLOCKS_PER_CANVAS_REGION,
                BLOCKS_PER_CANVAS_REGION,
                pixels
        );

        dirtyPixels = false;
    }

    public void markDirty() {
        dirtyPixels = true;
    }
}
