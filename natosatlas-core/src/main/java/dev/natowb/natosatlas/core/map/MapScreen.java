package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.settings.SettingsOption;
import dev.natowb.natosatlas.core.settings.SettingsScreen;
import dev.natowb.natosatlas.core.io.SaveWorker;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementOptionButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.waypoint.WaypointListScreen;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.access.WorldAccess;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class MapScreen extends UIScreen {

    private final MapViewport viewport = new MapViewport();
    private final PainterAccess painter = PainterAccess.get();

    private final MapStageRegions regionPainter = new MapStageRegions();
    private final MapStageSlime slimePainter = new MapStageSlime();
    private final MapStageGrid gridPainter = new MapStageGrid();
    private final MapStageEntities entitiesPainter = new MapStageEntities();

    private UIElementButton settingsButton;
    private UIElementOptionButton modeButton;
    private UIElementOptionButton slimeChunksButton;
    private UIElementButton waypointsButton;
    private UIElementButton closeButton;

    public MapScreen(UIScreen parent) {
        super(parent);
        viewport.setZoom(Settings.defaultZoom);
        NAEntity player = WorldAccess.get().getPlayer();
        if (player != null) {
            viewport.centerOn((float) player.x * 8f, (float) player.z * 8f);
        }
        Waypoints.load();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        viewport.initViewport(0, 0, width, height);


        int padding = 6;
        int buttonW = 80;
        int buttonH = 20;
        int hGap = 6;

        closeButton = new UIElementButton(9999, width - buttonH - padding, padding, buttonH, buttonH, "X");
        addButton(closeButton);

        settingsButton = new UIElementButton(1000, closeButton.x - hGap - buttonW, padding, buttonW, buttonH, "Settings");
        addButton(settingsButton);

        waypointsButton = new UIElementButton(1002, settingsButton.x - hGap - buttonW, padding, buttonW, buttonH, "Waypoints");
        addButton(waypointsButton);

        modeButton = new UIElementOptionButton(SettingsOption.MAP_RENDER_MODE, waypointsButton.x - hGap - buttonW, padding, buttonW, buttonH);

        if (WorldAccess.get().hasCeiling()) {
            modeButton.active = false;
            modeButton.label = "Mode: Cave";
        }

        addButton(modeButton);

        slimeChunksButton = new UIElementOptionButton(SettingsOption.SLIME_CHUNKS, modeButton.x - hGap - buttonW, padding, buttonW, buttonH);
        addButton(slimeChunksButton);
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

        viewport.end();

        renderDebugInfo(ctx);
        renderFooter(ctx);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }

    @Override
    public void mouseDown(int x, int y, int button) {
        super.mouseDown(x, y, button);
        if (button == 0) viewport.dragStart(x, y);
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

    @Override
    protected void onClick(UIElementButton btn) {
        if (btn.id == closeButton.id) {
            NatosAtlasCore.get().platform.openNacScreen(parent);
            return;
        }

        if (btn.id == settingsButton.id) {
            NatosAtlasCore.get().platform.openNacScreen(new SettingsScreen(this));
            return;
        }

        if (btn.id == waypointsButton.id) {
            NatosAtlasCore.get().platform.openNacScreen(new WaypointListScreen(this));
            return;
        }

        if (btn.id == modeButton.id) {
            modeButton.cycle();
            Settings.save();
            return;
        }

        if (btn.id == slimeChunksButton.id) {
            slimeChunksButton.cycle();
            Settings.save();
        }
    }

    private void renderDebugInfo(MapContext ctx) {
        if (!Settings.debugInfo) return;

        GL11.glEnable(GL11.GL_TEXTURE_2D);

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
        painter.drawString(String.format("Pending Saves: %d", SaveWorker.getPendingCount()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Total Cache Size: %d", NatosAtlasCore.get().cache.getCacheSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("Dirty Queue Size: %d", NatosAtlasCore.get().cache.getDirtyQueueSize()), 5, y, 0xFFFFFF);
        y += 10;
        painter.drawString(String.format("PNG Cache Size: %d", NatosAtlasCore.get().cache.getPngCacheSize()), 5, y, 0xFFFFFF);
    }

    private void renderFooter(MapContext ctx) {
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
        String shortcuts = "[LM Drag] Pan | [RM Drag] Rotate | [Space] Reset Viewport";

        int padding = 6;

        painter.drawString(blockInfo, x + padding, y + 6, 0xFFFFFF);

        int shortcutsWidth = painter.getStringWidth(shortcuts);
        painter.drawString(shortcuts, x + w - shortcutsWidth - padding, y + 6, 0xCCCCCC);
    }
}
