package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.NatoAtlasConstants;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.waypoint.Waypoint;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class MapStageWaypoints implements MapStage {

    @Override
    public void draw(MapContext ctx, Set<Long> visibleRegions) {
        drawWaypointIcons(ctx);
        drawWaypointLabels(ctx);
    }

    private void drawWaypointIcons(MapContext ctx) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                NACore.getClient().getPlatform().painter.getMinecraftTextureId("/misc/mapicons.png"));

        for (Waypoint wp : Waypoints.getAll()) {
            if (!wp.visible) continue;

            NAEntity e = new NAEntity(wp.x, wp.y, wp.z, 0, NAEntity.NAEntityType.Waypoint);
            renderWaypointMarker(ctx, e, wp);
        }
    }

    private void renderWaypointMarker(MapContext ctx, NAEntity e, Waypoint wp) {
        double x = e.x * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
        double z = e.z * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
        double s = 6 / ctx.zoom;

        int idx = 4;

        float u1 = (idx % 4) / 4f;
        float v1 = (idx / 4) / 4f;
        float u2 = u1 + 0.25f;
        float v2 = v1 + 0.25f;

        int argb = 0xFF000000 | (wp.color & 0xFFFFFF);

        drawUpright(ctx, x, z, s, 0, () ->
                NACore.getClient().getPlatform().painter.drawTexturedQuad(argb, u1, v1, u2, v2)
        );
    }


    private void drawWaypointLabels(MapContext ctx) {
        for (Waypoint wp : Waypoints.getAll()) {
            if (!wp.visible) continue;

            double x = wp.x * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
            double z = wp.z * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
            double s = 1 / ctx.zoom;

            drawUpright(ctx, x, z, s, 0, () -> {
                int w = NACore.getClient().getPlatform().painter.getStringWidth(wp.name);
                NACore.getClient().getPlatform().painter.drawString(wp.name, -(w / 2) + 1, 11, 0xFF000000);
                NACore.getClient().getPlatform().painter.drawString(wp.name, -(w / 2), 10, 0xFFFFFFFF);
            });
        }
    }

    private void drawUpright(MapContext ctx, double worldX, double worldZ, double scale, double yaw, Runnable draw) {
        GL11.glPushMatrix();
        GL11.glTranslated(worldX, worldZ, 0);
        GL11.glRotated(-Math.toDegrees(ctx.rotation), 0, 0, 1);
        if (yaw != 0) GL11.glRotated(yaw, 0, 0, 1);
        GL11.glScaled(scale, scale, 1);
        draw.run();
        GL11.glPopMatrix();
    }
}
