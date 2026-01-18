package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.ui.elements.UIElementButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementOptionButton;
import dev.natowb.natosatlas.core.ui.elements.UIElementSlider;
import dev.natowb.natosatlas.core.ui.UITheme;
import dev.natowb.natosatlas.core.platform.PlatformPainter;
import dev.natowb.natosatlas.core.map.MapConfig;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;

import java.util.ArrayList;
import java.util.List;

public class SettingsScreen extends UIScreen {

    private final List<UIElementOptionButton> optionButtons = new ArrayList<>();

    private UIElementSlider zoomSlider;
    private UIElementButton doneButton;
    private UIElementButton generateExistingButton;

    public SettingsScreen(UIScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        optionButtons.clear();

        int centerX = width / 2 - 75;
        int baseY = height / 6;
        int rowH = 24;

        optionButtons.add(new UIElementOptionButton(
                SettingsOption.MAP_GRID.ordinal(),
                centerX, baseY,
                150, 20,
                SettingsOption.MAP_GRID
        ));

        optionButtons.add(new UIElementOptionButton(
                SettingsOption.ENTITY_DISPLAY.ordinal(),
                centerX, baseY + rowH,
                150, 20,
                SettingsOption.ENTITY_DISPLAY
        ));

        optionButtons.add(new UIElementOptionButton(
                SettingsOption.DEBUG_INFO.ordinal(),
                centerX, baseY + rowH * 2,
                150, 20,
                SettingsOption.DEBUG_INFO
        ));

        generateExistingButton = new UIElementButton(201, centerX, baseY + rowH * 3, 150, 20, "Generate Existing");
        if (NatosAtlas.get().platform.worldProvider.getWorldInfo().isServer)
            generateExistingButton.active = false;

        float storedZoom = Settings.defaultZoom;
        float normalized = (storedZoom - MapConfig.MIN_ZOOM) / (MapConfig.MAX_ZOOM - MapConfig.MIN_ZOOM);

        zoomSlider = new UIElementSlider(
                3000,
                centerX, baseY + rowH * 4,
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

        doneButton = new UIElementButton(200, width / 2 - 100, baseY + rowH * 6, 200, 20, "Done");
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        PlatformPainter p = NatosAtlas.get().platform.painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Settings", width / 2, 20, UITheme.TITLE_TEXT);

        for (UIElementOptionButton b : optionButtons) {
            b.render(mouseX, mouseY);
        }

        zoomSlider.render(mouseX, mouseY);
        generateExistingButton.render(mouseX, mouseY);
        doneButton.render(mouseX, mouseY);
    }

    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        if (zoomSlider.mouseDown(mouseX, mouseY)) return;

        for (UIElementOptionButton b : optionButtons) {
            if (b.handleClick(mouseX, mouseY)) {
                b.cycle();
                return;
            }
        }

        if (doneButton.handleClick(mouseX, mouseY)) {
            Settings.save();
            NatosAtlas.get().platform.openNacScreen(parent);
        }

        if (generateExistingButton.handleClick(mouseX, mouseY)) {
            NatosAtlas.get().platform.worldProvider.generateExistingChunks();
        }
    }

    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {
        zoomSlider.mouseUp();
    }

    @Override
    public void mouseDrag(int mouseX, int mouseY, int button) {
        if (button == 0) {
            zoomSlider.mouseDrag(mouseX);
        }
    }

    @Override
    public void resetAllButtonsClickState() {
        for (UIElementOptionButton b : optionButtons) {
            b.resetClickState();
        }
        doneButton.resetClickState();
    }
}
