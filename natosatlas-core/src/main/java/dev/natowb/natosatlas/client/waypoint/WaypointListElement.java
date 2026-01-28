package dev.natowb.natosatlas.client.waypoint;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.access.PainterAccess;
import dev.natowb.natosatlas.client.ui.UIScaleInfo;
import dev.natowb.natosatlas.client.ui.UITheme;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static dev.natowb.natosatlas.client.texture.TextureProvider.*;

public class WaypointListElement {

    private static final int BTN_SIZE = 16;
    private static final int BTN_GAP = 4;
    private static final int BTN_Y_OFFSET = 7;

    private final int x, y, width, height;
    private final int entryHeight;

    private int scrollOffset = 0;

    public interface ClickHandler {
        void onEdit(Waypoint wp);

        void onDelete(Waypoint wp);
    }

    private ClickHandler handler;

    public WaypointListElement(int x, int y, int width, int height, int entryHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.entryHeight = entryHeight;
    }

    public void setHandler(ClickHandler handler) {
        this.handler = handler;
    }

    public void render(int mouseX, int mouseY, UIScaleInfo scaleInfo) {
        List<Waypoint> items = Waypoints.getAll();
        PainterAccess p = NACore.getClient().getPlatform().painter;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                x * scaleInfo.scaleFactor,
                (scaleInfo.scaledHeight - (y + height)) * scaleInfo.scaleFactor,
                width * scaleInfo.scaleFactor,
                height * scaleInfo.scaleFactor
        );

        int firstIndex = scrollOffset / entryHeight;
        int lastIndex = Math.min(items.size(), firstIndex + (height / entryHeight) + 1);

        int drawY = y - (scrollOffset % entryHeight);

        for (int i = firstIndex; i < lastIndex; i++) {
            Waypoint wp = items.get(i);
            renderEntry(p, wp, drawY, mouseX, mouseY);
            drawY += entryHeight;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        renderScrollbar(p, items.size());
    }


    private void renderEntry(PainterAccess p, Waypoint wp, int yPos, int mouseX, int mouseY) {
        boolean hovered = isMouseOver(x, yPos, width, entryHeight, mouseX, mouseY);

        int bgColor = hovered ? UITheme.LIST_BG_HOVER : UITheme.LIST_BG;
        p.drawRect(x, yPos, x + width, yPos + entryHeight, bgColor);

        int colorVisible = 0xFF000000 | (wp.color & 0xFFFFFF);
        int titleColor = wp.visible ? colorVisible : UITheme.LIST_SUBTEXT;
        p.drawString(wp.name, x + 5, yPos + 4, titleColor, false);
        p.drawString(
                "X: " + wp.x + "  Y: " + wp.y + "  Z: " + wp.z,
                x + 5,
                yPos + 16,
                UITheme.LIST_SUBTEXT,
                false
        );

        int buttonX = x + width - BTN_SIZE - BTN_GAP;
        int buttonY = yPos + BTN_Y_OFFSET;

        buttonX = renderButton(p, buttonX, buttonY, ICON_CROSS, mouseX, mouseY);
        buttonX = renderButton(p, buttonX, buttonY, ICON_COG, mouseX, mouseY);

        int visibilityIcon = wp.visible ? ICON_EYE_OPEN : ICON_EYE_CLOSED;
        renderButton(p, buttonX, buttonY, visibilityIcon, mouseX, mouseY);
    }

    private int renderButton(PainterAccess p, int x, int y, int icon, int mouseX, int mouseY) {
        boolean hover = isMouseOver(x, y, BTN_SIZE, BTN_SIZE, mouseX, mouseY);
        if (hover) {
            p.drawRect(x - 2, y - 2, x + BTN_SIZE + 2, y + BTN_SIZE + 2, UITheme.BUTTON_BG_HOVER);
        }

        p.drawIcon(icon, x, y, BTN_SIZE, 0xFFFFFFFF);
        return x - BTN_SIZE - BTN_GAP;
    }

    private void renderScrollbar(PainterAccess p, int itemCount) {
        int contentHeight = itemCount * entryHeight;
        if (contentHeight <= height) return;

        int barX1 = x + width + 2;
        int barX2 = barX1 + 6;

        float visibleRatio = (float) height / contentHeight;
        int thumbHeight = Math.max(20, (int) (visibleRatio * height));

        float scrollRatio = (float) scrollOffset / (contentHeight - height);
        int thumbY = y + (int) (scrollRatio * (height - thumbHeight));

        p.drawRect(barX1, y, barX2, y + height, UITheme.SCROLLBAR_BG);
        p.drawRect(barX1, thumbY, barX2, thumbY + thumbHeight, UITheme.SCROLLBAR_THUMB);
    }

    public void mouseScroll(int amount) {
        scrollOffset -= amount / 10;
        clampScroll();
    }

    public void mouseDown(int mouseX, int mouseY) {
        if (!isMouseOver(x, y, width, height, mouseX, mouseY)) {
            return;
        }

        List<Waypoint> items = Waypoints.getAll();
        int index = (mouseY - y + scrollOffset) / entryHeight;

        if (index < 0 || index >= items.size()) return;

        Waypoint wp = items.get(index);

        int entryY = y + (index * entryHeight) - scrollOffset;
        int buttonX = x + width - BTN_SIZE - BTN_GAP;
        int buttonY = entryY + BTN_Y_OFFSET;

        if (isMouseOver(buttonX, buttonY, BTN_SIZE, BTN_SIZE, mouseX, mouseY)) {
            if (handler != null) handler.onDelete(wp);
            return;
        }

        buttonX -= BTN_SIZE + BTN_GAP;
        if (isMouseOver(buttonX, buttonY, BTN_SIZE, BTN_SIZE, mouseX, mouseY)) {
            if (handler != null) handler.onEdit(wp);
            return;
        }

        buttonX -= BTN_SIZE + BTN_GAP;
        if (isMouseOver(buttonX, buttonY, BTN_SIZE, BTN_SIZE, mouseX, mouseY)) {
            wp.visible = !wp.visible;
            Waypoints.save();
        }
    }

    private void clampScroll() {
        int contentHeight = Waypoints.getAll().size() * entryHeight;
        scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, contentHeight - height)));
    }

    private boolean isMouseOver(int x, int y, int w, int h, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }
}
