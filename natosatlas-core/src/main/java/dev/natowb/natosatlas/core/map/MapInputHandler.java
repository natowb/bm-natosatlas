package dev.natowb.natosatlas.core.map;

import org.lwjgl.input.Mouse;

public class MapInputHandler {

    private int dragStartX = -1;
    private int dragStartY = -1;

    public void handle(MapContext ctx) {
        handleDrag(ctx);
        handleZoom(ctx);
    }

    private void handleDrag(MapContext ctx) {
        if (!Mouse.isButtonDown(0)) {
            dragStartX = dragStartY = -1;
            return;
        }

        if (dragStartX == -1) {
            dragStartX = ctx.mouseX;
            dragStartY = ctx.mouseY;
            return;
        }

        ctx.scrollX -= (ctx.mouseX - dragStartX) / ctx.zoom;
        ctx.scrollY -= (ctx.mouseY - dragStartY) / ctx.zoom;

        dragStartX = ctx.mouseX;
        dragStartY = ctx.mouseY;
    }

    private void handleZoom(MapContext ctx) {
        int wheel = Mouse.getDWheel();
        if (wheel == 0) return;

        float oldZoom = ctx.zoom;
        ctx.zoom *= (wheel > 0) ? 1.1f : 1 / 1.1f;
        ctx.zoom = Math.max(MapConfig.MIN_ZOOM, Math.min(MapConfig.MAX_ZOOM, ctx.zoom));

        float localX = ctx.mouseX - ctx.canvasX;
        float localY = ctx.mouseY - ctx.canvasY;

        float worldX = ctx.scrollX + localX / oldZoom;
        float worldY = ctx.scrollY + localY / oldZoom;

        ctx.scrollX = worldX - localX / ctx.zoom;
        ctx.scrollY = worldY - localY / ctx.zoom;
    }
}

