package dev.natowb.natosatlas.core.gui;

import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.painter.INacPainter;
import dev.natowb.natosatlas.core.screens.NacScreen;
import org.lwjgl.input.Keyboard;

public class NacGuiTextField {

    private final NacScreen parent;
    private final int x, y, w, h;

    private String text = "";
    private int maxLength = 32;

    public boolean focused = false;
    public boolean enabled = true;

    private int focusedTicks = 0;

    public NacGuiTextField(NacScreen parent, int x, int y, int w, int h, String initial) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = initial != null ? initial : "";
    }

    public void setText(String t) {
        this.text = t != null ? t : "";
    }

    public String getText() {
        return text;
    }

    public void setMaxLength(int len) {
        this.maxLength = len;
    }

    public void setFocused(boolean f) {
        if (f && !this.focused) {
            focusedTicks = 0;
        }
        this.focused = f;
    }

    public void tick() {
        focusedTicks++;
    }

    public void keyPressed(char c, int keyCode) {
        if (!enabled || !focused) return;

        if (keyCode == Keyboard.KEY_TAB) {
            parent.handleTab();
            return;
        }

        if (c == 22) {
            String clip = java.awt.Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getContents(null)
                    .toString();

            if (clip == null) clip = "";
            int space = maxLength - text.length();
            if (space > 0) {
                text += clip.substring(0, Math.min(space, clip.length()));
            }
            return;
        }

        if (keyCode == Keyboard.KEY_BACK && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
            return;
        }

        if (isValidChar(c)) {
            if (text.length() < maxLength) {
                text += c;
            }
        }
    }

    private boolean isValidChar(char c) {
        return c >= 32 && c != 127;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean inside =
                enabled &&
                        mouseX >= x && mouseX < x + w &&
                        mouseY >= y && mouseY < y + h;

        setFocused(inside);
    }

    public void render() {
        INacPainter p = NacPlatformAPI.get().painter;

        p.drawRect(x - 1, y - 1, x + w + 1, y + h + 1, NacGuiTheme.TEXTFIELD_BORDER);
        p.drawRect(x, y, x + w, y + h, NacGuiTheme.TEXTFIELD_BG);

        boolean showCursor = focused && (focusedTicks / 6) % 2 == 0;
        String display = enabled ? text + (showCursor ? "_" : "") : text;

        int textY = y + (h - 8) / 2;
        int color = enabled ? NacGuiTheme.TEXTFIELD_TEXT : NacGuiTheme.TEXTFIELD_TEXT_DISABLED;
        p.drawString(display, x + 4, textY, color, false);

    }
}
