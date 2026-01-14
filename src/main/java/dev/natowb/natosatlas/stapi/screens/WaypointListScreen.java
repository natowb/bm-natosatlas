package dev.natowb.natosatlas.stapi.screens;

import dev.natowb.natosatlas.core.NacWaypoints;
import dev.natowb.natosatlas.core.models.NacWaypoint;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.input.Keyboard;

public class WaypointListScreen extends Screen {

    private final Screen parent;
    private WaypointListWidget listWidget;

    public int selectedIndex = -1;

    private ButtonWidget deleteButton;
    private ButtonWidget editButton;

    public WaypointListScreen(Screen parent) {
        this.parent = parent;
    }
    @Override
    public void init() {
        super.init();
        Keyboard.enableRepeatEvents(true);
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        listWidget = new WaypointListWidget(this, mc);
        listWidget.registerButtons(this.buttons, 4, 5);

        int smallW = 50;
        int largeW = 100;
        int buttonH = 20;
        int gap = 10;
        int vGap = 5;

        int totalW = smallW + gap + largeW;
        int blockX = (width - totalW) / 2;

        int leftX = blockX;
        int rightX = blockX + smallW + gap;

        int bottomPadding = 20;
        int bottomY = height - bottomPadding - buttonH;
        int topY = bottomY - buttonH - vGap;

        editButton = new ButtonWidget(1003, leftX, topY, smallW, buttonH, "Edit");
        deleteButton = new ButtonWidget(1002, leftX, bottomY, smallW, buttonH, "Delete");

        editButton.active = false;
        deleteButton.active = false;

        ButtonWidget createButton = new ButtonWidget(1001, rightX, topY, largeW, buttonH, "Create");
        ButtonWidget backButton = new ButtonWidget(1000, rightX, bottomY, largeW, buttonH, "Back");

        this.buttons.add(editButton);
        this.buttons.add(deleteButton);
        this.buttons.add(createButton);
        this.buttons.add(backButton);
    }




    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void updateButtonStates() {
        boolean valid = selectedIndex >= 0;
        deleteButton.active = valid;
        editButton.active = valid;
    }

    public void openEditScreen(int index) {
        NacWaypoint selectedWaypoint = NacWaypoints.getAll().get(selectedIndex);
        minecraft.setScreen(new WaypointCreateScreen(this, selectedWaypoint));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 1000) {
            minecraft.setScreen(parent);
            return;
        }

        if (button.id == 1001) {
            minecraft.setScreen(new WaypointCreateScreen(this));
            return;
        }

        if (button.id == 1002 && selectedIndex >= 0) {
            NacWaypoint selectedWaypoint = NacWaypoints.getAll().get(selectedIndex);
            NacWaypoints.remove(selectedWaypoint);
            selectedIndex = -1;
            updateButtonStates();
            return;
        }

        if (button.id == 1003 && selectedIndex >= 0) {
            openEditScreen(selectedIndex);
            return;
        }

        listWidget.buttonClicked(button);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        listWidget.render(mouseX, mouseY, delta);
        drawCenteredTextWithShadow(textRenderer, "Waypoints", width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
}
