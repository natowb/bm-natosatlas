package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.ui.layout.UIPoint;
import org.lwjgl.input.Mouse;

public class UIElementSlider extends UIElement {

    public int id;

    private float value;
    private float min = 0f;
    private float max = 1f;
    private float step = 0f;

    private boolean dragging = false;
    private boolean wasMouseDown = false;

    private final String label;

    public UIElementSlider(int id, int x, int y, int w, int h, float initialValue, String label) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.label = label;
        this.value = initialValue;
    }

    public UIElementSlider(int id, UILayout layout, int w, int h, float initialValue, String label) {
        this.id = id;
        UIPoint p = layout.next(w, h);
        this.x = p.x;
        this.y = p.y;
        this.w = w;
        this.h = h;
        this.label = label;
        this.value = initialValue;
    }

    public void setRange(float min, float max) {
        this.min = min;
        this.max = max;
        setValue(value);
    }

    public void setStep(float step) {
        this.step = Math.max(0f, step);
        setValue(value);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float v) {
        v = Math.max(min, Math.min(max, v));
        if (step > 0f) {
            v = min + Math.round((v - min) / step) * step;
        }
        this.value = v;
    }

    protected String getDisplayText() {
        return String.format("%s: %.2f", label, value);
    }

    public void render(int mouseX, int mouseY) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;

        int bg = hovered ? UITheme.ELEMENT_BG_HOVER : UITheme.ELEMENT_BG;
        int border = hovered ? UITheme.BUTTON_BORDER_HOVER : UITheme.ELEMENT_BORDER;

        p.drawRect(x, y, x + w, y + h, bg);

        p.drawRect(x, y, x + w, y + 1, border);
        p.drawRect(x, y + h - 1, x + w, y + h, border);
        p.drawRect(x, y, x + 1, y + h, border);
        p.drawRect(x + w - 1, y, x + w, y + h, border);

        float t = (value - min) / (max - min);

        int thumbW = 8;
        int thumbX = x + 4 + (int) (t * (w - 8 - 8));

        int thumbColor = dragging
                ? UITheme.BUTTON_TEXT_DISABLED
                : (hovered ? UITheme.ELEMENT_BORDER_HOVER : UITheme.ELEMENT_BORDER_HOVER);

        p.drawRect(thumbX, y + 2, thumbX + thumbW, y + h - 2, thumbColor);

        String text = getDisplayText();
        int textWidth = p.getStringWidth(text);
        int tx = x + (w - textWidth) / 2;
        int ty = y + (h - 8) / 2;

        p.drawString(text, tx, ty, UITheme.SLIDER_TEXT, false);
    }

    public boolean mouseDown(int mouseX, int mouseY) {
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown && !wasMouseDown) {
            boolean inside = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
            if (inside) {
                dragging = true;
                updateValue(mouseX);
                wasMouseDown = true;
                return true;
            }
        }

        wasMouseDown = mouseDown;
        return false;
    }

    public void mouseUp() {
        dragging = false;
        wasMouseDown = false;
    }

    public void mouseDrag(int mouseX) {
        if (dragging) updateValue(mouseX);
    }

    private void updateValue(int mouseX) {
        float t = (float) (mouseX - (x + 4)) / (float) (w - 8);
        t = Math.max(0f, Math.min(1f, t));
        float real = min + t * (max - min);
        setValue(real);
    }
}