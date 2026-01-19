package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.waypoint.Waypoint;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.Constants;
import org.lwjgl.opengl.GL11;

import java.util.Set;

import static dev.natowb.natosatlas.core.utils.Constants.PIXELS_PER_CANVAS_CHUNK;

public class MapPainter {

    public void drawRegions(Set<Long> visible) {
        for (long key : visible) {
            NACoord coord = NACoord.fromKey(key);
            int texId = NatosAtlas.get().textures.getTexture(coord);
            if (texId != -1) {
                drawRegionTexture(coord.x, coord.z, texId);
            }
        }
    }


    public void drawRegionTexture(int rx, int rz, int texId) {
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

    public void drawGrid(MapContext ctx) {
        if (!Settings.mapGrid) return;

        NatosAtlas.get().platform.painter.drawGrid(PIXELS_PER_CANVAS_CHUNK,
                ctx.canvasW, ctx.canvasH, ctx.scrollX, ctx.scrollY, ctx.zoom, 0xFF5b5b5b);
    }

    public void drawEntities(MapContext ctx) {
        if (Settings.entityDisplayMode == Settings.EntityDisplayMode.Nothing) return;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NatosAtlas.get().platform.painter.getMinecraftTextureId("/misc/mapicons.png"));

        if (Settings.entityDisplayMode == Settings.EntityDisplayMode.All) {
            for (NAEntity e : NatosAtlas.get().platform.worldProvider.getEntities()) {
                renderEntity(e, ctx.zoom);
            }
        }

        for (NAEntity p : NatosAtlas.get().platform.worldProvider.getPlayers()) {
            renderEntity(p, ctx.zoom);
        }
    }

    public void drawWaypoints(MapContext ctx) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NatosAtlas.get().platform.painter.getMinecraftTextureId("/misc/mapicons.png"));
        for (Waypoint wp : Waypoints.getAll()) {
            renderEntity(new NAEntity(wp.x, wp.y, wp.z, 0, NAEntity.NAEntityType.Waypoint), ctx.zoom);
        }

        for (Waypoint wp : Waypoints.getAll()) {
            double worldX = wp.x * Constants.PIXELS_PER_CANVAS_UNIT;
            double worldZ = wp.z * Constants.PIXELS_PER_CANVAS_UNIT;

            double scale = 1 / ctx.zoom;
            GL11.glPushMatrix();
            GL11.glTranslated(worldX, worldZ, 0);
            GL11.glScaled(scale, scale, 1);

            int nameLength = NatosAtlas.get().platform.painter.getStringWidth(wp.name);

            NatosAtlas.get().platform.painter.drawString(wp.name, -(nameLength / 2) + 1, 11, 0xFF000000);
            NatosAtlas.get().platform.painter.drawString(wp.name, -(nameLength / 2), 10, 0xFFFFFFFF);
            GL11.glPopMatrix();

        }
    }


    private void renderEntity(NAEntity e, double zoom) {

        double worldX = e.x * Constants.PIXELS_PER_CANVAS_UNIT;
        double worldZ = e.z * Constants.PIXELS_PER_CANVAS_UNIT;

        int iconIndex = 3;
        switch (e.type) {
            case Player: {
                iconIndex = 0;
                break;
            }
            case Mob: {
                iconIndex = 2;
                break;
            }
            case Animal: {
                iconIndex = 1;
                break;
            }
            case Waypoint: {
                iconIndex = 4;
            }

        }

        float u1 = (iconIndex % 4) / 4.0f;
        float v1 = (iconIndex / 4) / 4.0f;
        float u2 = u1 + 0.25f;
        float v2 = v1 + 0.25f;

        double scale = 6 / zoom;

        GL11.glPushMatrix();
        GL11.glTranslated(worldX, worldZ, 0);
        GL11.glRotated(e.yaw, 0, 0, 1);
        GL11.glScaled(scale, scale, 1);

        NatosAtlas.get().platform.painter.drawTexturedQuad(u1, v1, u2, v2);
        GL11.glPopMatrix();
    }


    public void drawFooterBar(MapContext ctx) {
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
        String shortcuts = "[Space] Center on Player  |  Drag: Move Map";

        int padding = 6;

        painter.drawString(blockInfo, x + padding, y + 6, 0xFFFFFF);

        int shortcutsWidth = painter.getStringWidth(shortcuts);
        painter.drawString(shortcuts, x + w - shortcutsWidth - padding, y + 6, 0xCCCCCC);
    }


    public void drawDebugInfo(MapContext ctx) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        PlatformPainter painter = NatosAtlas.get().platform.painter;
        int y = 5;

        painter.drawString("Canvas", 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Size: %d x %d", ctx.canvasW, ctx.canvasH), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Scroll: %.2f, %.2f", ctx.scrollX, ctx.scrollY), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Zoom: %.2f", ctx.zoom), 5, y, 0xFFFFFF);

        y += 15;
        painter.drawString("Cache", 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Total Cache Size: %d",
                NatosAtlas.get().cache.getCacheSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Dirty Queue Size: %d",
                NatosAtlas.get().cache.getDirtyQueueSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("PNG Cache Size: %d",
                NatosAtlas.get().cache.getPngCacheSize()), 5, y, 0xFFFFFF);
    }
}
