package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.ui.themes.UIThemeMinecraft;

public abstract class UIElement {
    public int x, y, w, h;

    public boolean isHovered(int mx, int my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    protected int getTextColor(boolean active, boolean hovered) {
        if (!active) return UIThemeMinecraft.TEXT_DISABLED;
        if (hovered) return UIThemeMinecraft.TEXT_HOVER;
        return UIThemeMinecraft.TEXT_NORMAL;
    }

    protected void drawCenteredText(String text, int color) {
        PainterAccess p = PainterAccess.get();
        int tw = p.getStringWidth(text);
        int tx = x + (w - tw) / 2;
        int ty = y + (h - 8) / 2;
        p.drawStringWithShadow(text, tx, ty, color);
    }

    protected void drawButtonBackground(int state) {
        PainterAccess p = PainterAccess.get();
        int texture = p.getMinecraftTextureId("/gui/gui.png");
        int texY = 46 + state * 20;
        int half = w / 2;

        p.drawTextureRegion(texture, x, y, 0, texY, half, h);
        p.drawTextureRegion(texture, x + half, y, 200 - half, texY, half, h);
    }

    protected void drawTooltip(String text, int mouseX, int mouseY, int canvasW, int canvasH) {
        PainterAccess p = PainterAccess.get();

        int padding = 4;
        int tw = p.getStringWidth(text);
        int th = 8;

        int bw = tw + padding * 2;
        int bh = th + padding * 2;

        int tx = mouseX + 8;
        if (mouseX > canvasW / 2) tx = mouseX - 12 - bw;

        int ty = mouseY + 8;
        if (ty + bh > canvasH) ty = canvasH - bh;
        if (ty < 0) ty = 0;

        p.drawRect(tx, ty, tx + bw, ty + bh, UIThemeMinecraft.TOOLTIP_BG);
        p.drawRect(tx, ty, tx + bw, ty + 1, UIThemeMinecraft.TOOLTIP_BORDER);
        p.drawRect(tx, ty + bh - 1, tx + bw, ty + bh, UIThemeMinecraft.TOOLTIP_BORDER);

        p.drawStringWithShadow(text, tx + padding, ty + padding, UIThemeMinecraft.TOOLTIP_TEXT);
    }
}
