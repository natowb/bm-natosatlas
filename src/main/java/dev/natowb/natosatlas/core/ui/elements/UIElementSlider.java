package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.UITheme;
import org.lwjgl.input.Mouse;

public class UIElementSlider {

    public int id;
    public int x, y, w, h;

    private float value;
    private boolean dragging = false;
    private boolean wasMouseDown = false;

    private String label;
    private ValueFormatter formatter;
    private ValueChangedCallback callback;

    public interface ValueFormatter {
        String format(float value);
    }

    public interface ValueChangedCallback {
        void onChanged(float newValue);
    }

    public UIElementSlider(int id, int x, int y, int w, int h, float initialValue, String label, ValueFormatter formatter, ValueChangedCallback callback) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.value = clamp(initialValue);
        this.label = label;
        this.formatter = formatter != null ? formatter : (v -> String.format("%.2f", v));
        this.callback = callback;
    }

    private float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public float getValue() {
        return value;
    }

    public void setValue(float v) {
        this.value = clamp(v);
    }

    public void render(int mouseX, int mouseY) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;

        int bg = hovered ? UITheme.BUTTON_BG_HOVER : UITheme.BUTTON_BG;
        int border = hovered ? UITheme.BUTTON_BORDER_HOVER : UITheme.BUTTON_BORDER;

        p.drawRect(x, y, x + w, y + h, bg);

        p.drawRect(x, y, x + w, y + 1, border);
        p.drawRect(x, y + h - 1, x + w, y + h, border);
        p.drawRect(x, y, x + 1, y + h, border);
        p.drawRect(x + w - 1, y, x + w, y + h, border);

        int trackColor = hovered ? UITheme.SLIDER_TRACK_BG_HOVER : UITheme.SLIDER_TRACK_BG;
        p.drawRect(x + 4, y + 2, x + w - 4, y + h - 2, trackColor);

        int thumbW = 8;
        int thumbX = x + 4 + (int)(value * (w - 8 - 8));

        int thumbColor = dragging
                ? UITheme.SLIDER_THUMB_DRAG
                : (hovered ? UITheme.SLIDER_THUMB_HOVER : UITheme.SLIDER_THUMB);

        p.drawRect(thumbX, y + 2, thumbX + thumbW, y + h - 2, thumbColor);

        String text = label + ": " + formatter.format(value);
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
        float newValue = (float) (mouseX - (x + 4)) / (float) (w - 8);
        value = clamp(newValue);
        if (callback != null) callback.onChanged(value);
    }
}
