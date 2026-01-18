package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.waypoint.Waypoint;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.Constants;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

import static dev.natowb.natosatlas.core.utils.Constants.PIXELS_PER_CANVAS_CHUNK;

public class MapRenderer {

    public void render(MapContext ctx) {
        drawRegions(ctx);
        drawGrid(ctx);
        drawEntities(ctx);
        drawWaypoints(ctx);
    }

    private void drawRegions(MapContext ctx) {
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

        Set<Long> visible = new HashSet<>();

        for (int rx = startRegionX; rx <= endRegionX; rx++) {
            for (int rz = startRegionZ; rz <= endRegionZ; rz++) {
                MapRegionCoord coord = new MapRegionCoord(rx, rz);
                visible.add(coord.toKey());

                int texId = NatosAtlas.get().regionManager.getTexture(coord);
                if (texId != -1) {
                    drawRegionTexture(rx, rz, texId);
                }
            }
        }

        NatosAtlas.get().regionManager.updateCanvasVisibleRegions(visible);
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

    private void drawGrid(MapContext ctx) {
        if (!Settings.mapGrid) return;

        NatosAtlas.get().platform.painter.drawGrid(PIXELS_PER_CANVAS_CHUNK,
                ctx.canvasW, ctx.canvasH, ctx.scrollX, ctx.scrollY, ctx.zoom, 0xFF5b5b5b);
    }

    private void drawEntities(MapContext ctx) {
        if (Settings.entityDisplayMode == Settings.EntityDisplayMode.NONE) return;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NatosAtlas.get().platform.painter.getMinecraftTextureId("/misc/mapicons.png"));

        if (Settings.entityDisplayMode == Settings.EntityDisplayMode.ALL) {
            for (MapEntity e : NatosAtlas.get().platform.entityProvider.collectEntities()) {
                renderEntity(e, ctx.zoom);
            }
        }

        for (MapEntity p : NatosAtlas.get().platform.entityProvider.collectPlayers()) {
            renderEntity(p, ctx.zoom);
        }
    }

    private void drawWaypoints(MapContext ctx) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NatosAtlas.get().platform.painter.getMinecraftTextureId("/misc/mapicons.png"));
        for (Waypoint wp : Waypoints.getAll()) {
            renderEntity(new MapEntity(wp.x, wp.y, wp.z, 0, 4), ctx.zoom);
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


    private void renderEntity(MapEntity e, double zoom) {

        double worldX = e.x * Constants.PIXELS_PER_CANVAS_UNIT;
        double worldZ = e.z * Constants.PIXELS_PER_CANVAS_UNIT;

        float u1 = (e.iconIndex % 4) / 4.0f;
        float v1 = (e.iconIndex / 4) / 4.0f;
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
}
