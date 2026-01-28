package dev.natowb.natosatlas.core.waypoint;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;

import static dev.natowb.natosatlas.core.texture.TextureProvider.ICON_BACK;
import static dev.natowb.natosatlas.core.texture.TextureProvider.ICON_PLUS;

public class WaypointListScreen extends UIScreen {

    private WaypointListElement list;
    private UIElementIconButton createButton;
    private UIElementIconButton backButton;

    private int headerY;

    public WaypointListScreen(UIScreen parent) {
        super(parent);
        Waypoints.load();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int maxListWidth = 300;
        int listWidth = Math.min(width - 40, maxListWidth);
        int listX = (width - listWidth) / 2;

        int headerHeight = 20;
        int headerGap = 10;
        int listHeight = height - 140;

        int totalHeight = headerHeight + headerGap + listHeight;
        int contentTop = (height - totalHeight) / 2;

        headerY = contentTop;
        int listY = headerY + headerHeight + headerGap;

        backButton = new UIElementIconButton(1000, listX, headerY, 20, 20, ICON_BACK);
        backButton.setHandler(btn -> NACore.get().platform.openNacScreen(parent));
        addButton(backButton);

        createButton = new UIElementIconButton(1001, listX + listWidth - 20, headerY, 20, 20, ICON_PLUS);
        createButton.setHandler(btn -> NACore.get().platform.openNacScreen(new WaypointCreateScreen(this)));
        addButton(createButton);


        list = new WaypointListElement(listX, listY, listWidth, listHeight, 30);
        list.setHandler(new WaypointListElement.ClickHandler() {
            @Override
            public void onEdit(Waypoint wp) {
                NACore.get().platform.openNacScreen(new WaypointCreateScreen(WaypointListScreen.this, wp));
            }

            @Override
            public void onDelete(Waypoint wp) {
                Waypoints.remove(wp);
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        PainterAccess p = PainterAccess.get();

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        if (Settings.useReiMinimapWaypointStorage) {
            p.drawCenteredString("Use Rei's Minimap to manage waypoints", width / 2, height / 2, UITheme.TITLE_TEXT);
            return;
        }

        p.drawCenteredString("Waypoints", width / 2, headerY + 4, UITheme.TITLE_TEXT);

        list.render(mouseX, mouseY, scaleInfo);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }

    @Override
    public void mouseScroll(int amount) {
        if (Settings.useReiMinimapWaypointStorage) return;
        list.mouseScroll(amount);
    }

    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (Settings.useReiMinimapWaypointStorage) return;
        if (button != 0) return;
        list.mouseDown(mouseX, mouseY);
    }

    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {
        if (Settings.useReiMinimapWaypointStorage) return;
        super.mouseUp(mouseX, mouseY, button);
    }
}
