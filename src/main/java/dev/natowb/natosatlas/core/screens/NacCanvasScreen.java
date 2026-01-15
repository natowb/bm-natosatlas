package dev.natowb.natosatlas.core.screens;

import dev.natowb.natosatlas.core.NacCanvas;
import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.gui.NacGuiButton;

public class NacCanvasScreen extends NacScreen {

    private final NacCanvas mapRenderer = new NacCanvas();

    private NacGuiButton settingsButton;
    private NacGuiButton waypointsButton;
    private NacGuiButton closeButton;

    public NacCanvasScreen(NacScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int buttonW = 80;
        int buttonH = 20;
        int x = 6;
        int vGap = 4;

        int settingsY = height - x - buttonH;
        int waypointsY = settingsY - buttonH - vGap;

        settingsButton = new NacGuiButton(1000, x, settingsY, buttonW, buttonH, "Settings");
        waypointsButton = new NacGuiButton(1002, x, waypointsY, buttonW, buttonH, "Waypoints");

        int closeSize = 16;
        int padding = 6;
        closeButton = new NacGuiButton(
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
        mapRenderer.draw(mouseX, mouseY, width, height);

        boolean hoveringSettings =
                mouseX >= settingsButton.x && mouseX <= settingsButton.x + settingsButton.w &&
                        mouseY >= settingsButton.y && mouseY <= settingsButton.y + settingsButton.h;

        boolean hoveringWaypoints =
                mouseX >= waypointsButton.x && mouseX <= waypointsButton.x + waypointsButton.w &&
                        mouseY >= waypointsButton.y && mouseY <= waypointsButton.y + waypointsButton.h;

        boolean hoveringClose =
                mouseX >= closeButton.x && mouseX <= closeButton.x + closeButton.w &&
                        mouseY >= closeButton.y && mouseY <= closeButton.y + closeButton.h;

        if (!hoveringSettings && !hoveringWaypoints && !hoveringClose) {
            mapRenderer.handleInput();
        }

        if (NacPlatformAPI.get().getCurrentWorldInfo().isPlayerInOverworld) {
            settingsButton.render(mouseX, mouseY);
            waypointsButton.render(mouseX, mouseY);
            closeButton.render(mouseX, mouseY);
        }
    }

    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        if (closeButton.handleClick(mouseX, mouseY)) {
            NacPlatformAPI.get().openNacScreen(parent);
            return;
        }

        if (settingsButton.handleClick(mouseX, mouseY)) {
            NacPlatformAPI.get().openNacScreen(new NacSettingsScreen(this));
        }

        if (waypointsButton.handleClick(mouseX, mouseY)) {
            NacPlatformAPI.get().openNacScreen(new NacWaypointListScreen(this));
        }
    }

    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {
        // nothing special
    }

    @Override
    public void resetAllButtonsClickState() {
        settingsButton.resetClickState();
        waypointsButton.resetClickState();
        closeButton.resetClickState();
    }
}
