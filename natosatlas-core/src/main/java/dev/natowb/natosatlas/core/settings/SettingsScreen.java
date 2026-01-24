package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementOptionButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementSlider;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.ui.layout.UILayout;
import dev.natowb.natosatlas.core.ui.layout.UIVerticalLayout;

public class SettingsScreen extends UIScreen {
    public SettingsScreen(UIScreen parent) {
        super(parent);
    }

    private static final int BUTTON_ID_DONE = 3000;
    private static final int SLIDER_ID_ZOOM = 3001;
    private static final int BUTTON_ID_GENERATE_EXISTING = 3002;

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        UILayout layout = new UIVerticalLayout(width / 2, height / 6, 5);

        addButton(new UIElementOptionButton(SettingsOption.MAP_GRID, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.ENTITY_DISPLAY, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.DEBUG_INFO, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.USE_REIMINIMAP_WAYPOINTS, layout, 150, 20));

        boolean isServer = NatosAtlas.get().getCurrentWorld().isServer();
        addButton(new UIElementButton(BUTTON_ID_GENERATE_EXISTING, layout, 150, 20, "Generate Existing", !isServer));

        UIElementSlider zoomSlider = new UIElementSlider(SLIDER_ID_ZOOM, layout, 150, 20, Settings.defaultZoom, "Default Zoom");
        zoomSlider.setRange(MapConfig.MIN_ZOOM, MapConfig.MAX_ZOOM);
        zoomSlider.setStep(0.01f);
        addSlider(zoomSlider);

        addButton(new UIElementButton(BUTTON_ID_DONE, layout, 200, 20, "Done"));
    }


    @Override
    protected void onClick(UIElementButton button) {
        if (button instanceof UIElementOptionButton) {
            ((UIElementOptionButton) button).cycle();
            return;
        }

        if (button.id == BUTTON_ID_DONE) {
            Settings.save();
            NatosAtlas.get().platform.openNacScreen(parent);
            return;
        }

        if (button.id == BUTTON_ID_GENERATE_EXISTING) {
            NatosAtlas.get().generateExistingChunks();
        }
    }


    @Override
    public void onSliderChanged(UIElementSlider slider) {
        if (slider.id == SLIDER_ID_ZOOM) {
            Settings.defaultZoom = slider.getValue();
        }
    }


    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Settings", width / 2, 20, UITheme.TITLE_TEXT);
        super.render(mouseX, mouseY, delta, scaleInfo);
    }

}
