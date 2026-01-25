package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.ui.themes.UIThemeMinecraft;

public class UIElementIconButton extends UIElementButton {

    private int iconIndex;
    private int color = UIThemeMinecraft.TEXT_NORMAL;
    private String tooltip;

    public UIElementIconButton(int id, int x, int y, int w, int h, int iconIndex) {
        super(id, x, y, w, h, "");
        this.iconIndex = iconIndex;
    }

    public UIElementIconButton(int id, UILayout layout, int w, int h, int iconIndex) {
        super(id, layout, w, h, "");
        this.iconIndex = iconIndex;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
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
        int size = Math.min(w, h) - 6;
        int ix = x + (w - size) / 2;
        int iy = y + (h - size) / 2 - 1;
        p.drawIconWithShadow(iconIndex, ix, iy, size, color);
    }

    public void renderTooltip(int mouseX, int mouseY, int canvasW, int canvasH) {
        if (tooltip == null || tooltip.isEmpty()) return;
        if (!isHovered(mouseX, mouseY)) return;
        drawTooltip(tooltip, mouseX, mouseY, canvasW, canvasH);
    }
}
