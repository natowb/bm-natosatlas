package dev.natowb.natosatlas.core.screens;


import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.NacWaypoints;
import dev.natowb.natosatlas.core.gui.NacGuiButton;
import dev.natowb.natosatlas.core.gui.NacGuiTextField;
import dev.natowb.natosatlas.core.gui.NacGuiTheme;
import dev.natowb.natosatlas.core.models.NacEntity;
import dev.natowb.natosatlas.core.models.NacWaypoint;
import dev.natowb.natosatlas.core.painter.INacPainter;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;

public class NacWaypointCreateScreen extends NacScreen {

    private final boolean editMode;
    private final NacWaypoint editing;

    private NacGuiTextField nameField;
    private NacGuiTextField xField;
    private NacGuiTextField yField;
    private NacGuiTextField zField;

    private NacGuiButton actionButton;
    private NacGuiButton cancelButton;

    private boolean wasMouseDown = false;

    public NacWaypointCreateScreen(NacScreen parent) {
        super(parent);
        this.editMode = false;
        this.editing = null;
    }

    public NacWaypointCreateScreen(NacScreen parent, NacWaypoint waypoint) {
        super(parent);
        this.editMode = true;
        this.editing = waypoint;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int panelW = 260;
        int panelH = 160;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        nameField = new NacGuiTextField(this, px + 20, py + 40, 220, 20,
                editMode ? editing.name : "");
        nameField.setMaxLength(32);
        nameField.setFocused(true);

        xField = new NacGuiTextField(this, px + 20, py + 80, 60, 20,
                editMode ? Integer.toString(editing.x) : "");
        yField = new NacGuiTextField(this, px + 100, py + 80, 60, 20,
                editMode ? Integer.toString(editing.y) : "");
        zField = new NacGuiTextField(this, px + 180, py + 80, 60, 20,
                editMode ? Integer.toString(editing.z) : "");

        xField.setMaxLength(8);
        yField.setMaxLength(8);
        zField.setMaxLength(8);

        int buttonW = 100;
        int buttonH = 20;
        int gap = 10;

        int totalW = (buttonW * 2) + gap;
        int blockX = px + (panelW - totalW) / 2;
        int buttonY = py + panelH - buttonH - 10;

        cancelButton = new NacGuiButton(3000, blockX, buttonY, buttonW, buttonH, "Cancel");
        actionButton = new NacGuiButton(3001, blockX + buttonW + gap, buttonY, buttonW, buttonH,
                editMode ? "Save" : "Create");
        actionButton.active = false;

        if (!editMode) {
            NacEntity player = NacPlatformAPI.get().entityProvider.getLocalPlayer();
            xField.setText("" + (int) player.x);
            yField.setText("" + 0);
            zField.setText("" + (int) player.z);
        }
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
        INacPainter p = NacPlatformAPI.get().painter;

        p.drawRect(0, 0, width, height, NacGuiTheme.PANEL_BG);

        int panelW = 260;
        int panelH = 160;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        p.drawCenteredString(editMode ? "Edit Waypoint" : "Create Waypoint", width / 2, py + 10, NacGuiTheme.TITLE_TEXT);

        p.drawString("Name (A-Za-z0-9 _-)", px + 20, py + 28, NacGuiTheme.LABEL_TEXT, false);
        p.drawString("X", px + 20, py + 68, NacGuiTheme.LABEL_TEXT, false);
        p.drawString("Y", px + 100, py + 68, NacGuiTheme.LABEL_TEXT, false);
        p.drawString("Z", px + 180, py + 68, NacGuiTheme.LABEL_TEXT, false);

        nameField.render();
        xField.render();
        yField.render();
        zField.render();

        cancelButton.render(mouseX, mouseY);
        actionButton.render(mouseX, mouseY);

        handleFieldClicks(mouseX, mouseY);
        handleButtonClicks(mouseX, mouseY);
    }

    private void handleFieldClicks(int mouseX, int mouseY) {
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown && !wasMouseDown) {
            nameField.mouseClicked(mouseX, mouseY, 0);
            xField.mouseClicked(mouseX, mouseY, 0);
            yField.mouseClicked(mouseX, mouseY, 0);
            zField.mouseClicked(mouseX, mouseY, 0);
        }

        wasMouseDown = mouseDown;
    }

    private void handleButtonClicks(int mouseX, int mouseY) {
        if (cancelButton.handleClick(mouseX, mouseY)) {
            NacPlatformAPI.get().openNacScreen(parent);
        }

        if (actionButton.handleClick(mouseX, mouseY) && actionButton.active) {
            handleAction();
        }
    }

    @Override
    public void keyPressed(char character, int keyCode) {
        super.keyPressed(character, keyCode);

        if (keyCode == Keyboard.KEY_TAB) {
            handleTab();
            return;
        }

        if (nameField.focused) {
            nameField.keyPressed(character, keyCode);
            return;
        }

        if (xField.focused || yField.focused || zField.focused) {
            if (xField.focused) xField.keyPressed(character, keyCode);
            else if (yField.focused) yField.keyPressed(character, keyCode);
            else if (zField.focused) zField.keyPressed(character, keyCode);
            return;
        }

        if (character == '\r' && actionButton.active) {
            handleAction();
        }

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

        NacPlatformAPI.get().openNacScreen(parent);
    }

    @Override
    public void resetAllButtonsClickState() {
        cancelButton.resetClickState();
        actionButton.resetClickState();
    }
}
