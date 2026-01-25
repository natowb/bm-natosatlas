package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.NatosAtlasCore;
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

public class SettingsScreen extends UIScreen {

    private static final int SLIDER_ID_ZOOM = 3001;
    private static final int BUTTON_ID_GENERATE_EXISTING = 3002;
    private static final int BUTTON_ID_CLOSE = 3003;

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

        addButton(new UIElementIconButton(BUTTON_ID_CLOSE, width / 2 - 100, headerY, 20, 20, ICON_BACK));

        UILayout layout = new UIVerticalLayout(width / 2, headerY + headerHeight + headerGap, 5);

        addButton(new UIElementOptionButton(SettingsOption.MAP_GRID, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.ENTITY_DISPLAY, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.DEBUG_INFO, layout, 150, 20));
        addButton(new UIElementOptionButton(SettingsOption.USE_REIMINIMAP_WAYPOINTS, layout, 150, 20));

        boolean isServer = WorldAccess.get().isServer();
        addButton(new UIElementButton(BUTTON_ID_GENERATE_EXISTING, layout, 150, 20, "Generate Existing", !isServer));

        UIElementSlider zoomSlider = new UIElementSlider(SLIDER_ID_ZOOM, layout, 150, 20, Settings.defaultZoom, "Default Zoom");
        zoomSlider.setRange(MapConfig.MIN_ZOOM, MapConfig.MAX_ZOOM);
        zoomSlider.setStep(0.01f);
        addSlider(zoomSlider);
    }

    @Override
    protected void onClick(UIElementButton button) {
        if (button instanceof UIElementOptionButton) {
            ((UIElementOptionButton) button).cycle();
            return;
        }

        if (button.id == BUTTON_ID_CLOSE) {
            Settings.save();
            NatosAtlasCore.get().platform.openNacScreen(parent);
            return;
        }

        if (button.id == BUTTON_ID_GENERATE_EXISTING) {
            ChunkBuilder.rebuildExistingChunks(NatosAtlasCore.get().storage, NatosAtlasCore.get().cache);
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
        PainterAccess p = PainterAccess.get();

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Settings", width / 2, headerY + 4, UITheme.TITLE_TEXT);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }
}
