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
        UILayout layout = new UIVerticalLayout(width / 2, height / 6, 5);

        addButton(new UIElementOptionButton(SettingsOption.MAP_GRID, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.ENTITY_DISPLAY, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.DEBUG_INFO, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.USE_REIMINIMAP_WAYPOINTS, layout, 150, 20));

        addButton(new UIElementButton(
                BUTTON_ID_GENERATE_EXISTING,
                layout,
                150, 20,
                "Generate Existing",
                !NatosAtlas.get().platform.worldProvider.getWorldInfo().isServer
        ));

        float storedZoom = Settings.defaultZoom;
        float normalized = (storedZoom - MapConfig.MIN_ZOOM) / (MapConfig.MAX_ZOOM - MapConfig.MIN_ZOOM);

        addSlider(new UIElementSlider(
                SLIDER_ID_ZOOM,
                layout,
                150, 20,
                normalized,
                "Default Zoom",
                v -> {
                    float actual = MapConfig.MIN_ZOOM + v * (MapConfig.MAX_ZOOM - MapConfig.MIN_ZOOM);
                    return String.format("%.0f%%", actual * 100f);
                },
                newValue -> {
                    Settings.defaultZoom =
                            MapConfig.MIN_ZOOM + newValue * (MapConfig.MAX_ZOOM - MapConfig.MIN_ZOOM);
                }
        ));

        addButton(new UIElementButton(BUTTON_ID_DONE, layout, 200, 20, "Done"));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Settings", width / 2, 20, UITheme.TITLE_TEXT);


        super.render(mouseX, mouseY, delta, scaleInfo);
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


}
