package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementTextField;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.map.MapEntity;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import org.lwjgl.input.Keyboard;

public class WaypointCreateScreen extends UIScreen {

    private final boolean editMode;
    private final Waypoint editing;

    private UIElementTextField nameField;
    private UIElementTextField xField;
    private UIElementTextField yField;
    private UIElementTextField zField;

    private UIElementButton actionButton;
    private UIElementButton cancelButton;

    public WaypointCreateScreen(UIScreen parent) {
        super(parent);
        this.editMode = false;
        this.editing = null;
    }

    public WaypointCreateScreen(UIScreen parent, Waypoint waypoint) {
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

        nameField = new UIElementTextField(px + 20, py + 40, 220, 20,
                editMode ? editing.name : "");
        nameField.setMaxLength(32);
        nameField.setFocused(true);

        xField = new UIElementTextField(px + 20, py + 80, 60, 20,
                editMode ? Integer.toString(editing.x) : "");
        yField = new UIElementTextField(px + 100, py + 80, 60, 20,
                editMode ? Integer.toString(editing.y) : "");
        zField = new UIElementTextField(px + 180, py + 80, 60, 20,
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

        cancelButton = new UIElementButton(3000, blockX, buttonY, buttonW, buttonH, "Cancel");
        actionButton = new UIElementButton(3001, blockX + buttonW + gap, buttonY, buttonW, buttonH,
                editMode ? "Save" : "Create");
        actionButton.active = false;

        if (!editMode) {
            MapEntity player = NatosAtlas.get().platform.entityProvider.getLocalPlayer();
            xField.setText("" + (int) player.x);
            yField.setText("0");
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
        PlatformPainter p = NatosAtlas.get().platform.painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        int panelW = 260;
        int panelH = 160;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        p.drawCenteredString(editMode ? "Edit Waypoint" : "Create Waypoint",
                width / 2, py + 10, UITheme.TITLE_TEXT);

        p.drawString("Name (A-Za-z0-9 _-)", px + 20, py + 28, UITheme.LABEL_TEXT, false);
        p.drawString("X", px + 20, py + 68, UITheme.LABEL_TEXT, false);
        p.drawString("Y", px + 100, py + 68, UITheme.LABEL_TEXT, false);
        p.drawString("Z", px + 180, py + 68, UITheme.LABEL_TEXT, false);

        nameField.render();
        xField.render();
        yField.render();
        zField.render();

        cancelButton.render(mouseX, mouseY);
        actionButton.render(mouseX, mouseY);
    }

    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        nameField.mouseClicked(mouseX, mouseY, button);
        xField.mouseClicked(mouseX, mouseY, button);
        yField.mouseClicked(mouseX, mouseY, button);
        zField.mouseClicked(mouseX, mouseY, button);

        if (cancelButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.openNacScreen(parent);
            return;
        }

        if (actionButton.handleClick(mouseX, mouseY) && actionButton.active) {
            handleAction();
        }
    }

    @Override
    public void mouseDrag(int mouseX, int mouseY, int button) {
        nameField.mouseDragged(mouseX, mouseY, button);
        xField.mouseDragged(mouseX, mouseY, button);
        yField.mouseDragged(mouseX, mouseY, button);
        zField.mouseDragged(mouseX, mouseY, button);
    }

    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {
        nameField.mouseUp(mouseX, mouseY, button);
        xField.mouseUp(mouseX, mouseY, button);
        yField.mouseUp(mouseX, mouseY, button);
        zField.mouseUp(mouseX, mouseY, button);

        cancelButton.resetClickState();
        actionButton.resetClickState();
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
            Waypoint updated = new Waypoint(name, x, y, z);
            Waypoints.update(editing, updated);
        } else {
            Waypoints.add(new Waypoint(name, x, y, z));
        }

        NatosAtlas.get().platform.openNacScreen(parent);
    }

    @Override
    public void resetAllButtonsClickState() {
        cancelButton.resetClickState();
        actionButton.resetClickState();
    }
}
