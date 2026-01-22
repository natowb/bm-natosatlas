package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.utils.Constants;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class MapViewport {

    private final MapContext ctx = new MapContext();

    private boolean dragging = false;
    private int dragStartX = -1;
    private int dragStartY = -1;

    private final Set<Long> visibleRegions = new HashSet<>();

    public void initViewport(int x, int y, int w, int h) {
        ctx.canvasX = x;
        ctx.canvasY = y;
        ctx.canvasW = w;
        ctx.canvasH = h;
    }

    public void updateMouse(int mouseX, int mouseY) {
        ctx.mouseX = mouseX;
        ctx.mouseY = mouseY;
    }

    public void dragStart(int mouseX, int mouseY) {
        dragging = true;
        dragStartX = mouseX;
        dragStartY = mouseY;
    }

    public void dragMove(int mouseX, int mouseY) {
        if (!dragging) return;

        ctx.scrollX -= (mouseX - dragStartX) / ctx.zoom;
        ctx.scrollY -= (mouseY - dragStartY) / ctx.zoom;

        dragStartX = mouseX;
        dragStartY = mouseY;
    }

    public void dragEnd() {
        dragging = false;
    }

    public void zoom(int amount) {
        if (amount == 0) return;

        float oldZoom = ctx.zoom;
        ctx.zoom *= (amount > 0) ? 1.1f : 1f / 1.1f;
        ctx.zoom = Math.max(MapConfig.MIN_ZOOM, Math.min(MapConfig.MAX_ZOOM, ctx.zoom));

        float localX = ctx.mouseX - ctx.canvasX;
        float localY = ctx.mouseY - ctx.canvasY;

        float worldX = ctx.scrollX + localX / oldZoom;
        float worldY = ctx.scrollY + localY / oldZoom;

        ctx.scrollX = worldX - localX / ctx.zoom;
        ctx.scrollY = worldY - localY / ctx.zoom;
    }

    public void centerOn(float pixelX, float pixelZ) {
        ctx.scrollX = pixelX - (ctx.canvasW / 2f) / ctx.zoom;
        ctx.scrollY = pixelZ - (ctx.canvasH / 2f) / ctx.zoom;
    }

    public void begin(UIScaleInfo scaleInfo) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        GL11.glScissor(
                ctx.canvasX * scaleInfo.scaleFactor,
                (scaleInfo.scaledHeight - (ctx.canvasY + ctx.canvasH)) * scaleInfo.scaleFactor,
                ctx.canvasW * scaleInfo.scaleFactor,
                ctx.canvasH * scaleInfo.scaleFactor
        );

        GL11.glPushMatrix();
        GL11.glTranslatef(ctx.canvasX, ctx.canvasY, 0);
        GL11.glScalef(ctx.zoom, ctx.zoom, 1);
        GL11.glTranslatef(-ctx.scrollX, -ctx.scrollY, 0);
    }

    public void end() {
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public Set<Long> computeVisibleRegions() {
        visibleRegions.clear();

        double leftBlock = ctx.scrollX / Constants.PIXELS_PER_CANVAS_UNIT;
        double topBlock = ctx.scrollY / Constants.PIXELS_PER_CANVAS_UNIT;
        double rightBlock = (ctx.scrollX + ctx.canvasW / ctx.zoom) / Constants.PIXELS_PER_CANVAS_UNIT;
        double bottomBlock = (ctx.scrollY + ctx.canvasH / ctx.zoom) / Constants.PIXELS_PER_CANVAS_UNIT;

        int startChunkX = (int) Math.floor(leftBlock / 16);
        int endChunkX = (int) Math.floor(rightBlock / 16);
        int startChunkZ = (int) Math.floor(topBlock / 16);
        int endChunkZ = (int) Math.floor(bottomBlock / 16);

        int startRegionX = startChunkX / 32 - 1;
        int endRegionX = endChunkX / 32 + 1;
        int startRegionZ = startChunkZ / 32 - 1;
        int endRegionZ = endChunkZ / 32 + 1;

        for (int rx = startRegionX; rx <= endRegionX; rx++) {
            for (int rz = startRegionZ; rz <= endRegionZ; rz++) {
                visibleRegions.add(new NACoord(rx, rz).toKey());
            }
        }

        return visibleRegions;
    }

    public MapContext getContext() {
        return ctx;
    }
}
