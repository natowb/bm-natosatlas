package dev.natowb.natosatlas.stapi.screens;

import dev.natowb.natosatlas.core.NacWaypoints;
import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacEntity;
import dev.natowb.natosatlas.core.models.NacWaypoint;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.input.Keyboard;

public class WaypointCreateScreen extends Screen {

    private final Screen parent;
    private final boolean editMode;
    private final NacWaypoint editing;

    private TextFieldWidget nameField;
    private TextFieldWidget xField;
    private TextFieldWidget yField;
    private TextFieldWidget zField;

    private ButtonWidget actionButton;
    private ButtonWidget cancelButton;

    public WaypointCreateScreen(Screen parent) {
        this.parent = parent;
        this.editMode = false;
        this.editing = null;
    }

    public WaypointCreateScreen(Screen parent, NacWaypoint waypoint) {
        this.parent = parent;
        this.editMode = true;
        this.editing = waypoint;
    }

    @Override
    public void init() {
        super.init();
        Keyboard.enableRepeatEvents(true);

        int panelW = 260;
        int panelH = 160;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        nameField = new TextFieldWidget(this, textRenderer, px + 20, py + 40, 220, 20, editMode ? editing.name : "");
        nameField.setMaxLength(32);
        nameField.setFocused(true);

        xField = new TextFieldWidget(this, textRenderer, px + 20, py + 80, 60, 20, editMode ? Integer.toString(editing.x) : "");
        yField = new TextFieldWidget(this, textRenderer, px + 100, py + 80, 60, 20, editMode ? Integer.toString(editing.y) : "");
        zField = new TextFieldWidget(this, textRenderer, px + 180, py + 80, 60, 20, editMode ? Integer.toString(editing.z) : "");

        xField.setMaxLength(8);
        yField.setMaxLength(8);
        zField.setMaxLength(8);

        int buttonW = 100;
        int buttonH = 20;
        int gap = 10;

        int totalW = (buttonW * 2) + gap;
        int blockX = px + (panelW - totalW) / 2;
        int buttonY = py + panelH - buttonH - 10;

        cancelButton = new ButtonWidget(3000, blockX, buttonY, buttonW, buttonH, "Cancel");
        actionButton = new ButtonWidget(3001, blockX + buttonW + gap, buttonY, buttonW, buttonH, editMode ? "Save" : "Create");
        actionButton.active = false;

        NacEntity player = NacPlatformAPI.get().entityProvider.getLocalPlayer();

        if(!editMode) {
            xField.setText("" + (int)player.x);
            yField.setText("" + 0);
            zField.setText("" + (int)player.z);
        }

        buttons.add(cancelButton);
        buttons.add(actionButton);

    }


    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void tick() {
        nameField.tick();
        xField.tick();
        yField.tick();
        zField.tick();
        updateActionButtonState();
    }

    private void updateActionButtonState() {
        String name = nameField.getText().trim();
        String xs = xField.getText().trim();
        String ys = yField.getText().trim();
        String zs = zField.getText().trim();

        if (name.isEmpty() || xs.isEmpty() || ys.isEmpty() || zs.isEmpty()) {
            actionButton.active = false;
            return;
        }

        if (!name.matches("^[A-Za-z0-9 _-]+$")) {
            actionButton.active = false;
            return;
        }

        try {
            Integer.parseInt(xs);
            Integer.parseInt(ys);
            Integer.parseInt(zs);
        } catch (NumberFormatException e) {
            actionButton.active = false;
            return;
        }

        actionButton.active = true;
    }


    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();

        int panelW = 260;
        int panelH = 160;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        drawCenteredTextWithShadow(textRenderer, editMode ? "Edit Waypoint" : "Create Waypoint", width / 2, py + 10, 0xFFFFFF);
        drawTextWithShadow(textRenderer, "Name (A-Za-z0-9 _-)", px + 20, py + 28, 0xA0A0A0);
        drawTextWithShadow(textRenderer, "X", px + 20, py + 68, 0xA0A0A0);
        drawTextWithShadow(textRenderer, "Y", px + 100, py + 68, 0xA0A0A0);
        drawTextWithShadow(textRenderer, "Z", px + 180, py + 68, 0xA0A0A0);

        nameField.render();
        xField.render();
        yField.render();
        zField.render();

        super.render(mouseX, mouseY, delta);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        nameField.mouseClicked(mouseX, mouseY, button);
        xField.mouseClicked(mouseX, mouseY, button);
        yField.mouseClicked(mouseX, mouseY, button);
        zField.mouseClicked(mouseX, mouseY, button);
    }


    private boolean isAllowedNameChar(char c) {
        return Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-';
    }

    private boolean isAllowedDigitChar(char c) {
        return (c >= '0' && c <= '9') || c == '-';
    }


    @Override
    protected void keyPressed(char character, int keyCode) {

        // FIXME: handling this myself atm because returning stops the handle tab from working and too lazy to rewrite the below .
        if (keyCode == Keyboard.KEY_TAB) {
            handleTab();
            return;
        }

        if (nameField.focused) {
            if (isAllowedNameChar(character) || keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE) {
                nameField.keyPressed(character, keyCode);
            }
            return;
        }

        if (xField.focused || yField.focused || zField.focused) {
            if (isAllowedDigitChar(character) || keyCode == Keyboard.KEY_BACK || keyCode == Keyboard.KEY_DELETE) {
                if (xField.focused) xField.keyPressed(character, keyCode);
                else if (yField.focused) yField.keyPressed(character, keyCode);
                else if (zField.focused) zField.keyPressed(character, keyCode);
            }
            return;
        }

        if (character == '\r' && actionButton.active) {
            handleAction();
            return;
        }

        super.keyPressed(character, keyCode);
    }


    @Override
    public void handleTab() {
        if (nameField.focused) {
            nameField.setFocused(false);
            xField.setFocused(true);
        } else if (xField.focused) {
            xField.setFocused(false);
            yField.setFocused(true);
        } else if (yField.focused) {
            yField.setFocused(false);
            zField.setFocused(true);
        } else {
            zField.setFocused(false);
            nameField.setFocused(true);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 3000) {
            minecraft.setScreen(parent);
        } else if (button.id == 3001 && actionButton.active) {
            handleAction();
        }
    }

    private void handleAction() {
        String name = nameField.getText().trim();
        int x = Integer.parseInt(xField.getText().trim());
        int y = Integer.parseInt(yField.getText().trim());
        int z = Integer.parseInt(zField.getText().trim());


        if (editMode) {
            NacWaypoint updated = new NacWaypoint(name, x, y, z);
            NacWaypoints.update(editing, updated);
        } else {
            NacWaypoints.add(new NacWaypoint(name, x, y, z));
        }

        minecraft.setScreen(parent);
    }

}
