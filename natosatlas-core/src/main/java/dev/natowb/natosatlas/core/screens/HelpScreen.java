package dev.natowb.natosatlas.core.screens;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.ui.layout.UIHorizontalLayout;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.access.PainterAccess;

public class HelpScreen extends UIScreen {

    private UIElementIconButton closeButton;
    private final PainterAccess painter = PainterAccess.get();

    String[][] entries = {
            {"[Left Mouse]", "Pan the map"},
            {"[Right Mouse]", "Rotate the map"},
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

        UILayout layout = new UIHorizontalLayout(5, 15, 5);

        closeButton = new UIElementIconButton(200, layout, 20, 20, 4);
        addButton(closeButton);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        painter.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        int centerX = width / 2;
        int y = 50;

        painter.drawCenteredString("Help & Controls", centerX, y, 0xFFFFFF);
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


    @Override
    public void onClick(UIElementButton btn) {
        if (btn.id == closeButton.id) {
            NatosAtlasCore.get().platform.openNacScreen(parent);
        }
    }
}
