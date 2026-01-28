package dev.natowb.natosatlas.client.ui.elements;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.access.PainterAccess;
import dev.natowb.natosatlas.client.ui.layout.UILayout;
import dev.natowb.natosatlas.client.ui.layout.UIPoint;
import org.lwjgl.input.Mouse;

public class UIElementSlider extends UIElementButton {

    private float value;
    private float min = 0f;
    private float max = 1f;
    private float step = 0f;

    private boolean dragging = false;
    private boolean wasMouseDown = false;

    public UIElementSlider(int id, int x, int y, int w, int h, float initialValue, String label) {
        super(id, x, y, w, h, label);
        this.value = initialValue;
    }

    public UIElementSlider(int id, UILayout layout, int w, int h, float initialValue, String label) {
        super(id, 0, 0, w, h, label);
        UIPoint p = layout.next(w, h);
        this.x = p.x;
        this.y = p.y;
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
        if (step > 0f) v = min + Math.round((v - min) / step) * step;
        this.value = v;
    }

    protected String getDisplayText() {
        return String.format("%s: %.2f", label, value);
    }

    @Override
    public void render(int mouseX, int mouseY) {
        PainterAccess p = NAClient.get().getPlatform().painter;

        boolean hovered = isHovered(mouseX, mouseY);

        int texture = p.getMinecraftTextureId("/gui/gui.png");
        int texY = 46;
        int half = w / 2;

        p.drawTextureRegion(texture, x, y, 0, texY, half, h);
        p.drawTextureRegion(texture, x + half, y, 200 - half, texY, half, h);

        float t = (value - min) / (max - min);
        int thumbX = x + (int) (t * (w - 8));

        p.drawTextureRegion(texture, thumbX, y, 0, 66, 4, h);
        p.drawTextureRegion(texture, thumbX + 4, y, 196, 66, 4, h);

        String text = getDisplayText();
        int tw = p.getStringWidth(text);
        int tx = x + (w - tw) / 2;
        int ty = y + (h - 8) / 2;

        int color = getTextColor(active, hovered);
        p.drawStringWithShadow(text, tx, ty, color);
    }

    public boolean mouseDown(int mouseX, int mouseY) {
        boolean down = Mouse.isButtonDown(0);
        if (down && !wasMouseDown && isHovered(mouseX, mouseY) && active) {
            dragging = true;
            updateValue(mouseX);
            wasMouseDown = true;
            return true;
        }
        wasMouseDown = down;
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
        float t = (float) (mouseX - x) / (float) (w - 8);
        t = Math.max(0f, Math.min(1f, t));
        setValue(min + t * (max - min));
    }
}
