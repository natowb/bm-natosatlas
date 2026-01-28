package dev.natowb.natosatlas.client.map;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.client.texture.TextureProvider;

import java.util.Set;

import static dev.natowb.natosatlas.core.NAConstants.PIXELS_PER_CANVAS_REGION;
import static dev.natowb.natosatlas.core.NAConstants.PIXELS_PER_CANVAS_UNIT;

public class MapStageRegions implements MapStage {
    @Override
    public void draw(MapContext ctx, Set<Long> visibleRegions) {
        for (long key : visibleRegions) {
            NACoord coord = NACoord.fromKey(key);
            int texId = TextureProvider.getTexture(coord, NACore.getClient().getLayerController().getActiveLayer());
            if (texId != -1) {
                drawRegionTexture(coord.x, coord.z, texId);
            }
        }
    }

    private void drawRegionTexture(int rx, int rz, int texId) {
        double worldX = rx * 32 * 16;
        double worldZ = rz * 32 * 16;

        int px = (int) (worldX * PIXELS_PER_CANVAS_UNIT);
        int pz = (int) (worldZ * PIXELS_PER_CANVAS_UNIT);

        NACore.getClient().getPlatform().painter.drawTexture(texId, px, pz, PIXELS_PER_CANVAS_REGION, PIXELS_PER_CANVAS_REGION);
    }
}
