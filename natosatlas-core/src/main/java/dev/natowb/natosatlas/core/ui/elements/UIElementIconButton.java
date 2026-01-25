package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.ui.layout.UIPoint;

public class UIElementIconButton extends UIElementButton {

    private int iconIndex;
    private int color = 0xFFFFFFFF;

    public UIElementIconButton(int id, int x, int y, int w, int h, int iconIndex) {
        super(id, x, y, w, h, "");
        this.iconIndex = iconIndex;
    }

    public UIElementIconButton(int id, UILayout layout, int w, int h, int iconIndex) {
        super(id, layout, w, h, "");
        this.iconIndex = iconIndex;
    }

    public void setIcon(int iconIndex) {
        this.iconIndex = iconIndex;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);

        PainterAccess p = PainterAccess.get();

        int iconSize = Math.min(w, h) - 2;
        int ix = x + (w - iconSize) / 2;
        int iy = y + (h - iconSize) / 2;

        p.drawIcon(iconIndex, ix, iy, iconSize, color);
    }
}
