package dev.natowb.natosatlas.client.ui.hud;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.client.platform.NAPainter;
import dev.natowb.natosatlas.client.ui.elements.UIScaleInfo;
import dev.natowb.natosatlas.client.ui.screens.map.*;
import dev.natowb.natosatlas.client.ui.screens.settings.Settings;
import dev.natowb.natosatlas.client.ui.screens.waypoint.Waypoint;
import dev.natowb.natosatlas.client.ui.screens.waypoint.Waypoints;
import dev.natowb.natosatlas.core.NAConstants;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class MinimapRenderer {
    private final MapViewport viewport = new MapViewport();
    private final MapStageRegions stageRegions = new MapStageRegions();
    private final MapStageEntities stageEntities = new MapStageEntities();
    private final MapStageWaypoints stageWaypoints = new MapStageWaypoints();
    private int activeLayer = 0;

    private void chooseMinimapActiveLayer() {
        if (ClientWorldAccess.get().getWorldInfo().hasCeiling()) {
            activeLayer = 2;
        } else {
            switch (Settings.minimapRenderMode) {
                case Day:
                    activeLayer = 0;
                    break;
                case Night:
                    activeLayer = 1;
                    break;
                case Cave:
                    activeLayer = 2;
                    break;
                case Auto: {
                    long time = ClientWorldAccess.get().getWorldInfo().getTime() % 24000L;
                    boolean day = time < 12000L;

                    NAEntity player = ClientWorldAccess.get().getPlayer();
                    ChunkWrapper chunk = ClientWorldAccess.get().getChunk(
                            NACoord.from(player.chunkX, player.chunkZ)
                    );

                    int skyLight = chunk.getSkyLight(
                            player.localX,
                            (int) Math.floor(player.y),
                            player.localZ
                    );

                    if (skyLight == 0) {
                        activeLayer = 2;
                    } else {
                        activeLayer = day ? 0 : 1;
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    public void tick() {
        stageWaypoints.showLabels = false;
        stageEntities.setDisplayMode(Settings.minimapEntityDisplayMode);
        chooseMinimapActiveLayer();

        NAEntity player = ClientWorldAccess.get().getPlayer();
        if (player != null) {
            viewport.centerOn(
                    (float) player.x * NAConstants.PIXELS_PER_CANVAS_UNIT,
                    (float) player.z * NAConstants.PIXELS_PER_CANVAS_UNIT
            );

            if (Settings.minimapRotateWithPlayer) {
                double yaw = player.yaw;
                float rotation = (float) Math.toRadians(-(yaw - 180f));
                viewport.setRotation(rotation);
            } else {
                viewport.setRotation(0);
            }
        }
    }
    public void render(UIScaleInfo scaleInfo) {
        if (!Settings.minimapEnabled) return;
        setupViewport(scaleInfo);


        drawBackground();
        drawMinimap(scaleInfo);
        drawWaypointEdgeIndicators();
    }

    private void setupViewport(UIScaleInfo scaleInfo) {
        int viewportSize = 100;
        int minimapPadding = 10;
        int startX = scaleInfo.scaledWidth - viewportSize - minimapPadding;
        int startY = minimapPadding;
        viewport.initViewport(startX, startY, viewportSize, viewportSize);
        viewport.setZoom(Settings.minimapZoom);
    }
    private float[] worldToMinimap(MapContext ctx, float worldPx, float worldPz) {
        float halfW = ctx.canvasW * 0.5f;
        float halfH = ctx.canvasH * 0.5f;

        float localX = worldPx - ctx.scrollX;
        float localY = worldPz - ctx.scrollY;

        localX -= halfW;
        localY -= halfH;

        localX *= ctx.zoom;
        localY *= ctx.zoom;

        float cosR = (float) Math.cos(ctx.rotation);
        float sinR = (float) Math.sin(ctx.rotation);

        float rotX = localX * cosR - localY * sinR;
        float rotY = localX * sinR + localY * cosR;

        rotX += halfW;
        rotY += halfH;

        return new float[]{rotX, rotY};
    }

    private float[] raycastToEdge(MapContext ctx, float startX, float startY, float dirX, float dirY) {
        float bestT = Float.MAX_VALUE;
        float hitX = startX;
        float hitY = startY;

        if (dirX < 0) {
            float t = -startX / dirX;
            if (t > 0 && t < bestT) {
                bestT = t;
                hitX = startX + dirX * t;
                hitY = startY + dirY * t;
            }
        }

        if (dirX > 0) {
            float t = (ctx.canvasW - startX) / dirX;
            if (t > 0 && t < bestT) {
                bestT = t;
                hitX = startX + dirX * t;
                hitY = startY + dirY * t;
            }
        }

        if (dirY < 0) {
            float t = -startY / dirY;
            if (t > 0 && t < bestT) {
                bestT = t;
                hitX = startX + dirX * t;
                hitY = startY + dirY * t;
            }
        }

        if (dirY > 0) {
            float t = (ctx.canvasH - startY) / dirY;
            if (t > 0 && t < bestT) {
                hitX = startX + dirX * t;
                hitY = startY + dirY * t;
            }
        }

        return new float[]{hitX, hitY};
    }

    private void drawBackground() {
        MapContext ctx = viewport.getContext();
        NAPainter painter = NAClient.get().getPlatform().painter;
        int texId = painter.getMinecraftTextureId("/misc/mapbg.png");

        float scale = ctx.canvasW / 128f;
        int border = Math.round(7 * scale);

        int x1 = ctx.canvasX - border;
        int y1 = ctx.canvasY - border;
        int x2 = ctx.canvasX + ctx.canvasW + border;
        int y2 = ctx.canvasY + ctx.canvasH + border;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0f, 1f);
        GL11.glVertex2f(x1, y2);
        GL11.glTexCoord2f(1f, 1f);
        GL11.glVertex2f(x2, y2);
        GL11.glTexCoord2f(1f, 0f);
        GL11.glVertex2f(x2, y1);
        GL11.glTexCoord2f(0f, 0f);
        GL11.glVertex2f(x1, y1);
        GL11.glEnd();

    }

    private void drawMinimap(UIScaleInfo scaleInfo) {
        MapContext ctx = viewport.getContext();
        viewport.begin(scaleInfo);
        Set<Long> visible = viewport.computeVisibleRegions();
        stageRegions.draw(ctx, visible, activeLayer);
        stageEntities.draw(ctx, visible, activeLayer);
        stageWaypoints.draw(ctx, visible, activeLayer);
        viewport.end();
    }

    private void drawWaypointEdgeIndicators() {
        MapContext ctx = viewport.getContext();
        NAEntity player = ClientWorldAccess.get().getPlayer();
        if (player == null) return;

        float playerPx = (float) player.x * NAConstants.PIXELS_PER_CANVAS_UNIT;
        float playerPz = (float) player.z * NAConstants.PIXELS_PER_CANVAS_UNIT;

        float[] playerMap = worldToMinimap(ctx, playerPx, playerPz);
        float mapPlayerX = playerMap[0];
        float mapPlayerY = playerMap[1];

        for (Waypoint wp : Waypoints.getAll()) {
            if (!wp.visible) continue;

            float wpPx = wp.x * NAConstants.PIXELS_PER_CANVAS_UNIT;
            float wpPz = wp.z * NAConstants.PIXELS_PER_CANVAS_UNIT;

            float[] wpMap = worldToMinimap(ctx, wpPx, wpPz);
            float mapWpX = wpMap[0];
            float mapWpY = wpMap[1];

            if (mapWpX >= 0 && mapWpX <= ctx.canvasW && mapWpY >= 0 && mapWpY <= ctx.canvasH) {
                continue;
            }

            float dirX = mapWpX - mapPlayerX;
            float dirY = mapWpY - mapPlayerY;

            float[] edge = raycastToEdge(ctx, mapPlayerX, mapPlayerY, dirX, dirY);

            float screenX = ctx.canvasX + edge[0];
            float screenY = ctx.canvasY + edge[1];

            drawWaypointIconAt(screenX, screenY, wp);
        }
    }

    private void drawWaypointIconAt(float screenX, float screenY, Waypoint wp) {
        GL11.glPushMatrix();
        GL11.glTranslatef(screenX, screenY, 0);

        float iconScale = 6f;
        GL11.glScalef(iconScale, iconScale, 1);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                NAClient.get().getPlatform().painter.getMinecraftTextureId("/misc/mapicons.png"));

        int iconIndex = 4;

        float u1 = (iconIndex % 4) / 4f;
        float v1 = (iconIndex / 4) / 4f;
        float u2 = u1 + 0.25f;
        float v2 = v1 + 0.25f;

        int argb = 0xFF000000 | (wp.color & 0xFFFFFF);

        GL11.glTranslatef(-0.5f, -0.5f, 0);

        NAClient.get().getPlatform().painter.drawTexturedQuad(argb, u1, v1, u2, v2);

        GL11.glPopMatrix();
    }


}
