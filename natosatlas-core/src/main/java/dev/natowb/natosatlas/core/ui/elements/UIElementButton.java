package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.settings.SettingsOption;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.ui.layout.UIPoint;

public class UIElementButton extends UIElement {

    public int id;
    public String label;
    public boolean active = true;

    public UIElementButton(int id, int x, int y, int w, int h, String label) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.label = label;
    }

    public UIElementButton(int id, UILayout layout, int w, int h, String label) {
        this.id = id;
        UIPoint p = layout.next(w, h);
        this.x = p.x;
        this.y = p.y;

        this.w = w;
        this.h = h;
        this.label = label;
    }

    public UIElementButton(int id, UILayout layout, int w, int h, String label, boolean active) {
        this.id = id;
        UIPoint p = layout.next(w, h);
        this.x = p.x;
        this.y = p.y;

        this.w = w;
        this.h = h;
        this.label = label;
        this.active = active;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + w &&
                mouseY >= y && mouseY <= y + h;
    }

    public void render(int mouseX, int mouseY) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        boolean hovered = isInside(mouseX, mouseY);

        int bg, border, text;

        if (!active) {
            bg = UITheme.BUTTON_BG_DISABLED;
            border = UITheme.BUTTON_BORDER_DISABLED;
            text = UITheme.BUTTON_TEXT_DISABLED;
        } else if (hovered) {
            bg = UITheme.BUTTON_BG_HOVER;
            border = UITheme.BUTTON_BORDER_HOVER;
            text = UITheme.BUTTON_TEXT_HOVER;
        } else {
            bg = UITheme.BUTTON_BG;
            border = UITheme.BUTTON_BORDER;
            text = UITheme.BUTTON_TEXT;
        }

        p.drawRect(x, y, x + w, y + h, bg);
        p.drawRect(x, y, x + w, y + 1, border);
        p.drawRect(x, y + h - 1, x + w, y + h, border);
        p.drawRect(x, y, x + 1, y + h, border);
        p.drawRect(x + w - 1, y, x + w, y + h, border);

        int textWidth = p.getStringWidth(label);
        int tx = x + (w - textWidth) / 2;
        int ty = y + (h - 8) / 2;

        p.drawString(label, tx, ty, text, false);
    }
}

