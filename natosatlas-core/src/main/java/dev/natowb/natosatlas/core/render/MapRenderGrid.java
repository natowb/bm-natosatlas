package dev.natowb.natosatlas.core.render;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.MapContext;
import dev.natowb.natosatlas.core.settings.Settings;

import java.util.Set;

import static dev.natowb.natosatlas.core.utils.Constants.PIXELS_PER_CANVAS_CHUNK;

public class MapRenderGrid implements MapRenderStage {
    @Override
    public void render(MapContext ctx, Set<Long> visibleRegions) {
        if (!Settings.mapGrid) return;

        NatosAtlas.get().platform.painter.drawGrid(PIXELS_PER_CANVAS_CHUNK,
                ctx.canvasW, ctx.canvasH, ctx.scrollX, ctx.scrollY, ctx.zoom, 0xFF5b5b5b);
    }
}
