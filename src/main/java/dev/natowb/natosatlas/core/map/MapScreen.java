package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.settings.SettingsOption;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementOptionButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.settings.SettingsScreen;
import dev.natowb.natosatlas.core.waypoint.WaypointListScreen;
import org.lwjgl.input.Keyboard;

public class MapScreen extends UIScreen {

    private final MapContext ctx = new MapContext();
    private final MapViewport viewport = new MapViewport();
    private final MapRenderer mapRenderer = new MapRenderer();
    private final MapOverlayRenderer overlayRenderer = new MapOverlayRenderer();

    private UIElementButton settingsButton;
    private UIElementOptionButton dayNightButton;
    private UIElementButton waypointsButton;
    private UIElementButton closeButton;

    private boolean dragging = false;
    private int dragStartX = -1;
    private int dragStartY = -1;

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

        MapEntity player = NatosAtlas.get().platform.entityProvider.getLocalPlayer();
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
    }


    @Override
    public void render(int mouseX, int mouseY, float delta) {
        ctx.mouseX = mouseX;
        ctx.mouseY = mouseY;

        NatosAtlas.get().platform.painter.drawRect(
                ctx.canvasX, ctx.canvasY, ctx.canvasW, ctx.canvasH, UITheme.ELEMENT_BG
        );

        viewport.begin(ctx,
                NatosAtlas.get().platform.getScaleInfo().scaleFactor,
                NatosAtlas.get().platform.getScaleInfo().scaledHeight
        );

        mapRenderer.render(ctx);

        viewport.end();

        overlayRenderer.render(ctx);

        settingsButton.render(mouseX, mouseY);
        waypointsButton.render(mouseX, mouseY);
        closeButton.render(mouseX, mouseY);
        dayNightButton.render(mouseX, mouseY);
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

        switch (keyCode) {
            case Keyboard.KEY_Q:
                zoomAtCenter(1f / 1.1f);
                break;

            case Keyboard.KEY_E:
                zoomAtCenter(1.1f);
                break;

            case Keyboard.KEY_P:
                NatosAtlas.get().regionManager.exportLayers();
                break;

            case Keyboard.KEY_SPACE: {
                MapEntity player = NatosAtlas.get().platform.entityProvider.getLocalPlayer();
                if (player != null) {
                    float playerPixelX = (float) player.x * 8f;
                    float playerPixelZ = (float) player.z * 8f;
                    ctx.scrollX = playerPixelX - (ctx.canvasW / 2f) / ctx.zoom;
                    ctx.scrollY = playerPixelZ - (ctx.canvasH / 2f) / ctx.zoom;

                }
                break;
            }
        }
    }


    private void zoomAtCenter(float factor) {
        float oldZoom = ctx.zoom;
        ctx.zoom *= factor;
        ctx.zoom = Math.max(MapConfig.MIN_ZOOM, Math.min(MapConfig.MAX_ZOOM, ctx.zoom));

        float centerX = ctx.canvasW / 2f;
        float centerY = ctx.canvasH / 2f;

        float worldX = ctx.scrollX + centerX / oldZoom;
        float worldY = ctx.scrollY + centerY / oldZoom;

        ctx.scrollX = worldX - centerX / ctx.zoom;
        ctx.scrollY = worldY - centerY / ctx.zoom;
    }

    @Override
    public void resetAllButtonsClickState() {
        settingsButton.resetClickState();
        waypointsButton.resetClickState();
        closeButton.resetClickState();
        dayNightButton.resetClickState();
    }
}
