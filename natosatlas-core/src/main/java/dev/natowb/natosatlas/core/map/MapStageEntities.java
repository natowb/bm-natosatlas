package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.NatoAtlasConstants;
import dev.natowb.natosatlas.core.waypoint.Waypoint;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.access.WorldAccess;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class MapStageEntities implements MapStage {

    private static final double ENTITY_RENDER_RADIUS = 128.0;

    @Override
    public void draw(MapContext ctx, Set<Long> visibleRegions) {
        drawEntities(ctx);
        drawWaypoints(ctx);
    }

    private void drawEntities(MapContext ctx) {
        if (Settings.entityDisplayMode == Settings.EntityDisplayMode.Nothing) return;

        NAEntity player = WorldAccess.get().getPlayer();
        double px = player.x;
        double pz = player.z;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                PainterAccess.get().getMinecraftTextureId("/misc/mapicons.png"));

        if (Settings.entityDisplayMode == Settings.EntityDisplayMode.All) {
            for (NAEntity e : WorldAccess.get().getEntities()) {

                double dx = e.x - px;
                double dz = e.z - pz;

                if ((dx * dx + dz * dz) > (ENTITY_RENDER_RADIUS * ENTITY_RENDER_RADIUS)) {
                    continue;
                }

                renderEntity(ctx, e);
            }
        }

        for (NAEntity p : WorldAccess.get().getPlayers()) {

            double dx = p.x - px;
            double dz = p.z - pz;

            if ((dx * dx + dz * dz) > (ENTITY_RENDER_RADIUS * ENTITY_RENDER_RADIUS)) {
                continue;
            }

            renderMapMarker(ctx, p);
        }
    }

    private void drawWaypoints(MapContext ctx) {
        for (Waypoint wp : Waypoints.getAll()) {
            renderMapMarker(ctx,
                    new NAEntity(wp.x, wp.y, wp.z, 0, NAEntity.NAEntityType.Waypoint));
        }

        for (Waypoint wp : Waypoints.getAll()) {

            if (!wp.visible) continue;

            double x = wp.x * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
            double z = wp.z * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
            double s = 1 / ctx.zoom;

            drawUpright(ctx, x, z, s, 0, () -> {
                int w = PainterAccess.get().getStringWidth(wp.name);
                PainterAccess.get().drawString(wp.name, -(w / 2) + 1, 11, 0xFF000000);
                PainterAccess.get().drawString(wp.name, -(w / 2), 10, 0xFFFFFFFF);
            });
        }
    }

    private void renderEntity(MapContext ctx, NAEntity e) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                PainterAccess.get().getMinecraftTextureId(e.texturePath));

        double x = e.x * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
        double z = e.z * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
        double s = 4 / ctx.zoom;

        NAEntity.UV uv = NAEntity.getUV(e.texturePath);

        drawUpright(ctx, x, z, s, 180, () ->
                PainterAccess.get().drawTexturedQuad(uv.u1, uv.v1, uv.u2, uv.v2)
        );
    }

    private void renderMapMarker(MapContext ctx, NAEntity e) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                PainterAccess.get().getMinecraftTextureId("/misc/mapicons.png"));

        double x = e.x * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
        double z = e.z * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
        double s = 6 / ctx.zoom;

        int idx;
        switch (e.type) {
            case Player:
                idx = 0;
                break;
            case Animal:
                idx = 1;
                break;
            case Mob:
                idx = 2;
                break;
            case Waypoint:
                idx = 4;
                break;
            default:
                idx = 3;
                break;
        }

        float u1 = (idx % 4) / 4f;
        float v1 = (idx / 4) / 4f;
        float u2 = u1 + 0.25f;
        float v2 = v1 + 0.25f;

        if (e.type == NAEntity.NAEntityType.Player) {
            drawPlayerMarker(ctx, x, z, s, e.yaw, () ->
                    PainterAccess.get().drawTexturedQuad(u1, v1, u2, v2)
            );
        } else {
            drawUpright(ctx, x, z, s, e.yaw, () ->
                    PainterAccess.get().drawTexturedQuad(u1, v1, u2, v2)
            );
        }
    }


    private void drawPlayerMarker(MapContext ctx, double worldX, double worldZ, double scale, double yaw, Runnable draw) {
        GL11.glPushMatrix();
        GL11.glTranslated(worldX, worldZ, 0);
        GL11.glRotated(yaw, 0, 0, 1);   // only yaw, no unrotation
        GL11.glScaled(scale, scale, 1);
        draw.run();
        GL11.glPopMatrix();
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
