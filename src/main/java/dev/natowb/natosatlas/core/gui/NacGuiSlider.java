package dev.natowb.natosatlas.core.gui;

import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.painter.INacPainter;
import org.lwjgl.input.Mouse;

public class NacGuiSlider {

    public int id;
    public int x, y, w, h;

    private float value; // 0.0 → 1.0
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

    public NacGuiSlider(
            int id,
            int x, int y, int w, int h,
            float initialValue,
            String label,
            ValueFormatter formatter,
            ValueChangedCallback callback
    ) {
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
        INacPainter p = NacPlatformAPI.get().painter;

        boolean hovered = mouseX >= x && mouseX <= x + w &&
                mouseY >= y && mouseY <= y + h;

        int trackColor = hovered
                ? NacGuiTheme.SLIDER_TRACK_BG_HOVER
                : NacGuiTheme.SLIDER_TRACK_BG;

        p.drawRect(x, y + h / 2 - 2, x + w, y + h / 2 + 2, trackColor);

        int thumbX = x + (int) (value * (w - 8));

        int thumbColor =
                dragging ? NacGuiTheme.SLIDER_THUMB_DRAG :
                        hovered ? NacGuiTheme.SLIDER_THUMB_HOVER :
                                NacGuiTheme.SLIDER_THUMB;

        p.drawRect(thumbX, y, thumbX + 8, y + h, thumbColor);

        String text = label + ": " + formatter.format(value);
        int textWidth = p.getStringWidth(text);
        p.drawString(text, x + (w - textWidth) / 2, y - 12, NacGuiTheme.SLIDER_TEXT, false);
    }


    public boolean mouseDown(int mouseX, int mouseY) {
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown && !wasMouseDown) {
            boolean inside =
                    mouseX >= x && mouseX <= x + w &&
                            mouseY >= y && mouseY <= y + h;

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
        if (dragging) {
            updateValue(mouseX);
        }
    }

    private void updateValue(int mouseX) {
        float newValue = (float) (mouseX - x) / (float) (w - 8);
        value = clamp(newValue);

        if (callback != null) {
            callback.onChanged(value);
        }
    }
}
