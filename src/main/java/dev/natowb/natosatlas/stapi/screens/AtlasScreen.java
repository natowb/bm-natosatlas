package dev.natowb.natosatlas.stapi.screens;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;
import dev.natowb.natosatlas.core.NacCanvas;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

public class AtlasScreen extends Screen {

    private final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
    private final NacCanvas mapRenderer;

    private ButtonWidget settingsButton;
    private ButtonWidget waypointsButton;

    public AtlasScreen() {
        this.mapRenderer = new NacCanvas();
    }

    @Override
    public void init() {
        super.init();

        int buttonW = 80;
        int buttonH = 20;
        int x = 6;
        int vGap = 4;

        int settingsY = height - x - buttonH;
        int waypointsY = settingsY - buttonH - vGap;

        settingsButton = new ButtonWidget(1000, x, settingsY, buttonW, buttonH, "Settings");
        waypointsButton = new ButtonWidget(1002, x, waypointsY, buttonW, buttonH, "Waypoints");

        this.buttons.add(settingsButton);
        this.buttons.add(waypointsButton);
    }


    @Override
    public void render(int mouseX, int mouseY, float delta) {
        mapRenderer.draw(mouseX, mouseY, width, height);

        if (!settingsButton.isMouseOver(mc, mouseX, mouseY) && !waypointsButton.isMouseOver(mc, mouseX, mouseY)) {
            mapRenderer.handleInput();
        }

        if(NacPlatformAPI.get().getCurrentWorldInfo().isPlayerInOverworld) {
            super.render(mouseX, mouseY, delta);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {

        switch (button.id) {

            case 1000:
                minecraft.setScreen(new SettingsScreen(this));
                break;
            case 1002:
                minecraft.setScreen(new WaypointListScreen(this));
                break;

        }
    }
}
