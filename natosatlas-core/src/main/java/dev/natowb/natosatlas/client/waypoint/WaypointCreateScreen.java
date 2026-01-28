package dev.natowb.natosatlas.client.waypoint;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.access.PainterAccess;
import dev.natowb.natosatlas.client.ui.UIScaleInfo;
import dev.natowb.natosatlas.client.ui.elements.UIElementButton;
import dev.natowb.natosatlas.client.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.client.ui.elements.UIElementTextField;
import dev.natowb.natosatlas.client.ui.elements.UIElementSlider;
import dev.natowb.natosatlas.client.ui.UITheme;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import dev.natowb.natosatlas.client.ui.layout.UIHorizontalLayout;
import dev.natowb.natosatlas.client.ui.layout.UILayout;
import dev.natowb.natosatlas.client.ui.layout.UIVerticalLayout;
import org.lwjgl.input.Keyboard;

import static dev.natowb.natosatlas.client.texture.TextureProvider.*;

public class WaypointCreateScreen extends UIScreen {

    private final boolean editMode;
    private final Waypoint editing;

    private UIElementTextField nameField;
    private UIElementTextField xField;
    private UIElementTextField yField;
    private UIElementTextField zField;

    private UIElementSlider rSlider;
    private UIElementSlider gSlider;
    private UIElementSlider bSlider;

    private UIElementButton actionButton;

    private int previewColor = 0xFFFFFF;

    int x = 0;
    int z = 0;
    int y = 0;

    public WaypointCreateScreen(UIScreen parent) {
        super(parent);
        this.editMode = false;
        this.editing = null;
        NAEntity player = NACore.getClient().getPlatform().world.getPlayer();
        this.x = (int) player.x;
        this.y = (int) player.y;
        this.z = (int) player.z;
    }

    public WaypointCreateScreen(UIScreen parent, int x, int z) {
        super(parent);
        this.editMode = false;
        this.editing = null;
        this.x = x;
        this.y = 64;
        this.z = z;
    }

    public WaypointCreateScreen(UIScreen parent, Waypoint waypoint) {
        super(parent);
        this.editMode = true;
        this.editing = waypoint;
        this.previewColor = waypoint.color;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int panelW = 260;
        int panelH = 220;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        nameField = new UIElementTextField(px + 20, py + 40, 220, 20, editMode ? editing.name : "");
        nameField.setMaxLength(24);
        nameField.setFocused(true);
        addTextField(nameField);

        int labelY = py + 68;
        int fieldY = labelY + 22;

        UILayout coordsLayout = new UIHorizontalLayout(px + 20, fieldY, 20, false);

        xField = new UIElementTextField(coordsLayout, 60, 20, editMode ? Integer.toString(editing.x) : Integer.toString(x));
        yField = new UIElementTextField(coordsLayout, 60, 20, editMode ? Integer.toString(editing.y) : Integer.toString(y));
        zField = new UIElementTextField(coordsLayout, 60, 20, editMode ? Integer.toString(editing.z) : Integer.toString(z));

        xField.setMaxLength(8);
        yField.setMaxLength(8);
        zField.setMaxLength(8);

        addTextField(xField);
        addTextField(yField);
        addTextField(zField);

        UILayout vertical = new UIVerticalLayout(px + 130, py + 110, 4);

        int red = editMode ? ((editing.color >> 16) & 0xFF) : 255;
        int green = editMode ? ((editing.color >> 8) & 0xFF) : 255;
        int blue = editMode ? (editing.color & 0xFF) : 255;

        rSlider = new UIElementSlider(4001, vertical, 220, 20, red, "R");
        rSlider.setRange(0, 255);
        rSlider.setStep(1);
        addSlider(rSlider);

        gSlider = new UIElementSlider(4002, vertical, 220, 20, green, "G");
        gSlider.setRange(0, 255);
        gSlider.setStep(1);
        addSlider(gSlider);

        bSlider = new UIElementSlider(4003, vertical, 220, 20, blue, "B");
        bSlider.setRange(0, 255);
        bSlider.setStep(1);
        addSlider(bSlider);


        UIElementIconButton back = new UIElementIconButton(3000, px + 20, py + panelH - 30, 20, 20, ICON_BACK);
        back.setTooltip("Back");
        back.setHandler(btn -> NACore.getClient().getPlatform().openNacScreen(parent));
        addButton(back);

        UIElementIconButton confirm = new UIElementIconButton(3001, px + panelW - 40, py + panelH - 30, 20, 20, ICON_CHECK);
        confirm.setTooltip(editMode ? "Save" : "Create");
        confirm.active = false;
        confirm.setHandler(btn -> handleAction());
        addButton(confirm);

        this.actionButton = confirm;
    }

    @Override
    public void tick() {
        super.tick();
        updateActionButtonState();

        int r = (int) rSlider.getValue();
        int g = (int) gSlider.getValue();
        int b = (int) bSlider.getValue();
        previewColor = (r << 16) | (g << 8) | b;
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
        } catch (Exception e) {
            actionButton.active = false;
            return;
        }

        actionButton.active = true;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        PainterAccess p = NACore.getClient().getPlatform().painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        int panelW = 260;
        int panelH = 220;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        p.drawCenteredString(editMode ? "Edit Waypoint" : "Create Waypoint", width / 2, py + 10, UITheme.TITLE_TEXT);
        p.drawString("Name (A-Za-z0-9 _-)", px + 20, py + 28, UITheme.LABEL_TEXT, false);
        p.drawString("X", px + 20, py + 68, UITheme.LABEL_TEXT, false);
        p.drawString("Y", px + 100, py + 68, UITheme.LABEL_TEXT, false);
        p.drawString("Z", px + 180, py + 68, UITheme.LABEL_TEXT, false);

        int argb = 0xFF000000 | previewColor;

        int leftBtnX = px + 20;
        int rightBtnX = px + panelW - 40;

        int centerX = (leftBtnX + rightBtnX) / 2;
        int iconX = centerX;
        int iconY = py + panelH - 30;

        NACore.getClient().getPlatform().painter.drawIcon(ICON_WAYPOINTS, iconX, iconY, 20, argb);


        super.render(mouseX, mouseY, delta, scaleInfo);
    }

    @Override
    public void keyPressed(char character, int keyCode) {
        super.keyPressed(character, keyCode);

        if (keyCode == Keyboard.KEY_TAB) {
            handleTab();
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

        Waypoint wp = new Waypoint(name, x, y, z);
        wp.color = previewColor;

        if (editMode) {
            Waypoints.update(editing, wp);
        } else {
            Waypoints.add(wp);
        }

        NACore.getClient().getPlatform().openNacScreen(parent);
    }

    @Override
    public void onSliderChanged(UIElementSlider slider) {
        if (slider.id == 4001 || slider.id == 4002 || slider.id == 4003) {
            int r = (int) rSlider.getValue();
            int g = (int) gSlider.getValue();
            int b = (int) bSlider.getValue();
            previewColor = (r << 16) | (g << 8) | b;
        }
    }

}
