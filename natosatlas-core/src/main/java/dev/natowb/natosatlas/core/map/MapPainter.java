package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NAWorldInfo;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.waypoint.Waypoint;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.Constants;
import org.lwjgl.opengl.GL11;

import java.util.Random;
import java.util.Set;

import static dev.natowb.natosatlas.core.utils.Constants.CHUNKS_PER_MINECRAFT_REGION;
import static dev.natowb.natosatlas.core.utils.Constants.PIXELS_PER_CANVAS_CHUNK;

public class MapPainter {

    public void drawRegions(Set<Long> visibleRegions) {
        for (long key : visibleRegions) {
            NACoord coord = NACoord.fromKey(key);
            int texId = NatosAtlas.get().textures.getTexture(coord);
            if (texId != -1) {
                drawRegionTexture(coord.x, coord.z, texId);
            }
        }
    }

    private boolean isSlimeChunk(int worldChunkX, int worldChunkZ) {
        NAWorldInfo info = NatosAtlas.get().platform.worldProvider.getWorldInfo();
        return new Random(info.worldSeed + (long) (worldChunkX * worldChunkZ * 4987142) +
                (long) (worldChunkX * 5947611) + (long) (worldChunkZ * worldChunkZ) * 4392871L +
                (long) (worldChunkZ * 389711) ^ 987234911L).nextInt(10) == 0;

    }

    public void drawSlimeChunks(Set<Long> visibleRegions) {
        if (!Settings.showSlimeChunks) return;

        for (long key : visibleRegions) {
            NACoord coord = NACoord.fromKey(key);
            int texId = NatosAtlas.get().textures.getTexture(coord);
            if (texId != -1) {
                for (int x = 0; x < CHUNKS_PER_MINECRAFT_REGION; x++) {
                    for (int z = 0; z < CHUNKS_PER_MINECRAFT_REGION; z++) {
                        int worldChunkX = coord.x * CHUNKS_PER_MINECRAFT_REGION + x;
                        int worldChunkZ = coord.z * CHUNKS_PER_MINECRAFT_REGION + z;
                        if (!isSlimeChunk(worldChunkX, worldChunkZ)) continue;
                        drawSlimeChunkSquare(worldChunkX, worldChunkZ);
                    }
                }
            }
        }
    }

    public void drawSlimeChunkSquare(int chunkX, int chunkZ) {
        double worldX = chunkX * 16;
        double worldZ = chunkZ * 16;

        int px = (int) (worldX * Constants.PIXELS_PER_CANVAS_UNIT);
        int pz = (int) (worldZ * Constants.PIXELS_PER_CANVAS_UNIT);

        int size = PIXELS_PER_CANVAS_CHUNK;

        int x1 = px + size;
        int y1 = pz + size;
        int x2 = px;
        int y2 = pz;

        NatosAtlas.get().platform.painter.drawRect(
                x1, y1, x2, y2,
                0x8000FF00
        );
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
