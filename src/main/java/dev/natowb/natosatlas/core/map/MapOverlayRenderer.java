package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import org.lwjgl.opengl.GL11;

public class MapOverlayRenderer {

    public void render(MapContext ctx) {
        renderTooltip(ctx);
        renderDebugInfo(ctx);
    }


    private void renderDebugInfo(MapContext ctx) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        PlatformPainter painter = NatosAtlas.get().platform.painter;
        MapManager manager = NatosAtlas.get().regionManager;

        int y = 5;

        painter.drawString("Canvas", 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Size: %d x %d", ctx.canvasW, ctx.canvasH), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Scroll: %.2f, %.2f", ctx.scrollX, ctx.scrollY), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Zoom: %.2f", ctx.zoom), 5, y, 0xFFFFFF);

        y += 15;
        painter.drawString("Region Manager", 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Active Chunk: %d, %d",
                manager.getActiveChunkX(), manager.getActiveChunkZ()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Loaded Regions (Player): %d",
                manager.getLoadedRegionCount()), 5, y, 0xFFFFFF);

    }

    public void renderTooltip(MapContext ctx) {
        PlatformPainter painter = NatosAtlas.get().platform.painter;
        double worldPixelX = ctx.scrollX + ctx.mouseX / ctx.zoom;
        double worldPixelZ = ctx.scrollY + ctx.mouseY / ctx.zoom;

        int blockX = (int) (worldPixelX / 8.0);
        int blockZ = (int) (worldPixelZ / 8.0);

        String blockCoords = "Block: " + blockX + ", " + blockZ;
        int tooltipX = ctx.mouseX + 12;
        int tooltipY = ctx.mouseY + 12;


        int width = painter.getStringWidth(blockCoords) + 6;
        int height = 14;

        int bgColor = 0xAA000000;
        int borderColor = 0xFFFFFFFF;

        painter.drawRect(tooltipX, tooltipY, tooltipX + width, tooltipY + height, bgColor);
        painter.drawRect(tooltipX, tooltipY, tooltipX + width, tooltipY + 1, borderColor);
        painter.drawRect(tooltipX, tooltipY + height - 1, tooltipX + width, tooltipY + height, borderColor);
        painter.drawRect(tooltipX, tooltipY, tooltipX + 1, tooltipY + height, borderColor);
        painter.drawRect(tooltipX + width - 1, tooltipY, tooltipX + width, tooltipY + height, borderColor);
        painter.drawString(blockCoords, tooltipX + 3, tooltipY + 3, 0xFFFFFF);
    }


}
