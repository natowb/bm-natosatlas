package dev.natowb.natosatlas.core.screens;

import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.NacWaypoints;
import dev.natowb.natosatlas.core.gui.NacGuiButton;
import dev.natowb.natosatlas.core.gui.NacGuiList;
import dev.natowb.natosatlas.core.gui.NacGuiTheme;
import dev.natowb.natosatlas.core.models.NacWaypoint;
import dev.natowb.natosatlas.core.painter.INacPainter;

public class NacWaypointListScreen extends NacScreen {


    private NacGuiList<NacWaypoint> list;

    private NacGuiButton deleteButton;
    private NacGuiButton editButton;
    private NacGuiButton createButton;
    private NacGuiButton backButton;

    public NacWaypointListScreen(NacScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        // Create reusable list component
        list = new NacGuiList<>(
                20, 50,
                width - 40,
                height - 140,
                30,
                NacWaypoints.getAll(),
                (p, wp, x, y, w, h, hovered, selected) -> {

                    int bg = selected ? NacGuiTheme.LIST_BG_SELECTED :
                            hovered  ? NacGuiTheme.LIST_BG_HOVER :
                                    NacGuiTheme.LIST_BG;

                    p.drawRect(x, y, x + w, y + h, bg);

                    p.drawString(wp.name, x + 5, y + 4, NacGuiTheme.LIST_TEXT, false);
                    p.drawString("X: " + wp.x + "  Y: " + wp.y + "  Z: " + wp.z,
                            x + 5, y + 16, NacGuiTheme.LIST_SUBTEXT, false);
                }
        );

        // Buttons
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

        editButton   = new NacGuiButton(1003, leftX, topY, smallW, buttonH, "Edit");
        deleteButton = new NacGuiButton(1002, leftX, bottomY, smallW, buttonH, "Delete");
        createButton = new NacGuiButton(1001, rightX, topY, largeW, buttonH, "Create");
        backButton   = new NacGuiButton(1000, rightX, bottomY, largeW, buttonH, "Back");

        editButton.active = false;
        deleteButton.active = false;
    }

    private void updateButtonStates() {
        boolean valid = list.getSelectedIndex() >= 0;
        editButton.active = valid;
        deleteButton.active = valid;
    }

    private void openEditScreen(int index) {
        NacWaypoint wp = NacWaypoints.getAll().get(index);
        NacPlatformAPI.get().openNacScreen(new NacWaypointCreateScreen(this, wp));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        INacPainter p = NacPlatformAPI.get().painter;

        p.drawRect(0, 0, width, height, NacGuiTheme.PANEL_BG);
        p.drawCenteredString("Waypoints", width / 2, 20, NacGuiTheme.TITLE_TEXT);

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
                NacWaypoint wp = NacWaypoints.getAll().get(index);
                NacWaypoints.remove(wp);
                list.setItems(NacWaypoints.getAll());
                updateButtonStates();
            }
        }

        if (createButton.handleClick(mouseX, mouseY)) {
            NacPlatformAPI.get().openNacScreen(new NacWaypointCreateScreen(this));
        }

        if (backButton.handleClick(mouseX, mouseY)) {
            NacPlatformAPI.get().openNacScreen(parent);
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
