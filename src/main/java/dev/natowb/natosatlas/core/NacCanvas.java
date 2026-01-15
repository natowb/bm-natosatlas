package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.models.NacCanvasInfo;
import dev.natowb.natosatlas.core.models.NacEntity;
import dev.natowb.natosatlas.core.models.NacScaleInfo;
import dev.natowb.natosatlas.core.models.NacWaypoint;
import dev.natowb.natosatlas.core.painter.INacPainter;
import dev.natowb.natosatlas.core.utils.NacConstants;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import static dev.natowb.natosatlas.core.utils.NacConstants.*;


public class NacCanvas {
    private static final float REGION_THRESHOLD = 0.3f;
    private static final int CANVAS_PADDING = 0;
    private static final double MIN_ZOOM = 0.05;
    private static final double MAX_ZOOM = 4.0;

    private final int canvasX = CANVAS_PADDING;
    private final int canvasY = CANVAS_PADDING;

    private int canvasW = 0;
    private int canvasH = 0;

    private double scrollX = 0.0;
    private double scrollY = 0.0;
    private double zoom = 1.0;

    private int dragStartX = -1;
    private int dragStartY = -1;

    private int mouseX;
    private int mouseY;

    private boolean firstRun = true;


    public void handleInput() {
        handleInputs(mouseX, mouseY);
    }

    private void drawUnavailableMessage() {
        NacScaleInfo scale = NacPlatformAPI.get().getScaleInfo();

        String msg = "Only available in the Overworld";

        // Canvas center in screen space
        float centerX = canvasX + canvasW / 2f;
        float centerY = canvasY + canvasH / 2f;

        // Measure text width/height using your painter
        int textW = NacPlatformAPI.get().painter.getStringWidth(msg);
        int textH = 20;

        // Draw centered
        NacPlatformAPI.get().painter.drawString(
                msg,
                (int) (centerX - textW / 2f),
                (int) (centerY - textH / 2f),
                0xFFFFFFFF
        );
    }

    private void beginCanvas(NacScaleInfo scaleInfo) {
        NacPlatformAPI.get().painter.drawRect(canvasX, canvasY, canvasX + canvasW, canvasY + canvasH, 0xFF181818);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scale = scaleInfo.scaleFactor;

        GL11.glScissor(
                canvasX * scale,
                (scaleInfo.scaledHeight - (canvasY + canvasH)) * scale,
                canvasW * scale,
                canvasH * scale
        );

        GL11.glPushMatrix();
    }

    private void applyCanvasTransform() {
        GL11.glTranslatef(canvasX, canvasY, 0);
        GL11.glScalef((float) zoom, (float) zoom, 1f);
        GL11.glTranslatef((float) -scrollX, (float) -scrollY, 0);
    }

    private void endCanvas() {
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void draw(int mouseX, int mouseY, int screenW, int screenH) {
        updateWidthAndHeight(screenW, screenH);

        if (firstRun) {
            centerOnActiveChunk();
            firstRun = false;
        }

        this.mouseX = mouseX;
        this.mouseY = mouseY;


        beginCanvas(NacPlatformAPI.get().getScaleInfo());
        if (!NacPlatformAPI.get().getCurrentWorldInfo().isPlayerInOverworld) {
            drawUnavailableMessage();
            endCanvas();
            return;
        }
        applyCanvasTransform();

        NacCanvasInfo info = getInfo();
        drawRegionTiles(info);

        if (NacSettings.MAP_GRID.getValue()) {
            drawCanvasGrid(info);
        }
        if (NacSettings.ENTITY_DISPLAY_MODE.getValue() != NacSettings.EntityDisplayMode.NONE) {
            drawEntities(info);
        }

        drawWaypoints(info);


        endCanvas();

        if (NacSettings.DEBUG_INFO.getValue()) {
            drawDebugInfo(info);
        }
    }

    public NacCanvasInfo getInfo() {
        return new NacCanvasInfo(canvasW, canvasH, scrollX, scrollY, zoom, mouseX, mouseY);
    }


    private void handleInputs(int mouseX, int mouseY) {
        handleCanvasDragging(mouseX, mouseY);
        handleZooming(mouseX, mouseY);
    }

    private void handleCanvasDragging(int mouseX, int mouseY) {
        if (!Mouse.isButtonDown(0)) {
            dragStartX = dragStartY = -1;
            return;
        }
        if (dragStartX == -1) {
            dragStartX = mouseX;
            dragStartY = mouseY;
            return;
        }
        scrollX -= (mouseX - dragStartX) / zoom;
        scrollY -= (mouseY - dragStartY) / zoom;
        dragStartX = mouseX;
        dragStartY = mouseY;
    }

    private void handleZooming(int mouseX, int mouseY) {
        int wheel = Mouse.getDWheel();
        if (wheel == 0) return;

        double oldZoom = zoom;
        zoom *= (wheel > 0) ? 1.1 : 1 / 1.1;
        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));

        double localX = mouseX - canvasX;
        double localY = mouseY - canvasY;

        double worldX = scrollX + localX / oldZoom;
        double worldY = scrollY + localY / oldZoom;

        scrollX = worldX - localX / zoom;
        scrollY = worldY - localY / zoom;
    }

    public void updateWidthAndHeight(int width, int height) {
        canvasW = width - CANVAS_PADDING * 2;
        canvasH = height - CANVAS_PADDING * 2;
    }


    private void centerOnActiveChunk() {
        int chunkX = NAC.get().regionManager.getActiveChunkX();
        int chunkZ = NAC.get().regionManager.getActiveChunkZ();

        double centerBlockX = chunkX * 16 + 8;
        double centerBlockZ = chunkZ * 16 + 8;

        double px = centerBlockX * PIXELS_PER_CANVAS_UNIT;
        double pz = centerBlockZ * PIXELS_PER_CANVAS_UNIT;

        scrollX = px - (canvasW / zoom) / 2;
        scrollY = pz - (canvasH / zoom) / 2;

    }

    private void drawRegionTiles(NacCanvasInfo info) {
        double leftBlock = info.scrollX / PIXELS_PER_CANVAS_UNIT;
        double topBlock = info.scrollY / PIXELS_PER_CANVAS_UNIT;
        double rightBlock = (info.scrollX + info.width / info.zoom) / PIXELS_PER_CANVAS_UNIT;
        double bottomBlock = (info.scrollY + info.height / info.zoom) / PIXELS_PER_CANVAS_UNIT;

        int startChunkX = (int) Math.floor(leftBlock / BLOCKS_PER_MINECRAFT_CHUNK);
        int endChunkX = (int) Math.floor(rightBlock / BLOCKS_PER_MINECRAFT_CHUNK);
        int startChunkZ = (int) Math.floor(topBlock / BLOCKS_PER_MINECRAFT_CHUNK);
        int endChunkZ = (int) Math.floor(bottomBlock / BLOCKS_PER_MINECRAFT_CHUNK);

        int startRegionX = startChunkX / CHUNKS_PER_MINECRAFT_REGION - 1;
        int endRegionX = endChunkX / CHUNKS_PER_MINECRAFT_REGION + 1;
        int startRegionZ = startChunkZ / CHUNKS_PER_MINECRAFT_REGION - 1;
        int endRegionZ = endChunkZ / CHUNKS_PER_MINECRAFT_REGION + 1;

        for (int rx = startRegionX; rx <= endRegionX; rx++) {
            for (int rz = startRegionZ; rz <= endRegionZ; rz++) {

                int texId = NAC.get().regionManager.getTexture(rx, rz);
                if (texId == -1) continue;

                drawRegionTexture(rx, rz, texId);
            }
        }
    }

    private void drawRegionTexture(int regionX, int regionZ, int texId) {

        double worldX = regionX * CHUNKS_PER_MINECRAFT_REGION * BLOCKS_PER_MINECRAFT_CHUNK;
        double worldZ = regionZ * CHUNKS_PER_MINECRAFT_REGION * BLOCKS_PER_MINECRAFT_CHUNK;

        int drawX = (int) (worldX * PIXELS_PER_CANVAS_UNIT);
        int drawY = (int) (worldZ * PIXELS_PER_CANVAS_UNIT);

        NacPlatformAPI.get().painter.drawTexture(texId, drawX, drawY, PIXELS_PER_CANVAS_REGION, PIXELS_PER_CANVAS_REGION);
    }

    private static final double ICON_SIZE = 6.0;


    // FIXME: hacky but idc atm
    private void drawWaypoints(NacCanvasInfo info) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NacPlatformAPI.get().painter.getMinecraftTextureId("/misc/mapicons.png"));
        for (NacWaypoint wp : NacWaypoints.getAll()) {
            renderEntity(new NacEntity(wp.x, wp.z, 0, 4), info.zoom);
        }
        drawWaypointLabels(info);
    }

    public void drawWaypointLabels(NacCanvasInfo info) {
        for (NacWaypoint wp : NacWaypoints.getAll()) {
            double worldX = wp.x * NacConstants.PIXELS_PER_CANVAS_UNIT;
            double worldZ = wp.z * NacConstants.PIXELS_PER_CANVAS_UNIT;

            double scale = 1 / zoom;
            GL11.glPushMatrix();
            GL11.glTranslated(worldX, worldZ, 0);
            GL11.glScaled(scale, scale, 1);

            int nameLength = NacPlatformAPI.get().painter.getStringWidth(wp.name);

            NacPlatformAPI.get().painter.drawString(wp.name, -(nameLength / 2) + 1, 11, 0xFF000000);
            NacPlatformAPI.get().painter.drawString(wp.name, -(nameLength / 2), 10, 0xFFFFFFFF);
            GL11.glPopMatrix();

        }
    }


    private void drawEntities(NacCanvasInfo info) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NacPlatformAPI.get().painter.getMinecraftTextureId("/misc/mapicons.png"));

        if (NacSettings.ENTITY_DISPLAY_MODE.getValue() == NacSettings.EntityDisplayMode.ALL) {
            for (NacEntity e : NacPlatformAPI.get().entityProvider.collectEntities()) {
                renderEntity(e, info.zoom);
            }
        }

        for (NacEntity p : NacPlatformAPI.get().entityProvider.collectPlayers()) {
            renderEntity(p, info.zoom);
        }
    }


    private void renderEntity(NacEntity e, double zoom) {

        double worldX = e.x * NacConstants.PIXELS_PER_CANVAS_UNIT;
        double worldZ = e.z * NacConstants.PIXELS_PER_CANVAS_UNIT;

        float u1 = (e.iconIndex % 4) / 4.0f;
        float v1 = (e.iconIndex / 4) / 4.0f;
        float u2 = u1 + 0.25f;
        float v2 = v1 + 0.25f;

        double scale = ICON_SIZE / zoom;

        GL11.glPushMatrix();
        GL11.glTranslated(worldX, worldZ, 0);
        GL11.glRotated(e.yaw, 0, 0, 1);
        GL11.glScaled(scale, scale, 1);
        NacPlatformAPI.get().painter.drawTexturedQuad(u1, v1, u2, v2);
        GL11.glPopMatrix();
    }


    private void drawCanvasGrid(NacCanvasInfo info) {
        if (info.zoom < REGION_THRESHOLD) {
            NacPlatformAPI.get().painter.drawGrid(PIXELS_PER_CANVAS_REGION,
                    info.width, info.height, info.scrollX, info.scrollY, info.zoom, 0xFFFFFFFF);

        } else {
            NacPlatformAPI.get().painter.drawGrid(PIXELS_PER_CANVAS_CHUNK,
                    info.width, info.height, info.scrollX, info.scrollY, info.zoom, 0xFFFFFFFF);
        }
    }


    private void drawDebugInfo(NacCanvasInfo info) {
        renderDebugInfo(info);
        renderTooltip(info);
    }

    private void renderDebugInfo(NacCanvasInfo canvas) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        NacPlatformAPI.get().painter.drawString("Canvas", 5, 5, 0xFFFFFF);
        NacPlatformAPI.get().painter.drawString(String.format("Size: %d, %d", canvas.width, canvas.height), 5, 15, 0xFFFFFF);
        NacPlatformAPI.get().painter.drawString(String.format("Scroll: %.2f, %.2f", canvas.scrollX, canvas.scrollY), 5, 25, 0xFFFFFF);
        NacPlatformAPI.get().painter.drawString(String.format("Zoom: %.2f", canvas.zoom), 5, 35, 0xFFFFFF);

        NacPlatformAPI.get().painter.drawString("Region Cache", 5, 50, 0xFFFFFF);
        NacPlatformAPI.get().painter.drawString(String.format("Tile Count: %d", NAC.get().regionManager.getCacheSize()), 5, 60, 0xFFFFFF);
    }

    private void renderTooltip(NacCanvasInfo canvas) {
        double worldPixelX = canvas.scrollX + canvas.mouseX / canvas.zoom;
        double worldPixelZ = canvas.scrollY + canvas.mouseY / canvas.zoom;

        int blockX = (int) (worldPixelX / 8.0);
        int blockZ = (int) (worldPixelZ / 8.0);

        String blockCoords = "Block: " + blockX + ", " + blockZ;
        int tooltipX = canvas.mouseX + 12;
        int tooltipY = canvas.mouseY + 12;

        drawTooltip(NacPlatformAPI.get().painter, tooltipX, tooltipY, blockCoords);
    }

    private void drawTooltip(INacPainter painter, int x, int y, String line1) {
        int width = painter.getStringWidth(line1) + 6;
        int height = 14;

        int bgColor = 0xAA000000;
        int borderColor = 0xFFFFFFFF;

        painter.drawRect(x, y, x + width, y + height, bgColor);
        painter.drawRect(x, y, x + width, y + 1, borderColor);
        painter.drawRect(x, y + height - 1, x + width, y + height, borderColor);
        painter.drawRect(x, y, x + 1, y + height, borderColor);
        painter.drawRect(x + width - 1, y, x + width, y + height, borderColor);

        painter.drawString(line1, x + 3, y + 3, 0xFFFFFF);
    }

}
