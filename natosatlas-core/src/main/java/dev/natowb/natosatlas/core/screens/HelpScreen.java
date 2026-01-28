package dev.natowb.natosatlas.core.screens;

import dev.natowb.natosatlas.core.NAClientPlatform;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.access.PainterAccess;

import static dev.natowb.natosatlas.core.texture.TextureProvider.ICON_BACK;

public class HelpScreen extends UIScreen {

    private UIElementIconButton closeButton;
    private final PainterAccess painter = NACore.getClient().getPlatform().painter;

    private int headerY;

    String[][] entries = {
            {"[L Mouse Drag]", "Pan the map"},
            {"[R Mouse Drag]", "Rotate the map"},
            {"[L Mouse DBClick]", "Create Waypoint"},
            {"[Mouse Wheel]", "Zoom in/out"},
            {"[Space]", "Reset camera"},
            {"[P]", "Export map layers"}
    };

    public HelpScreen(UIScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int headerHeight = 20;
        int headerGap = 10;
        int contentHeight = entries.length * 18 + 40;

        int totalHeight = headerHeight + headerGap + contentHeight;
        int contentTop = (height - totalHeight) / 2;

        headerY = contentTop;

        closeButton = new UIElementIconButton(200, width / 2 - 100, headerY, 20, 20, ICON_BACK);
        closeButton.setHandler(btn -> NACore.getClient().getPlatform().openNacScreen(parent));
        addButton(closeButton);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        painter.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        int centerX = width / 2;
        int y = headerY + 4;

        painter.drawCenteredString("Help & Controls", centerX, y, UITheme.TITLE_TEXT);
        y += 30;

        int maxKeyWidth = 0;
        for (String[] e : entries) {
            int w = painter.getStringWidth(e[0]);
            if (w > maxKeyWidth) maxKeyWidth = w;
        }

        int gap = 12;

        for (String[] e : entries) {
            String key = e[0];
            String desc = e[1];

            int keyWidth = painter.getStringWidth(key);

            int keyRight = centerX - (gap / 2);
            int keyX = keyRight - keyWidth;

            int descX = centerX + (gap / 2);

            painter.drawString(key, keyX, y, 0xCCCCCC);
            painter.drawString(desc, descX, y, 0xCCCCCC);

            y += 18;
        }

        super.render(mouseX, mouseY, delta, scaleInfo);
    }
}