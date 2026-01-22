package dev.natowb.natosatlas.core.render;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.map.MapContext;
import dev.natowb.natosatlas.core.utils.Constants;

import java.util.Set;

public class MapRenderRegions implements MapRenderStage {
    @Override
    public void render(MapContext ctx, Set<Long> visibleRegions) {
        for (long key : visibleRegions) {
            NACoord coord = NACoord.fromKey(key);
            int texId = NatosAtlas.get().textures.getTexture(coord);
            if (texId != -1) {
                drawRegionTexture(coord.x, coord.z, texId);
            }
        }
    }


    private void drawRegionTexture(int rx, int rz, int texId) {
        double worldX = rx * 32 * 16;
        double worldZ = rz * 32 * 16;

        int px = (int) (worldX * Constants.PIXELS_PER_CANVAS_UNIT);
        int pz = (int) (worldZ * Constants.PIXELS_PER_CANVAS_UNIT);

        NatosAtlas.get().platform.painter.drawTexture(
                texId, px, pz,
                Constants.PIXELS_PER_CANVAS_REGION,
                Constants.PIXELS_PER_CANVAS_REGION
        );
    }
}
