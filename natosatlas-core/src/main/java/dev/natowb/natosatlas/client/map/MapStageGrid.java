package dev.natowb.natosatlas.client.map;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.settings.Settings;
import org.lwjgl.opengl.GL11;

import java.util.Set;

import static dev.natowb.natosatlas.core.NAConstants.PIXELS_PER_CANVAS_CHUNK;

public class MapStageGrid implements MapStage {
    @Override
    public void draw(MapContext ctx, Set<Long> visibleRegions) {
        if (!Settings.mapGrid) return;

        drawGrid(PIXELS_PER_CANVAS_CHUNK, 0xFF5b5b5b, ctx);
    }


    public void drawGrid(double cellSize, int argbColor, MapContext ctx) {
        float a = (argbColor >> 24 & 255) / 255f;
        float r = (argbColor >> 16 & 255) / 255f;
        float g = (argbColor >> 8 & 255) / 255f;
        float b = (argbColor & 255) / 255f;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);

        float fCell = (float) cellSize;

        float pivotX = ctx.canvasW / 2f;
        float pivotY = ctx.canvasH / 2f;

        float left = ctx.scrollX + (0f - pivotX) / ctx.zoom;
        float top = ctx.scrollY + (0f - pivotY) / ctx.zoom;
        float right = ctx.scrollX + (ctx.canvasW - pivotX) / ctx.zoom;
        float bottom = ctx.scrollY + (ctx.canvasH - pivotY) / ctx.zoom;


        // FIXME: im too tired to actual redo all this grid logic for rotation
        //      the solution is probably simple but for now here is excessive padding.
        //      - @natowb a lazy POS
        int paddingCells = 50;
        float pad = paddingCells * fCell;

        left -= pad;
        right += pad;
        top -= pad;
        bottom += pad;

        float startX = (float) Math.floor(left / fCell) * fCell;
        float startY = (float) Math.floor(top / fCell) * fCell;

        float maxX = right + fCell;
        float maxY = bottom + fCell;


        for (float x = startX; x <= maxX; x += fCell) {
            NACore.getClient().getPlatform().painter.drawLine(
                    x, top - fCell,
                    x, bottom + fCell
            );
        }

        for (float y = startY; y <= maxY; y += fCell) {
            NACore.getClient().getPlatform().painter.drawLine(
                    left - fCell, y,
                    right + fCell, y
            );
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
