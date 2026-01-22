package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementList;

public class WaypointListElement extends UIElementList<Waypoint> {

    private static final int TOGGLE_W = 50;
    private static final int TOGGLE_H = 16;

    public WaypointListElement(int x, int y, int w, int h, int entryHeight) {
        super(x, y, w, h, entryHeight);
        setItems(Waypoints.getAll());
        setRenderer(this::renderWaypoint);
    }

    @Override
    public boolean mouseDown(int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x + w &&
                mouseY >= y && mouseY <= y + h) {

            int index = (mouseY - y + getScrollOffset()) / getEntryHeight();
            if (index >= 0 && index < getItems().size()) {

                Waypoint wp = getItems().get(index);

                int entryY = y + index * getEntryHeight() - (getScrollOffset() % getEntryHeight());
                int toggleX = x + w - TOGGLE_W - 6;
                int toggleY = entryY + (getEntryHeight() - TOGGLE_H) / 2;

                if (mouseX >= toggleX && mouseX <= toggleX + TOGGLE_W &&
                        mouseY >= toggleY && mouseY <= toggleY + TOGGLE_H) {

                    wp.visible = !wp.visible;
                    Waypoints.save();
                    return false;
                }
            }
        }

        return super.mouseDown(mouseX, mouseY);
    }

    private void renderWaypoint(Waypoint wp, int x, int y, int w, int h, boolean hovered, boolean selected) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        int bg = UITheme.LIST_BG;
        if (hovered) bg = UITheme.LIST_BG_HOVER;
        if (selected) bg = UITheme.LIST_BG_SELECTED;

        p.drawRect(x, y, x + w, y + h, bg);

        if (hovered || selected) {
            p.drawRect(x, y, x + w, y + 1, UITheme.LIST_BORDER_HOVER);
            p.drawRect(x, y + h - 1, x + w, y + h, UITheme.LIST_BORDER_HOVER);
            p.drawRect(x, y, x + 1, y + h, UITheme.LIST_BORDER_HOVER);
            p.drawRect(x + w - 1, y, x + w, y + h, UITheme.LIST_BORDER_HOVER);
        }

        int titleColor = wp.visible ? UITheme.LIST_TEXT : UITheme.LIST_SUBTEXT;

        p.drawString(wp.name, x + 5, y + 4, titleColor, false);
        p.drawString("X: " + wp.x + "  Y: " + wp.y + "  Z: " + wp.z, x + 5, y + 16, UITheme.LIST_SUBTEXT, false);

        String label = wp.visible ? "Hide" : "Show";

        int toggleX = x + w - TOGGLE_W - 6;
        int toggleY = y + (h - TOGGLE_H) / 2;

        int toggleBg = wp.visible ? UITheme.BUTTON_BG : UITheme.BUTTON_BG_HOVER;

        p.drawRect(toggleX, toggleY, toggleX + TOGGLE_W, toggleY + TOGGLE_H, toggleBg);

        int textW = p.getStringWidth(label);
        p.drawString(label, toggleX + (TOGGLE_W - textW) / 2, toggleY + 4, UITheme.BUTTON_TEXT, false);
    }
}
