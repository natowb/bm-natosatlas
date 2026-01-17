package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.ui.UITheme;
import org.lwjgl.opengl.GL11;

public class MapOverlayRenderer {

    public void render(MapContext ctx) {
        if (Settings.debugInfo) {
            renderDebugInfo(ctx);
        }
        renderBottomBar(ctx);
    }

    private void renderBottomBar(MapContext ctx) {
        PlatformPainter painter = NatosAtlas.get().platform.painter;

        int barHeight = 20;
        int x = ctx.canvasX;
        int y = ctx.canvasY + ctx.canvasH - barHeight;
        int w = ctx.canvasW;
        int h = barHeight;

        painter.drawRect(x, y, x + w, y + h, UITheme.ELEMENT_BG);

        double worldPixelX = ctx.scrollX + ctx.mouseX / ctx.zoom;
        double worldPixelZ = ctx.scrollY + ctx.mouseY / ctx.zoom;

        int blockX = (int) (worldPixelX / 8.0);
        int blockZ = (int) (worldPixelZ / 8.0);


        String blockInfo = "Block: " + blockX + ", " + blockZ;
        String shortcuts = "[Q/E] Zoom  |  [Space] Center on Player  |  Drag: Move Map";

        int padding = 6;

        painter.drawString(blockInfo, x + padding, y + 6, 0xFFFFFF);

        int shortcutsWidth = painter.getStringWidth(shortcuts);
        painter.drawString(shortcuts, x + w - shortcutsWidth - padding, y + 6, 0xCCCCCC);
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

        y += 15;
        painter.drawString("Cache", 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Total Cache Size: %d",
                manager.getTotalCacheSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Dirty Queue Size: %d",
                manager.getTotalDirtyQueueSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("PNG Cache Size: %d",
                manager.getTotalPngCacheSize()), 5, y, 0xFFFFFF);
    }

}
