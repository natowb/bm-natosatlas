package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementTextField;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.access.WorldAccess;
import dev.natowb.natosatlas.core.ui.layout.UIHorizontalLayout;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import org.lwjgl.input.Keyboard;

import static dev.natowb.natosatlas.core.texture.TextureProvider.*;

public class WaypointCreateScreen extends UIScreen {

    private final boolean editMode;
    private final Waypoint editing;

    private UIElementTextField nameField;
    private UIElementTextField xField;
    private UIElementTextField yField;
    private UIElementTextField zField;

    private UIElementButton actionButton;
    private UIElementButton cancelButton;

    int x = 0;
    int z = 0;
    int y = 0;

    public WaypointCreateScreen(UIScreen parent) {
        super(parent);
        this.editMode = false;
        this.editing = null;
        NAEntity player = WorldAccess.get().getPlayer();
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
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int panelW = 260;
        int panelH = 160;
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

        UIElementIconButton back = new UIElementIconButton(3000, px + 20, py + panelH - 30, 20, 20, ICON_BACK);
        back.setTooltip("Back");
        back.setHandler(btn -> NatosAtlasCore.get().platform.openNacScreen(parent));
        addButton(back);

        UIElementIconButton confirm = new UIElementIconButton(3001, px + panelW - 20 - 20, py + panelH - 30, 20, 20, ICON_CHECK);
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
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        PainterAccess p = PainterAccess.get();

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        int panelW = 260;
        int panelH = 160;
        int px = (width - panelW) / 2;
        int py = (height - panelH) / 2;

        p.drawCenteredString(editMode ? "Edit Waypoint" : "Create Waypoint", width / 2, py + 10, UITheme.TITLE_TEXT);
        p.drawString("Name (A-Za-z0-9 _-)", px + 20, py + 28, UITheme.LABEL_TEXT, false);
        p.drawString("X", px + 20, py + 68, UITheme.LABEL_TEXT, false);
        p.drawString("Y", px + 100, py + 68, UITheme.LABEL_TEXT, false);
        p.drawString("Z", px + 180, py + 68, UITheme.LABEL_TEXT, false);


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

        if (editMode) {
            Waypoint updated = new Waypoint(name, x, y, z);
            Waypoints.update(editing, updated);
        } else {
            Waypoints.add(new Waypoint(name, x, y, z));
        }

        NatosAtlasCore.get().platform.openNacScreen(parent);
    }
}
