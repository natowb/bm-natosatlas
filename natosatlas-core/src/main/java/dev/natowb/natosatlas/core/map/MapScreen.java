package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.screens.HelpScreen;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.settings.SettingsScreen;
import dev.natowb.natosatlas.core.io.SaveWorker;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.ui.layout.UIHorizontalLayout;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
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

    private UIElementIconButton closeButton;
    private UIElementIconButton settingsButton;
    private UIElementIconButton waypointsButton;
    private UIElementIconButton slimeChunksButton;
    private UIElementIconButton modeButton;
    private UIElementIconButton helpButton;


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


        UILayout layout = new UIHorizontalLayout(5, 15, 5);


        closeButton = new UIElementIconButton(101, layout, 20, 20, 4);
        addButton(closeButton);

        settingsButton = new UIElementIconButton(102, layout, 20, 20, 3);
        addButton(settingsButton);

        waypointsButton = new UIElementIconButton(103, layout, 20, 20, 2);
        waypointsButton.setColor(0xFFFF0000);
        addButton(waypointsButton);

        slimeChunksButton = new UIElementIconButton(104, layout, 20, 20, 0);
        if (!Settings.showSlimeChunks) {
            slimeChunksButton.setIcon(1);
        }
        addButton(slimeChunksButton);

        modeButton = new UIElementIconButton(105, layout, 20, 20, 5);

        Settings.MapRenderMode m = Settings.mapRenderMode;
        switch (m) {
            case Day:
                modeButton.setIcon(5);
                break;
            case Night:
                modeButton.setIcon(6);
                break;
            case Cave:
                modeButton.setIcon(7);
                break;
            case Auto:
                modeButton.setIcon(8);
                break;
        }

        addButton(modeButton);

        helpButton = new UIElementIconButton(106, layout, 20, 20, 11);
        addButton(helpButton);
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
            switch (Settings.mapRenderMode) {
                case Day:
                    Settings.mapRenderMode = Settings.MapRenderMode.Night;
                    break;
                case Night:
                    Settings.mapRenderMode = Settings.MapRenderMode.Cave;
                    break;
                case Cave:
                    Settings.mapRenderMode = Settings.MapRenderMode.Auto;
                    break;
                case Auto:
                    Settings.mapRenderMode = Settings.MapRenderMode.Day;
                    break;
            }

            switch (Settings.mapRenderMode) {
                case Day:
                    modeButton.setIcon(5);
                    break;
                case Night:
                    modeButton.setIcon(6);
                    break;
                case Cave:
                    modeButton.setIcon(7);
                    break;
                case Auto:
                    modeButton.setIcon(8);
                    break;
            }
            Settings.save();
            return;
        }

        if (btn.id == slimeChunksButton.id) {
            Settings.showSlimeChunks = !Settings.showSlimeChunks;
            if (Settings.showSlimeChunks) {
                ((UIElementIconButton) btn).setIcon(0);
            } else {
                ((UIElementIconButton) btn).setIcon(1);
            }
            Settings.save();
        }

        if (btn.id == helpButton.id) {
            NatosAtlasCore.get().platform.openNacScreen(new HelpScreen(this));
            return;
        }
    }

    private void renderDebugInfo(MapContext ctx) {
        if (!Settings.debugInfo) return;


        GL11.glEnable(GL11.GL_TEXTURE_2D);

        int y = 25;
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
