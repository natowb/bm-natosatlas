package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementList;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;

public class WaypointListScreen extends UIScreen {

    private UIElementList<Waypoint> list;

    private UIElementButton deleteButton;
    private UIElementButton editButton;
    private UIElementButton createButton;
    private UIElementButton backButton;

    public WaypointListScreen(UIScreen parent) {
        super(parent);
        Waypoints.load();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int maxListWidth = 400;
        int listWidth = Math.min(width - 40, maxListWidth);
        int listX = (width - listWidth) / 2;

        list = new WaypointListElement(listX, 50, listWidth, height - 140, 30);

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

        editButton = new UIElementButton(1003, leftX, topY, smallW, buttonH, "Edit");
        deleteButton = new UIElementButton(1002, leftX, bottomY, smallW, buttonH, "Delete");
        createButton = new UIElementButton(1001, rightX, topY, largeW, buttonH, "Create");
        backButton = new UIElementButton(1000, rightX, bottomY, largeW, buttonH, "Back");

        addButton(editButton);
        addButton(deleteButton);
        addButton(createButton);
        addButton(backButton);

        editButton.active = false;
        deleteButton.active = false;
    }

    private void updateButtonStates() {
        boolean valid = list.getSelectedIndex() >= 0;
        editButton.active = valid;
        deleteButton.active = valid;
    }

    private void openEditScreen(int index) {
        Waypoint wp = Waypoints.getAll().get(index);
        NatosAtlasCore.get().platform.openNacScreen(new WaypointCreateScreen(this, wp));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        PainterAccess p = PainterAccess.get();

        if (Settings.useReiMinimapWaypointStorage) {
            p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
            p.drawCenteredString("Use Rei's Minimap to manage waypoints", width / 2, height / 2, UITheme.TITLE_TEXT);
            return;
        }

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Waypoints", width / 2, 20, UITheme.TITLE_TEXT);

        list.render(mouseX, mouseY);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }


    @Override
    public void mouseScroll(int amount) {
        if (Settings.useReiMinimapWaypointStorage) return;
        list.mouseScroll(amount);
    }

    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (Settings.useReiMinimapWaypointStorage) return;
        if (button != 0) return;

        boolean doubleClick = list.mouseDown(mouseX, mouseY);
        updateButtonStates();

        if (doubleClick) {
            int index = list.getSelectedIndex();
            if (index >= 0) openEditScreen(index);
        }
    }

    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {
        if (Settings.useReiMinimapWaypointStorage) return;
        super.mouseUp(mouseX, mouseY, button);
        if (button == 0) list.mouseUp();
    }

    @Override
    protected void onClick(UIElementButton btn) {
        if (Settings.useReiMinimapWaypointStorage) return;

        if (btn.id == editButton.id) {
            int index = list.getSelectedIndex();
            if (index >= 0) openEditScreen(index);
            return;
        }

        if (btn.id == deleteButton.id) {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                Waypoint wp = Waypoints.getAll().get(index);
                Waypoints.remove(wp);
                list.setItems(Waypoints.getAll());
                updateButtonStates();
            }
            return;
        }

        if (btn.id == createButton.id) {
            NatosAtlasCore.get().platform.openNacScreen(new WaypointCreateScreen(this));
            return;
        }

        if (btn.id == backButton.id) {
            NatosAtlasCore.get().platform.openNacScreen(parent);
        }
    }

}
