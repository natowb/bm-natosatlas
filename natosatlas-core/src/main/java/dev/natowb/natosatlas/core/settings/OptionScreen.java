package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.NAClientPlatform;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.chunk.ChunkBuilder;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementIconButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementOptionButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementSlider;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.ui.layout.UIVerticalLayout;
import dev.natowb.natosatlas.core.access.WorldAccess;

import static dev.natowb.natosatlas.core.texture.TextureProvider.ICON_BACK;

public class OptionScreen extends UIScreen {
    private int headerY;

    public OptionScreen(UIScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int headerHeight = 20;
        int headerGap = 10;
        int listHeight = height - 140;

        int totalHeight = headerHeight + headerGap + listHeight;
        int contentTop = (height - totalHeight) / 2;

        headerY = contentTop;

        UIElementIconButton closeButton = new UIElementIconButton(101, width / 2 - 100, headerY, 20, 20, ICON_BACK);
        closeButton.setHandler(btn -> {
            Settings.save();
            NACore.getClient().getPlatform().openNacScreen(parent);
        });

        addButton(closeButton);

        UILayout layout = new UIVerticalLayout(width / 2, headerY + headerHeight + headerGap, 5);

        UIElementOptionButton debugButton = new UIElementOptionButton(SettingsOption.DEBUG_INFO, layout, 150, 20);
        debugButton.setHandler(btn -> debugButton.cycle());
        addButton(debugButton);

        UIElementOptionButton reiButton = new UIElementOptionButton(SettingsOption.USE_REIMINIMAP_WAYPOINTS, layout, 150, 20);
        reiButton.setHandler(btn -> reiButton.cycle());
        addButton(reiButton);

        boolean isServer = WorldAccess.get().isServer();
        UIElementButton existingButton = new UIElementButton(102, layout, 150, 20, "Generate Existing", !isServer);
        existingButton.setHandler(btn -> ChunkBuilder.rebuildExistingChunks(MapStorage.get(),  NARegionCache.get()));
        addButton(existingButton);

        UIElementSlider zoomSlider = new UIElementSlider(103, layout, 150, 20, Settings.defaultZoom, "Default Zoom");
        zoomSlider.setRange(MapConfig.MIN_ZOOM, MapConfig.MAX_ZOOM);
        zoomSlider.setStep(0.01f);
        addSlider(zoomSlider);
    }


    @Override
    public void onSliderChanged(UIElementSlider slider) {
        if (slider.id == 103) {
            Settings.defaultZoom = slider.getValue();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        PainterAccess p = PainterAccess.get();

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Options", width / 2, headerY + 4, UITheme.TITLE_TEXT);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }
}
