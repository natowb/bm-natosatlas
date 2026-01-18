package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.settings.SettingsScreen;
import dev.natowb.natosatlas.core.waypoint.WaypointListScreen;

public class MapScreen extends UIScreen {

    private final MapContext ctx = new MapContext();
    private final MapViewport viewport = new MapViewport();
    private final MapInputHandler input = new MapInputHandler();
    private final MapRenderer mapRenderer = new MapRenderer();
    private final MapOverlayRenderer overlayRenderer = new MapOverlayRenderer();

    private UIElementButton settingsButton;
    private UIElementButton waypointsButton;
    private UIElementButton closeButton;

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

        int padding = 6;
        int buttonW = 80;
        int buttonH = 20;
        int vGap = 4;

        int settingsY = height - padding - buttonH;
        int waypointsY = settingsY - buttonH - vGap;

        settingsButton = new UIElementButton(1000, padding, settingsY, buttonW, buttonH, "Settings");
        waypointsButton = new UIElementButton(1002, padding, waypointsY, buttonW, buttonH, "Waypoints");

        int closeSize = 16;
        closeButton = new UIElementButton(
                9999,
                width - closeSize - padding,
                padding,
                closeSize,
                closeSize,
                "X"
        );
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        ctx.mouseX = mouseX;
        ctx.mouseY = mouseY;

        NatosAtlas.get().platform.painter.drawRect(ctx.canvasX, ctx.canvasY, ctx.canvasW, ctx.canvasH, UITheme.PANEL_BG);

        viewport.begin(ctx, NatosAtlas.get().platform.getScaleInfo().scaleFactor,
                NatosAtlas.get().platform.getScaleInfo().scaledHeight);

        mapRenderer.render(ctx);

        viewport.end();

        overlayRenderer.render(ctx);


        boolean hoveringUI = isHovering(settingsButton, mouseX, mouseY) || isHovering(waypointsButton, mouseX, mouseY)
                || isHovering(closeButton, mouseX, mouseY);

        if (!hoveringUI) {
            input.handle(ctx);
        }

        settingsButton.render(mouseX, mouseY);
        waypointsButton.render(mouseX, mouseY);
        closeButton.render(mouseX, mouseY);
    }

    private boolean isHovering(UIElementButton b, int mx, int my) {
        return (mx >= b.x && mx <= b.x + b.w) && (my >= b.y && my <= b.y + b.h);
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
        }

        if (waypointsButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.openNacScreen(new WaypointListScreen(this));
        }
    }

    @Override
    public void resetAllButtonsClickState() {
        settingsButton.resetClickState();
        waypointsButton.resetClickState();
        closeButton.resetClickState();
    }
}
