package dev.natowb.natosatlas.core.gui;

import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.painter.INacPainter;
import org.lwjgl.input.Mouse;

public class NacGuiButton {

    public int id;
    public int x, y, w, h;
    public String label;
    public boolean active = true;

    private boolean wasMouseDown = false;

    public NacGuiButton(int id, int x, int y, int w, int h, String label) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.label = label;
    }

    public void render(int mouseX, int mouseY) {
        INacPainter p = NacPlatformAPI.get().painter;

        boolean hovered = mouseX >= x && mouseX <= x + w &&
                mouseY >= y && mouseY <= y + h;

        int bg;
        int border;
        int text;

        if (!active) {
            bg = NacGuiTheme.BUTTON_BG_DISABLED;
            border = NacGuiTheme.BUTTON_BORDER_DISABLED;
            text = NacGuiTheme.BUTTON_TEXT_DISABLED;
        } else if (hovered) {
            bg = NacGuiTheme.BUTTON_BG_HOVER;
            border = NacGuiTheme.BUTTON_BORDER_HOVER;
            text = NacGuiTheme.BUTTON_TEXT_HOVER;
        } else {
            bg = NacGuiTheme.BUTTON_BG;
            border = NacGuiTheme.BUTTON_BORDER;
            text = NacGuiTheme.BUTTON_TEXT;
        }


        p.drawRect(x, y, x + w, y + h, bg);

        p.drawRect(x, y, x + w, y + 1, border);         // top
        p.drawRect(x, y + h - 1, x + w, y + h, border); // bottom
        p.drawRect(x, y, x + 1, y + h, border);         // left
        p.drawRect(x + w - 1, y, x + w, y + h, border); // right

        int textWidth = p.getStringWidth(label);
        int tx = x + (w - textWidth) / 2;
        int ty = y + (h - 8) / 2;

        p.drawString(label, tx, ty, text, false);
    }


    public boolean handleClick(int mouseX, int mouseY) {
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown && !wasMouseDown) {
            if (active &&
                    mouseX >= x && mouseX <= x + w &&
                    mouseY >= y && mouseY <= y + h) {

                wasMouseDown = mouseDown;
                NacPlatformAPI.get().playSound("random.click", 1.0F, 1.0F);
                return true;
            }
        }

        wasMouseDown = mouseDown;
        return false;
    }

    public void resetClickState() {
        wasMouseDown = false;
    }

}
