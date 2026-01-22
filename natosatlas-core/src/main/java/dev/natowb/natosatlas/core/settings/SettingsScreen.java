package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.tasks.MapSaveScheduler;
import dev.natowb.natosatlas.core.tasks.MapSaveWorker;
import dev.natowb.natosatlas.core.tasks.MapUpdateScheduler;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementOptionButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementSlider;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.utils.LogUtil;

import java.io.File;
import java.util.List;

public class SettingsScreen extends UIScreen {


    private UIElementSlider zoomSlider;
    private UIElementButton doneButton;
    private UIElementButton generateExistingButton;

    public SettingsScreen(UIScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        int centerX = width / 2 - 75;
        int baseY = height / 6;
        int rowH = 24;

        addButton(new UIElementOptionButton(
                SettingsOption.MAP_GRID.ordinal(),
                centerX, baseY,
                150, 20,
                SettingsOption.MAP_GRID
        ));

        addButton(new UIElementOptionButton(
                SettingsOption.ENTITY_DISPLAY.ordinal(),
                centerX, baseY + rowH,
                150, 20,
                SettingsOption.ENTITY_DISPLAY
        ));

        addButton(new UIElementOptionButton(
                SettingsOption.DEBUG_INFO.ordinal(),
                centerX, baseY + rowH * 2,
                150, 20,
                SettingsOption.DEBUG_INFO
        ));


        addButton(new UIElementOptionButton(
                SettingsOption.USE_REIMINIMAP_WAYPOINTS.ordinal(),
                centerX, baseY + rowH * 3,
                150, 20,
                SettingsOption.USE_REIMINIMAP_WAYPOINTS
        ));

        generateExistingButton = new UIElementButton(201, centerX, baseY + rowH * 4, 150, 20, "Generate Existing");
        if (NatosAtlas.get().platform.worldProvider.getWorldInfo().isServer)
            generateExistingButton.active = false;

        addButton(generateExistingButton);

        float storedZoom = Settings.defaultZoom;
        float normalized = (storedZoom - MapConfig.MIN_ZOOM) / (MapConfig.MAX_ZOOM - MapConfig.MIN_ZOOM);

        zoomSlider = new UIElementSlider(
                3000,
                centerX, baseY + rowH * 5,
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
        );

        addSlider(zoomSlider);

        doneButton = new UIElementButton(200, width / 2 - 100, baseY + rowH * 6, 200, 20, "Done");

        addButton(doneButton);
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
        if(button instanceof UIElementOptionButton) {
            ((UIElementOptionButton) button).cycle();
            return;
        }

        if(button.id == doneButton.id) {
            Settings.save();
            NatosAtlas.get().platform.openNacScreen(parent);
            return;
        }

        if(button.id == generateExistingButton.id) {
            NatosAtlas.get().generateExistingChunks();
        }
    }


}
