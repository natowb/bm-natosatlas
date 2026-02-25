package dev.natowb.natosatlas.client.ui.screens.settings;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.client.ui.screens.generate.GenerateScreen;
import dev.natowb.natosatlas.client.ui.screens.map.MapConfig;
import dev.natowb.natosatlas.client.platform.NAPainter;
import dev.natowb.natosatlas.client.ui.elements.UIScaleInfo;
import dev.natowb.natosatlas.client.ui.elements.*;
import dev.natowb.natosatlas.client.ui.themes.UITheme;
import dev.natowb.natosatlas.client.ui.layout.UILayout;
import dev.natowb.natosatlas.client.ui.layout.UIVerticalLayout;

import static dev.natowb.natosatlas.client.texture.TextureProvider.ICON_BACK;

public class SettingsScreen extends UIScreen {
    private int headerY;

    public SettingsScreen(UIScreen parent) {
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
            NAClient.get().getPlatform().screen.openNacScreen(parent);
        });

        addButton(closeButton);

        UILayout layout = new UIVerticalLayout(width / 2, headerY + headerHeight + headerGap, 5);

        UIElementOptionButton debugButton = new UIElementOptionButton(SettingsOption.DEBUG_INFO, layout, 150, 20);
        debugButton.setHandler(btn -> debugButton.cycle());
        addButton(debugButton);

        UIElementOptionButton reiButton = new UIElementOptionButton(SettingsOption.USE_REIMINIMAP_WAYPOINTS, layout, 150, 20);
        reiButton.setHandler(btn -> reiButton.cycle());
        addButton(reiButton);

        boolean isMultiplayer = ClientWorldAccess.get().getWorldInfo().isMultiplayer();
        UIElementButton existingButton = new UIElementButton(102, layout, 150, 20, "Generate Existing", !isMultiplayer);
        // FIXME: this is a pure hack lets make a proper flow for doing this
        existingButton.setHandler(btn -> {
            NAClient.get().getPlatform().screen.openNacScreen(new GenerateScreen(this));
        });
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
        NAPainter p = NAClient.get().getPlatform().painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Options", width / 2, headerY + 4, UITheme.TITLE_TEXT);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }
}
