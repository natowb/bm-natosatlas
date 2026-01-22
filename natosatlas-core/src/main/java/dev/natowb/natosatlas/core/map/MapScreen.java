package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.render.*;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.settings.SettingsOption;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementOptionButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.settings.SettingsScreen;
import dev.natowb.natosatlas.core.utils.Constants;
import dev.natowb.natosatlas.core.waypoint.WaypointListScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class MapScreen extends UIScreen {

    private final MapContext ctx = new MapContext();
    private final MapViewport viewport = new MapViewport();

    private UIElementButton settingsButton;
    private UIElementOptionButton dayNightButton;
    private UIElementOptionButton slimeChunksButton;
    private UIElementButton waypointsButton;
    private UIElementButton closeButton;

    private boolean dragging = false;
    private int dragStartX = -1;
    private int dragStartY = -1;
    private final Set<Long> visibleRegions = new HashSet<>();

    public MapScreen(UIScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        ctx.canvasX = 0;
        ctx.canvasY = 0;
        ctx.canvasW = width;
        ctx.canvasH = height;

        NAEntity player = NatosAtlas.get().platform.worldProvider.getPlayer();
        if (player != null) {
            float playerPixelX = (float) player.x * 8f;
            float playerPixelZ = (float) player.z * 8f;
            ctx.scrollX = playerPixelX - (ctx.canvasW / 2f) / ctx.zoom;
            ctx.scrollY = playerPixelZ - (ctx.canvasH / 2f) / ctx.zoom;

        }

        int padding = 6;

        int buttonW = 80;
        int buttonH = 20;
        int hGap = 6;

        closeButton = new UIElementButton(
                9999,
                width - buttonH - padding,
                padding,
                buttonH,
                buttonH,
                "X"
        );

        settingsButton = new UIElementButton(
                1000,
                closeButton.x - hGap - buttonW,
                padding,
                buttonW,
                buttonH,
                "Settings"
        );

        waypointsButton = new UIElementButton(
                1002,
                settingsButton.x - hGap - buttonW,
                padding,
                buttonW,
                buttonH,
                "Waypoints"
        );

        dayNightButton = new UIElementOptionButton(
                1003,
                waypointsButton.x - hGap - buttonW,
                padding,
                buttonW,
                buttonH,
                SettingsOption.MAP_RENDER_MODE
        );

        slimeChunksButton = new UIElementOptionButton(
                1004,
                dayNightButton.x - hGap - buttonW,
                padding,
                buttonW,
                buttonH,
                SettingsOption.SLIME_CHUNKS
        );
    }

    @Override
    public void tick() {
        if (!NatosAtlas.get().isEnabled()) return;
        visibleRegions.clear();
        computeVisibleRegions(ctx, visibleRegions);
        NatosAtlas.get().updateCanvasVisibleRegions(visibleRegions);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        ctx.mouseX = mouseX;
        ctx.mouseY = mouseY;

        NatosAtlas.get().platform.painter.drawRect(
                ctx.canvasX, ctx.canvasY, ctx.canvasW, ctx.canvasH, UITheme.ELEMENT_BG
        );

        if (!NatosAtlas.get().isEnabled()) {
            String msg = "NatosAtlas is not enabled, check console for errors";
            int textW = NatosAtlas.get().platform.painter.getStringWidth(msg);
            int textX = ctx.canvasX + (ctx.canvasW - textW) / 2;
            int textY = ctx.canvasY + ctx.canvasH / 2;

            NatosAtlas.get().platform.painter.drawString(msg, textX, textY, UITheme.TITLE_TEXT);
            return;
        }

        viewport.begin(ctx, scaleInfo);
        new MapRenderRegions().render(ctx, visibleRegions);
        new MapRenderSlimeChunks().render(ctx, visibleRegions);
        new MapRenderGrid().render(ctx, visibleRegions);
        new MapRenderEntities().render(ctx, visibleRegions);
        viewport.end();

        if (Settings.debugInfo) {
            renderDebugInfo();
        }

        renderButtons(mouseX, mouseY);
        renderFooter();
    }


    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        if (closeButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.openNacScreen(parent);
            return;
        }
        if (settingsButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.openNacScreen(new SettingsScreen(this));
            return;
        }
        if (waypointsButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.openNacScreen(new WaypointListScreen(this));
            return;
        }

        if (dayNightButton.handleClick(mouseX, mouseY)) {
            dayNightButton.cycle();
            Settings.save();
            return;
        }

        if (slimeChunksButton.handleClick(mouseX, mouseY)) {
            slimeChunksButton.cycle();
            Settings.save();
            return;
        }

        dragging = true;
        dragStartX = mouseX;
        dragStartY = mouseY;
    }

    @Override
    public void mouseDrag(int mouseX, int mouseY, int button) {
        if (!dragging || button != 0) return;

        ctx.scrollX -= (mouseX - dragStartX) / ctx.zoom;
        ctx.scrollY -= (mouseY - dragStartY) / ctx.zoom;

        dragStartX = mouseX;
        dragStartY = mouseY;
    }

    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {
        if (button == 0) dragging = false;
    }

    @Override
    public void mouseScroll(int amount) {
        if (amount == 0) return;

        float oldZoom = ctx.zoom;
        ctx.zoom *= (amount > 0) ? 1.1f : 1f / 1.1f;
        ctx.zoom = Math.max(MapConfig.MIN_ZOOM, Math.min(MapConfig.MAX_ZOOM, ctx.zoom));

        float localX = ctx.mouseX - ctx.canvasX;
        float localY = ctx.mouseY - ctx.canvasY;

        float worldX = ctx.scrollX + localX / oldZoom;
        float worldY = ctx.scrollY + localY / oldZoom;

        ctx.scrollX = worldX - localX / ctx.zoom;
        ctx.scrollY = worldY - localY / ctx.zoom;
    }

    @Override
    public void keyPressed(char character, int keyCode) {
        super.keyPressed(character, keyCode);
        if (keyCode == Keyboard.KEY_SPACE) {
            NAEntity player = NatosAtlas.get().platform.worldProvider.getPlayer();
            if (player != null) {
                float playerPixelX = (float) player.x * 8f;
                float playerPixelZ = (float) player.z * 8f;
                ctx.scrollX = playerPixelX - (ctx.canvasW / 2f) / ctx.zoom;
                ctx.scrollY = playerPixelZ - (ctx.canvasH / 2f) / ctx.zoom;

            }
        }
    }


    @Override
    public void resetAllButtonsClickState() {
        settingsButton.resetClickState();
        waypointsButton.resetClickState();
        closeButton.resetClickState();
        dayNightButton.resetClickState();
        slimeChunksButton.resetClickState();
    }

    private void renderButtons(int mouseX, int mouseY) {
        settingsButton.render(mouseX, mouseY);
        waypointsButton.render(mouseX, mouseY);
        closeButton.render(mouseX, mouseY);
        dayNightButton.render(mouseX, mouseY);
        slimeChunksButton.render(mouseX, mouseY);
    }

    private void renderDebugInfo() {
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

    private void renderFooter() {
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


    private void computeVisibleRegions(MapContext ctx, Set<Long> visibleRegions) {
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

        for (int rx = startRegionX; rx <= endRegionX; rx++) {
            for (int rz = startRegionZ; rz <= endRegionZ; rz++) {
                visibleRegions.add(new NACoord(rx, rz).toKey());
            }
        }
    }

}
