package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementList;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;

public class WaypointListScreen extends UIScreen {


    private UIElementList<Waypoint> list;

    private UIElementButton deleteButton;
    private UIElementButton editButton;
    private UIElementButton createButton;
    private UIElementButton backButton;

    public WaypointListScreen(UIScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        list = new UIElementList<>(
                20, 50,
                width - 40,
                height - 140,
                30,
                Waypoints.getAll(),
                (p, wp, x, y, w, h, hovered, selected) -> {

                    int bg = selected ? UITheme.LIST_BG_SELECTED :
                            hovered  ? UITheme.LIST_BG_HOVER :
                                    UITheme.LIST_BG;

                    p.drawRect(x, y, x + w, y + h, bg);

                    p.drawString(wp.name, x + 5, y + 4, UITheme.LIST_TEXT, false);
                    p.drawString("X: " + wp.x + "  Y: " + wp.y + "  Z: " + wp.z,
                            x + 5, y + 16, UITheme.LIST_SUBTEXT, false);
                }
        );

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

        editButton   = new UIElementButton(1003, leftX, topY, smallW, buttonH, "Edit");
        deleteButton = new UIElementButton(1002, leftX, bottomY, smallW, buttonH, "Delete");
        createButton = new UIElementButton(1001, rightX, topY, largeW, buttonH, "Create");
        backButton   = new UIElementButton(1000, rightX, bottomY, largeW, buttonH, "Back");

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
        NatosAtlas.get().platform.openNacScreen(new WaypointCreateScreen(this, wp));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Waypoints", width / 2, 20, UITheme.TITLE_TEXT);

        list.render(mouseX, mouseY);

        editButton.render(mouseX, mouseY);
        deleteButton.render(mouseX, mouseY);
        createButton.render(mouseX, mouseY);
        backButton.render(mouseX, mouseY);
    }

    @Override
    public void mouseScroll(int amount) {
        list.mouseScroll(amount);
    }

    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        boolean doubleClick = list.mouseDown(mouseX, mouseY);

        updateButtonStates();

        if (doubleClick) {
            int index = list.getSelectedIndex();
            if (index >= 0) openEditScreen(index);
        }

        if (editButton.handleClick(mouseX, mouseY)) {
            int index = list.getSelectedIndex();
            if (index >= 0) openEditScreen(index);
        }

        if (deleteButton.handleClick(mouseX, mouseY)) {
            int index = list.getSelectedIndex();
            if (index >= 0) {
                Waypoint wp = Waypoints.getAll().get(index);
                Waypoints.remove(wp);
                list.setItems(Waypoints.getAll());
                updateButtonStates();
            }
        }

        if (createButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.openNacScreen(new WaypointCreateScreen(this));
        }

        if (backButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.openNacScreen(parent);
        }
    }

    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {
        if (button == 0) {
            list.mouseUp();
        }
    }

    @Override
    public void resetAllButtonsClickState() {
        editButton.resetClickState();
        deleteButton.resetClickState();
        createButton.resetClickState();
        backButton.resetClickState();
    }
}
