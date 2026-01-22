package dev.natowb.natosatlas.core.render;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.map.MapContext;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.Constants;
import dev.natowb.natosatlas.core.waypoint.Waypoint;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class MapRenderEntities implements MapRenderStage {
    @Override
    public void render(MapContext ctx, Set<Long> visibleRegions) {
        drawEntities(ctx);
        drawWaypoints(ctx);
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
            renderMapMarker(p, ctx.zoom);
        }
    }

    public void drawWaypoints(MapContext ctx) {
        for (Waypoint wp : Waypoints.getAll()) {
            renderMapMarker(new NAEntity(wp.x, wp.y, wp.z, 0, NAEntity.NAEntityType.Waypoint), ctx.zoom);
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
        GL11.glBindTexture(
                GL11.GL_TEXTURE_2D,
                NatosAtlas.get().platform.painter.getMinecraftTextureId(e.texturePath)
        );

        double worldX = e.x * Constants.PIXELS_PER_CANVAS_UNIT;
        double worldZ = e.z * Constants.PIXELS_PER_CANVAS_UNIT;

        NAEntity.UV uv = NAEntity.getUV(e.texturePath);

        double scale = 6 / zoom;

        GL11.glPushMatrix();
        GL11.glTranslated(worldX, worldZ, 0);
        GL11.glRotated(180, 0, 0, 1);
        GL11.glScaled(scale, scale, 1);

        NatosAtlas.get().platform.painter.drawTexturedQuad(
                uv.u1, uv.v1, uv.u2, uv.v2
        );

        GL11.glPopMatrix();
    }

    private void renderMapMarker(NAEntity e, double zoom) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, NatosAtlas.get().platform.painter.getMinecraftTextureId("/misc/mapicons.png"));
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


}
