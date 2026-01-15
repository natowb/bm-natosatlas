package dev.natowb.natosatlas.core.screens;

import dev.natowb.natosatlas.core.NacCanvas;
import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.NacSettings;
import dev.natowb.natosatlas.core.config.NacOption;
import dev.natowb.natosatlas.core.gui.NacGuiButton;
import dev.natowb.natosatlas.core.gui.NacGuiOptionButton;
import dev.natowb.natosatlas.core.gui.NacGuiSlider;
import dev.natowb.natosatlas.core.gui.NacGuiTheme;
import dev.natowb.natosatlas.core.painter.INacPainter;

import java.util.ArrayList;
import java.util.List;

public class NacSettingsScreen extends NacScreen {

    private static final NacOption[] OPTIONS = {
            NacOption.MAP_RENDERER,
            NacOption.ENTITY_DISPLAY,
            NacOption.MAP_GRID,
            NacOption.DEBUG_INFO
    };

    private final List<NacGuiOptionButton> optionButtons = new ArrayList<>();

    private NacGuiSlider zoomSlider;
    private NacGuiButton doneButton;

    public NacSettingsScreen(NacScreen parent) {
        super(parent);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        optionButtons.clear();

        int index = 0;

        for (NacOption opt : OPTIONS) {
            int x = width / 2 - 155 + (index % 2) * 160;
            int y = height / 6 + 24 * (index / 2);

            optionButtons.add(new NacGuiOptionButton(
                    opt.getId(),
                    x, y,
                    150, 20,
                    opt
            ));

            index++;
        }

        float storedZoom = NacSettings.DEFAULT_ZOOM.getValue();

        float normalized = (storedZoom - NacCanvas.MIN_ZOOM) /
                (NacCanvas.MAX_ZOOM - NacCanvas.MIN_ZOOM);

        zoomSlider = new NacGuiSlider(
                3000,
                width / 2 - 75,
                height / 6 + 120,
                150,
                16,
                normalized,
                "Default Zoom",
                v -> {
                    float actual = NacCanvas.MIN_ZOOM + v * (NacCanvas.MAX_ZOOM - NacCanvas.MIN_ZOOM);
                    return String.format("%.0f%%", actual * 100f);
                },
                newValue -> {
                    float actual = NacCanvas.MIN_ZOOM + newValue * (NacCanvas.MAX_ZOOM - NacCanvas.MIN_ZOOM);
                    NacSettings.DEFAULT_ZOOM.setValue(actual);
                }
        );


        doneButton = new NacGuiButton(
                200,
                width / 2 - 100,
                height / 6 + 168,
                200, 20,
                "Done"
        );
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        INacPainter p = NacPlatformAPI.get().painter;

        p.drawRect(0, 0, width, height, NacGuiTheme.PANEL_BG);
        p.drawCenteredString("Settings", width / 2, 20, NacGuiTheme.TITLE_TEXT);

        for (NacGuiOptionButton b : optionButtons) {
            b.render(mouseX, mouseY);
        }

        zoomSlider.render(mouseX, mouseY);
        doneButton.render(mouseX, mouseY);
    }

    @Override
    public void mouseDown(int mouseX, int mouseY, int button) {
        if (button != 0) return;

        if (zoomSlider.mouseDown(mouseX, mouseY)) return;

        for (NacGuiOptionButton b : optionButtons) {
            if (b.handleClick(mouseX, mouseY)) {
                b.cycle();
                return;
            }
        }

        if (doneButton.handleClick(mouseX, mouseY)) {
            NacSettings.save();
            NacPlatformAPI.get().openNacScreen(parent);
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
        for (NacGuiOptionButton b : optionButtons) {
            b.resetClickState();
        }
        doneButton.resetClickState();
    }
}
