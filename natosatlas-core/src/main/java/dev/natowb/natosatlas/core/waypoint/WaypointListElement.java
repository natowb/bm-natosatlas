package dev.natowb.natosatlas.core.waypoint;


import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementList;


public class WaypointListElement extends UIElementList<Waypoint> {

    public WaypointListElement(int x, int y, int w, int h, int entryHeight) {
        super(x, y, w, h, entryHeight);
        setItems(Waypoints.getAll());
        setRenderer(this::renderWaypoint);
    }

    private void renderWaypoint(Waypoint wp, int x, int y, int w, int h, boolean hovered, boolean selected) {
        PlatformPainter p = NatosAtlas.get().platform.painter;
        int bg = UITheme.LIST_BG;
        if (hovered) bg = UITheme.LIST_BG_HOVER;
        if (selected) bg = UITheme.LIST_BG_SELECTED;


        p.drawRect(x, y, x + w, y + h, bg);

        if(hovered || selected) {
            p.drawRect(x, y, x + w, y + 1, UITheme.LIST_BORDER_HOVER);
            p.drawRect(x, y + h - 1, x + w, y + h, UITheme.LIST_BORDER_HOVER);
            p.drawRect(x, y, x + 1, y + h, UITheme.LIST_BORDER_HOVER);
            p.drawRect(x + w - 1, y, x + w, y + h, UITheme.LIST_BORDER_HOVER);
        }




        p.drawString(wp.name, x + 5, y + 4, UITheme.LIST_TEXT, false);
        p.drawString("X: " + wp.x + "  Y: " + wp.y + "  Z: " + wp.z, x + 5, y + 16, UITheme.LIST_SUBTEXT, false);
    }
}
