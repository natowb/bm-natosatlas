package dev.natowb.natosatlas.core.ui.elements;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.UITheme;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.*;

public class UIElementTextField {

    private final int x, y, w, h;

    private String text = "";
    private int maxLength = 32;

    public boolean focused = false;
    public boolean enabled = true;

    private int cursor = 0;
    private int selectionStart = 0;
    private int selectionEnd = 0;

    private int focusedTicks = 0;
    private boolean dragging = false;

    public UIElementTextField(int x, int y, int w, int h, String initial) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.text = initial != null ? initial : "";
        this.cursor = text.length();
        this.selectionStart = this.selectionEnd = cursor;
    }

    public void setText(String t) {
        this.text = t != null ? t : "";
        cursor = text.length();
        selectionStart = selectionEnd = cursor;
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
        if (!f) clearSelection();
    }

    public void tick() {
        focusedTicks++;
    }

    private void clearSelection() {
        selectionStart = selectionEnd = cursor;
    }

    private boolean hasSelection() {
        return selectionStart != selectionEnd;
    }

    private int getSelectionMin() {
        return Math.min(selectionStart, selectionEnd);
    }

    private int getSelectionMax() {
        return Math.max(selectionStart, selectionEnd);
    }

    private void deleteSelection() {
        if (!hasSelection()) return;
        int min = getSelectionMin();
        int max = getSelectionMax();
        text = text.substring(0, min) + text.substring(max);
        cursor = min;
        clearSelection();
    }

    private void copySelection() {
        if (!hasSelection()) return;
        String sel = text.substring(getSelectionMin(), getSelectionMax());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sel), null);
    }

    private void cutSelection() {
        if (!hasSelection()) return;
        copySelection();
        deleteSelection();
    }

    private void pasteClipboard() {
        try {
            String clip = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .getData(DataFlavor.stringFlavor);

            if (clip == null) return;

            deleteSelection();

            int space = maxLength - text.length();
            if (space <= 0) return;

            clip = clip.substring(0, Math.min(space, clip.length()));

            text = text.substring(0, cursor) + clip + text.substring(cursor);
            cursor += clip.length();
            clearSelection();

        } catch (Exception ignored) {}
    }

    public void keyPressed(char c, int keyCode) {
        if (!enabled || !focused) return;

        boolean ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        if (ctrl && keyCode == Keyboard.KEY_A) {
            selectionStart = 0;
            selectionEnd = text.length();
            cursor = selectionEnd;
            return;
        }

        if (ctrl && keyCode == Keyboard.KEY_C) {
            copySelection();
            return;
        }

        if (ctrl && keyCode == Keyboard.KEY_X) {
            cutSelection();
            return;
        }

        if (ctrl && keyCode == Keyboard.KEY_V) {
            pasteClipboard();
            return;
        }

        if (keyCode == Keyboard.KEY_LEFT) {
            if (cursor > 0) cursor--;
            if (shift) selectionEnd = cursor;
            else clearSelection();
            return;
        }

        if (keyCode == Keyboard.KEY_RIGHT) {
            if (cursor < text.length()) cursor++;
            if (shift) selectionEnd = cursor;
            else clearSelection();
            return;
        }

        if (keyCode == Keyboard.KEY_BACK) {
            if (hasSelection()) {
                deleteSelection();
            } else if (cursor > 0) {
                text = text.substring(0, cursor - 1) + text.substring(cursor);
                cursor--;
            }
            return;
        }

        if (keyCode == Keyboard.KEY_DELETE) {
            if (hasSelection()) {
                deleteSelection();
            } else if (cursor < text.length()) {
                text = text.substring(0, cursor) + text.substring(cursor + 1);
            }
            return;
        }

        if (isValidChar(c)) {
            deleteSelection();
            if (text.length() < maxLength) {
                text = text.substring(0, cursor) + c + text.substring(cursor);
                cursor++;
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

        if (!inside) return;

        int relX = mouseX - (x + 4);
        cursor = getCharIndexAtPixel(relX);
        clearSelection();
        dragging = true;
    }

    public void mouseDragged(int mouseX, int mouseY, int button) {
        if (!dragging || !focused) return;

        int relX = mouseX - (x + 4);
        int newPos = getCharIndexAtPixel(relX);

        selectionEnd = newPos;
        cursor = newPos;
    }

    public void mouseUp(int mouseX, int mouseY, int button) {
        dragging = false;
    }

    private int getCharIndexAtPixel(int px) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        int pos = 0;
        int currentX = 0;

        for (int i = 0; i < text.length(); i++) {
            int w = p.getStringWidth(text.substring(i, i + 1));
            if (currentX + w / 2 >= px) return i;
            currentX += w;
            pos = i + 1;
        }

        return pos;
    }

    public void render() {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        p.drawRect(x - 1, y - 1, x + w + 1, y + h + 1, UITheme.TEXTFIELD_BORDER);
        p.drawRect(x, y, x + w, y + h, UITheme.TEXTFIELD_BG);

        int textY = y + (h - 8) / 2;

        if (hasSelection()) {
            int min = getSelectionMin();
            int max = getSelectionMax();

            int selX1 = x + 4 + p.getStringWidth(text.substring(0, min));
            int selX2 = x + 4 + p.getStringWidth(text.substring(0, max));

            p.drawRect(selX1, y + 1, selX2, y + h - 1, UITheme.TEXTFIELD_SELECTION);
        }

        p.drawString(text, x + 4, textY, UITheme.TEXTFIELD_TEXT, false);

        boolean showCursor = focused && (focusedTicks / 6) % 2 == 0;
        if (showCursor && !hasSelection()) {
            int cx = x + 4 + p.getStringWidth(text.substring(0, cursor));
            p.drawRect(cx, y + 2, cx + 1, y + h - 2, UITheme.TEXTFIELD_TEXT);
        }
    }
}
