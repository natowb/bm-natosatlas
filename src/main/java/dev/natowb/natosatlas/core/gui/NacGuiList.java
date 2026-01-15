package dev.natowb.natosatlas.core.gui;

import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.painter.INacPainter;
import org.lwjgl.input.Mouse;

import java.util.List;

public class NacGuiList<T> {

    private final int x, y, w, h;
    private final int entryHeight;

    private List<T> items;

    private int scrollOffset = 0;
    private int selectedIndex = -1;

    private int lastClickedIndex = -1;
    private long lastClickTime = 0;
    private boolean wasMouseDown = false;

    private final NacListRenderer<T> renderer;

    public interface NacListRenderer<T> {
        void render(INacPainter p, T item,
                    int x, int y, int w, int h,
                    boolean hovered, boolean selected);
    }

    public NacGuiList(int x, int y, int w, int h, int entryHeight,
                      List<T> items, NacListRenderer<T> renderer) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.entryHeight = entryHeight;
        this.items = items;
        this.renderer = renderer;
    }

    public void setItems(List<T> items) {
        this.items = items;
        clampScroll();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public T getSelectedItem() {
        if (selectedIndex < 0 || selectedIndex >= items.size()) return null;
        return items.get(selectedIndex);
    }

    public void render(int mouseX, int mouseY) {
        INacPainter p = NacPlatformAPI.get().painter;

        int visibleStart = scrollOffset / entryHeight;
        int visibleEnd = Math.min(items.size(),
                visibleStart + (h / entryHeight) + 1);

        int yPos = y - (scrollOffset % entryHeight);

        for (int i = visibleStart; i < visibleEnd; i++) {
            T item = items.get(i);

            boolean hovered = mouseX >= x && mouseX <= x + w &&
                    mouseY >= yPos && mouseY <= yPos + entryHeight;

            boolean selected = (i == selectedIndex);

            renderer.render(p, item, x, yPos, w, entryHeight, hovered, selected);

            yPos += entryHeight;
        }

        renderScrollbar();
    }

    private void renderScrollbar() {
        INacPainter p = NacPlatformAPI.get().painter;

        int contentHeight = items.size() * entryHeight;
        if (contentHeight <= h) return;

        int barX1 = x + w - 6;
        int barX2 = x + w;

        float ratio = (float) h / contentHeight;
        int thumbHeight = Math.max(20, (int) (ratio * h));

        float scrollRatio = (float) scrollOffset / (contentHeight - h);
        int thumbY = y + (int) (scrollRatio * (h - thumbHeight));

        p.drawRect(barX1, y, barX2, y + h, NacGuiTheme.SCROLLBAR_BG);
        p.drawRect(barX1, thumbY, barX2, thumbY + thumbHeight, NacGuiTheme.SCROLLBAR_THUMB);
    }

    public void mouseScroll(int amount) {
        scrollOffset -= amount / 10;
        clampScroll();
    }

    private void clampScroll() {
        int contentHeight = items.size() * entryHeight;
        scrollOffset = Math.max(0, scrollOffset);
        scrollOffset = Math.min(scrollOffset, Math.max(0, contentHeight - h));
    }

    public boolean mouseDown(int mouseX, int mouseY) {
        boolean mouseDown = Mouse.isButtonDown(0);

        if (mouseDown && !wasMouseDown) {

            if (mouseX >= x && mouseX <= x + w &&
                    mouseY >= y && mouseY <= y + h) {

                int index = (mouseY - y + scrollOffset) / entryHeight;

                if (index >= 0 && index < items.size()) {

                    long now = System.currentTimeMillis();
                    boolean doubleClick =
                            (index == lastClickedIndex) &&
                                    ((now - lastClickTime) < 250);

                    lastClickTime = now;
                    lastClickedIndex = index;

                    selectedIndex = index;

                    wasMouseDown = true;
                    return doubleClick;
                }
            }
        }

        wasMouseDown = mouseDown;
        return false;
    }

    public void mouseUp() {
        wasMouseDown = false;
    }
}
