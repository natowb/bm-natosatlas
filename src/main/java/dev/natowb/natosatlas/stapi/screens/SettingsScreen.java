package dev.natowb.natosatlas.stapi.screens;

import dev.natowb.natosatlas.core.NACSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class SettingsScreen extends Screen {

    private final Screen parent;
    private ButtonWidget entityModeButton;
    private ButtonWidget showGridButton;
    private ButtonWidget showDebugButton;

    public SettingsScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();

        int buttonW = 200;
        int buttonH = 20;
        int vGap = 10;

        int bx = (width - buttonW) / 2;

        int totalH = (buttonH * 3) + (vGap * 2);
        int by = (height - totalH) / 2;

        String entityModeLabel = "Entities: " + NACSettings.getEntityDisplayMode().getLabel();
        entityModeButton = new ButtonWidget(1, bx, by, buttonW, buttonH, entityModeLabel);
        String gridLabel = NACSettings.isMapGridEnabled() ? "Grid: On" : "Grid: Off";
        showGridButton = new ButtonWidget(2, bx, by + buttonH + vGap, buttonW, buttonH, gridLabel);
        String debugLabel = NACSettings.isMapDebugInfoEnabled() ? "Debug: On" : "Debug: Off";
        showDebugButton = new ButtonWidget(3, bx, by + (buttonH + vGap) * 2, buttonW, buttonH, debugLabel);
        ButtonWidget backButton = new ButtonWidget(4, bx, by + (buttonH + vGap) * 3, buttonW, buttonH, "Back");
        buttons.add(entityModeButton);
        buttons.add(showGridButton);
        buttons.add(showDebugButton);
        buttons.add(backButton);
    }


    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 1:
                entityModeButton.text = "Entities: " + NACSettings.cycleEntityDisplayMode().getLabel();
                break;

            case 2:
                showGridButton.text = NACSettings.toggleMapGrid() ? "Grid: On" : "Grid: Off";
                break;
            case 3:
                showDebugButton.text = NACSettings.toggleDebugInfo() ?"Debug: On" : "Debug: Off";
                break;
            case 4:
                minecraft.setScreen(parent);
                break;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);
    }
}
