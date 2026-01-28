package dev.natowb.natosatlas.client.ui.elements;

import dev.natowb.natosatlas.client.ui.layout.UILayout;
import dev.natowb.natosatlas.client.ui.layout.UIPoint;

public class UIElementButton extends UIElement {

    public interface Handler {
        void onClick(UIElementButton button);
    }

    public int id;
    public String label;
    public boolean active = true;
    public Handler handler;


    public void setHandler(Handler handler) {
        this.handler = handler;
    }

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
        this(id, layout, w, h, label);
        this.active = active;
    }

    public void render(int mouseX, int mouseY) {
        boolean hovered = isHovered(mouseX, mouseY);
        int state = !active ? 0 : hovered ? 2 : 1;
        drawButtonBackground(state);
        int color = getTextColor(active, hovered);
        drawCenteredText(label, color);
    }
}
