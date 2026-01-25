package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatoAtlasConstants;
import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.screens.HelpScreen;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.settings.SettingsOption;
import dev.natowb.natosatlas.core.settings.SettingsScreen;
import dev.natowb.natosatlas.core.io.SaveWorker;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.ui.layout.UIHorizontalLayout;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.waypoint.WaypointCreateScreen;
import dev.natowb.natosatlas.core.waypoint.WaypointListScreen;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.access.WorldAccess;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Set;

import static dev.natowb.natosatlas.core.texture.TextureProvider.*;

public class MapScreen extends UIScreen {

    private final MapViewport viewport = new MapViewport();
    private final PainterAccess painter = PainterAccess.get();

    private final MapStageRegions regionPainter = new MapStageRegions();
    private final MapStageSlime slimePainter = new MapStageSlime();
    private final MapStageGrid gridPainter = new MapStageGrid();
    private final MapStageEntities entitiesPainter = new MapStageEntities();

    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME_THRESHOLD = 300;

    public MapScreen(UIScreen parent) {
        super(parent);
        viewport.setZoom(Settings.defaultZoom);
        Waypoints.load();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        viewport.initViewport(0, 0, width, height);
        NAEntity player = WorldAccess.get().getPlayer();
        if (player != null) {
            viewport.centerOn((float) player.x * 8f, (float) player.z * 8f);
        }

        UILayout leftLayout = new UIHorizontalLayout(5, 15, 5, false);


        UIElementIconButton closeButton = new UIElementIconButton(101, leftLayout, 20, 20, ICON_CROSS);
        closeButton.setHandler(btn -> NatosAtlasCore.get().platform.openNacScreen(parent));
        closeButton.setTooltip("Close");
        addButton(closeButton);

        UIElementIconButton settingsButton = new UIElementIconButton(102, leftLayout, 20, 20, ICON_COG);
        settingsButton.setHandler(btn -> NatosAtlasCore.get().platform.openNacScreen(new SettingsScreen(this)));
        settingsButton.setTooltip("Settings");

        addButton(settingsButton);

        UIElementIconButton helpButton = new UIElementIconButton(106, leftLayout, 20, 20, ICON_HELP);
        helpButton.setHandler(btn -> NatosAtlasCore.get().platform.openNacScreen(new HelpScreen(this)));
        helpButton.setTooltip("Help");
        addButton(helpButton);


        UILayout rightLayout = new UIHorizontalLayout(width - 25, 15, 5, true);


        UIElementIconButton waypointsButton = new UIElementIconButton(103, rightLayout, 20, 20, ICON_WAYPOINTS);
        waypointsButton.setHandler(btn -> NatosAtlasCore.get().platform.openNacScreen(new WaypointListScreen(this)));
        waypointsButton.setTooltip("Waypoints");
        addButton(waypointsButton);

        UIElementIconButton slimeChunksButton = new UIElementIconButton(104, rightLayout, 20, 20, ICON_SLIME_ENABLED);
        if (!Settings.showSlimeChunks) {
            slimeChunksButton.setIcon(ICON_SLIME_DISABLED);
        }
        slimeChunksButton.setHandler(btn -> {
            Settings.showSlimeChunks = !Settings.showSlimeChunks;
            if (Settings.showSlimeChunks) {
                ((UIElementIconButton) btn).setIcon(ICON_SLIME_ENABLED);
            } else {
                ((UIElementIconButton) btn).setIcon(ICON_SLIME_DISABLED);
            }
            Settings.save();
        });
        slimeChunksButton.setTooltip("Toggle Slime Chunks");
        addButton(slimeChunksButton);

        UIElementIconButton gridButton = new UIElementIconButton(107, rightLayout, 20, 20, ICON_GRID_ENABLED);
        gridButton.setTooltip("Toggle Grid");

        if (!Settings.mapGrid) {
            gridButton.setIcon(ICON_GRID_DISABLED);
        }

        gridButton.setHandler(btn -> {
            Settings.mapGrid = !Settings.mapGrid;
            if (Settings.mapGrid) {
                ((UIElementIconButton) btn).setIcon(ICON_GRID_ENABLED);
            } else {
                ((UIElementIconButton) btn).setIcon(ICON_GRID_DISABLED);
            }
            Settings.save();
        });
        addButton(gridButton);


        UIElementIconButton entityButton = new UIElementIconButton(105, rightLayout, 20, 20, 5);

        entityButton.setTooltip(String.format("Entities: %s", Settings.entityDisplayMode.name()));
        switch (Settings.entityDisplayMode) {
            case Player:
                entityButton.setIcon(ICON_ENTITY_PLAYER);
                break;
            case All:
                entityButton.setIcon(ICON_ENTITY_ALL);
                break;
            case Nothing:
                entityButton.setIcon(ICON_ENTITY_NONE);
                break;
        }

        entityButton.setHandler(btn -> {
            SettingsOption.ENTITY_DISPLAY.cycle();
            entityButton.setTooltip(String.format("Entities: %s", Settings.entityDisplayMode.name()));
            switch (Settings.entityDisplayMode) {
                case Player:
                    entityButton.setIcon(ICON_ENTITY_PLAYER);
                    break;
                case All:
                    entityButton.setIcon(ICON_ENTITY_ALL);
                    break;
                case Nothing:
                    entityButton.setIcon(ICON_ENTITY_NONE);
                    break;
            }
            Settings.save();
        });
        addButton(entityButton);


        if (!WorldAccess.get().hasCeiling()) {
            UIElementIconButton modeButton = new UIElementIconButton(105, rightLayout, 20, 20, 5);
            modeButton.setTooltip(String.format("Mode: %s", Settings.mapRenderMode.name()));

            switch (Settings.mapRenderMode) {
                case Day:
                    modeButton.setIcon(ICON_DAY);
                    break;
                case Night:
                    modeButton.setIcon(ICON_NIGHT);
                    break;
                case Cave:
                    modeButton.setIcon(ICON_CAVE);
                    break;
                case Auto:
                    modeButton.setIcon(ICON_AUTO);
                    break;
            }

            modeButton.setHandler(btn -> {
                if (!WorldAccess.get().hasCeiling()) {
                    SettingsOption.MAP_RENDER_MODE.cycle();
                    modeButton.setTooltip(String.format("Mode: %s", Settings.mapRenderMode.name()));

                    switch (Settings.mapRenderMode) {
                        case Day:
                            modeButton.setIcon(ICON_DAY);
                            break;
                        case Night:
                            modeButton.setIcon(ICON_NIGHT);
                            break;
                        case Cave:
                            modeButton.setIcon(ICON_CAVE);
                            break;
                        case Auto:
                            modeButton.setIcon(ICON_AUTO);
                            break;
                    }
                    Settings.save();
                }
            });
            addButton(modeButton);
        }


    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        painter.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        viewport.updateMouse(mouseX, mouseY);
        viewport.begin(scaleInfo);

        MapContext ctx = viewport.getContext();
        Set<Long> visible = viewport.computeVisibleRegions();

        regionPainter.draw(ctx, visible);
        slimePainter.draw(ctx, visible);
        gridPainter.draw(ctx, visible);
        entitiesPainter.draw(ctx, visible);
        renderMouseBlockHighlight(ctx);
        viewport.end();


        renderDebugInfo(ctx);
        renderMouseTooltip(ctx);
        super.render(mouseX, mouseY, delta, scaleInfo);
    }

    public void renderMouseBlockHighlight(MapContext ctx) {
        NACoord bc = getMouseBlock(ctx);

        double px = (bc.x - 1) * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;
        double pz = (bc.z - 1) * NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;

        double size = NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1f, 1f, 0f, 0.5f);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2d(px, pz);
        GL11.glVertex2d(px + size, pz);
        GL11.glVertex2d(px + size, pz + size);
        GL11.glVertex2d(px, pz + size);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }


    private void renderMouseTooltip(MapContext ctx) {
        NACoord bc = getMouseBlock(ctx);
        String text = "(" + bc.x + ", " + bc.z + ")";

        PainterAccess p = PainterAccess.get();

        int padding = 4;
        int textW = p.getStringWidth(text);
        int textH = 8;

        int tx = (width - textW) / 2;
        int ty = height - textH - padding - 6;

        p.drawRect(tx - padding, ty - padding, tx + textW + padding, ty + textH + padding, 0xF0100010);
        p.drawRect(tx - padding, ty - padding, tx + textW + padding, ty - padding + 1, 0x505000FF);
        p.drawRect(tx - padding, ty + textH + padding - 1, tx + textW + padding, ty + textH + padding, 0x505000FF);

        p.drawStringWithShadow(text, tx, ty, 0xFFFFFFFF);
    }


    public NACoord getMouseBlock(MapContext ctx) {

        float pivotX = ctx.canvasW / 2f;
        float pivotY = ctx.canvasH / 2f;

        float x = ctx.mouseX - ctx.canvasX;
        float y = ctx.mouseY - ctx.canvasY;

        x -= pivotX;
        y -= pivotY;

        float cos = (float) Math.cos(-ctx.rotation);
        float sin = (float) Math.sin(-ctx.rotation);

        float rx = x * cos - y * sin;
        float ry = x * sin + y * cos;

        rx /= ctx.zoom;
        ry /= ctx.zoom;

        rx += pivotX;
        ry += pivotY;

        float worldX = ctx.scrollX + rx;
        float worldZ = ctx.scrollY + ry;

        int blockX = (int) Math.floor(worldX / NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT) + 1;
        int blockZ = (int) Math.floor(worldZ / NatoAtlasConstants.PIXELS_PER_CANVAS_UNIT) + 1;

        return new NACoord(blockX, blockZ);
    }

    @Override
    public void mouseDown(int x, int y, int button) {
        super.mouseDown(x, y, button);

        if (button == 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime <= DOUBLE_CLICK_TIME_THRESHOLD) {
                MapContext ctx = viewport.getContext();
                NACoord blockCoord = getMouseBlock(ctx);
                NatosAtlasCore.get().platform.openNacScreen(new WaypointCreateScreen(this, blockCoord.x, blockCoord.z));
                return;
            }
            lastClickTime = currentTime;
            viewport.dragStart(x, y);
        }
        if (button == 1) viewport.rotateStart(x, y);
    }


    @Override
    public void mouseDrag(int x, int y, int button) {
        super.mouseDrag(x, y, button);
        if (button == 0) viewport.dragMove(x, y);
        if (button == 1) viewport.rotateMove(x, y);
    }

    @Override
    public void mouseUp(int x, int y, int button) {
        super.mouseUp(x, y, button);
        if (button == 0) viewport.dragEnd();
        if (button == 1) viewport.rotateEnd();
    }

    @Override
    public void mouseScroll(int amount) {
        viewport.zoom(amount);
    }

    @Override
    public void keyPressed(char character, int keyCode) {
        super.keyPressed(character, keyCode);

        if (keyCode == Keyboard.KEY_SPACE) {
            viewport.setRotation(0);
            NAEntity player = WorldAccess.get().getPlayer();
            if (player != null) {
                viewport.centerOn((float) player.x * 8f, (float) player.z * 8f);
            }
        }

        if (keyCode == Keyboard.KEY_P) {
            MapExporter.exportAllLayers();
        }
    }

    private void renderDebugInfo(MapContext ctx) {
        if (!Settings.debugInfo) return;


        GL11.glEnable(GL11.GL_TEXTURE_2D);

        int y = 30;
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
        painter.drawString(String.format("Pending Saves: %d", SaveWorker.getPendingCount()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Total Cache Size: %d", NatosAtlasCore.get().cache.getCacheSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Dirty Queue Size: %d", NatosAtlasCore.get().cache.getDirtyQueueSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("PNG Cache Size: %d", NatosAtlasCore.get().cache.getPngCacheSize()), 5, y, 0xFFFFFF);
    }
}
