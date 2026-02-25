package dev.natowb.natosatlas.client.ui.screens.settings;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.client.ui.screens.generate.GenerateScreen;
import dev.natowb.natosatlas.client.ui.screens.map.MapConfig;
import dev.natowb.natosatlas.client.platform.NAPainter;
import dev.natowb.natosatlas.client.ui.elements.*;
import dev.natowb.natosatlas.client.ui.themes.UITheme;
import dev.natowb.natosatlas.client.ui.layout.UILayout;
import dev.natowb.natosatlas.client.ui.layout.UIVerticalLayout;

import static dev.natowb.natosatlas.client.texture.TextureProvider.ICON_BACK;

public class SettingsScreen extends UIScreen {

    private int headerY;

    private enum Tab {
        GENERAL,
        ATLAS,
        MINIMAP
    }

    private Tab activeTab = Tab.GENERAL;

    public SettingsScreen(UIScreen parent) {
        super(parent);
    }

    private UIElementButton generalTab;
    private UIElementButton worldMapTab;
    private UIElementButton minimapTab;


    @Override
    public void init(int width, int height) {
        super.init(width, height);

        int headerHeight = 20;
        int headerGap = 10;
        int listHeight = height - 140;

        int totalHeight = headerHeight + headerGap + listHeight;
        int contentTop = (height - totalHeight) / 2;

        headerY = contentTop;

        UIElementIconButton closeButton =
                new UIElementIconButton(101, width / 2 - 100, headerY, 20, 20, ICON_BACK);
        closeButton.setHandler(btn -> {
            Settings.save();
            NAClient.get().getPlatform().screen.openNacScreen(parent);
        });
        addButton(closeButton);

        int tabY = headerY + headerHeight + 5;

        int tabButtonWidth = 80;
        int tabButtonHeight = 20;
        int tabSpacing = 10;

        int tabWidth = (tabButtonWidth * 3) + (tabSpacing * 2);

        int tabX = (width - tabWidth) / 2;

        generalTab = new UIElementButton(201, tabX, tabY, tabButtonWidth, tabButtonHeight, "General");
        generalTab.setHandler(btn -> switchTab(Tab.GENERAL));
        addButton(generalTab);

        worldMapTab = new UIElementButton(202, tabX + tabButtonWidth + tabSpacing, tabY, tabButtonWidth, tabButtonHeight, "World map");
        worldMapTab.setHandler(btn -> switchTab(Tab.ATLAS));
        addButton(worldMapTab);

        minimapTab = new UIElementButton(203, tabX + (tabButtonWidth + tabSpacing) * 2, tabY, tabButtonWidth, tabButtonHeight, "Minimap");
        minimapTab.setHandler(btn -> switchTab(Tab.MINIMAP));
        addButton(minimapTab);

        updateTabActiveStates();


        rebuildContent();
    }

    private void switchTab(Tab tab) {
        this.activeTab = tab;
        updateTabActiveStates();
        rebuildContent();
    }

    private void updateTabActiveStates() {
        generalTab.active = !(activeTab == Tab.GENERAL);
        worldMapTab.active = !(activeTab == Tab.ATLAS);
        minimapTab.active = !(activeTab == Tab.MINIMAP);
    }


    private void rebuildContent() {
        buttons.removeIf(b -> b.id >= 300);
        sliders.removeIf(s -> s.id >= 300);

        int listTop = headerY + 50;
        UILayout layout = new UIVerticalLayout(width / 2, listTop, 5);

        switch (activeTab) {
            case GENERAL:
                buildGeneralSettings(layout);
                break;

            case ATLAS:
                buildAtlasSettings(layout);
                break;

            case MINIMAP:
                buildMinimapSettings(layout);
                break;
        }
    }

    private void buildGeneralSettings(UILayout layout) {
        UIElementOptionButton debugButton =
                new UIElementOptionButton(SettingsOption.DEBUG_INFO, layout, 150, 20);
        debugButton.setHandler(btn -> debugButton.cycle());
        debugButton.id = 300;
        addButton(debugButton);
        boolean isMultiplayer = ClientWorldAccess.get().getWorldInfo().isMultiplayer();
        UIElementButton existingButton =
                new UIElementButton(302, layout, 150, 20, "Generate Existing", !isMultiplayer);
        existingButton.setHandler(btn ->
                NAClient.get().getPlatform().screen.openNacScreen(new GenerateScreen(this)));
        addButton(existingButton);
    }

    private void buildAtlasSettings(UILayout layout) {
        UIElementSlider zoomSlider =
                new UIElementSlider(303, layout, 150, 20, Settings.defaultZoom, "Default Zoom");
        zoomSlider.setRange(MapConfig.MIN_ZOOM, MapConfig.MAX_ZOOM);
        zoomSlider.setStep(0.01f);
        addSlider(zoomSlider);
    }

    private void buildMinimapSettings(UILayout layout) {
        UIElementOptionButton enableButton =
                new UIElementOptionButton(SettingsOption.MINIMAP_ENABLED, layout, 150, 20);
        enableButton.setHandler(btn -> enableButton.cycle());
        enableButton.id = 307;
        addButton(enableButton);

        UIElementOptionButton rotateButton =
                new UIElementOptionButton(SettingsOption.MINIMAP_ROTATE, layout, 150, 20);
        rotateButton.setHandler(btn -> rotateButton.cycle());
        rotateButton.id = 304;
        addButton(rotateButton);

        UIElementOptionButton entityButton =
                new UIElementOptionButton(SettingsOption.MINIMAP_ENTITY_DISPLAY, layout, 150, 20);
        entityButton.setHandler(btn -> entityButton.cycle());
        entityButton.id = 305;
        addButton(entityButton);

        UIElementOptionButton renderButton =
                new UIElementOptionButton(SettingsOption.MINIMAP_MAP_RENDER_MODE, layout, 150, 20);
        renderButton.setHandler(btn -> renderButton.cycle());
        renderButton.id = 306;
        addButton(renderButton);

        UIElementSlider minimapZoom =
                new UIElementSlider(305, layout, 150, 20, Settings.minimapZoom, "Minimap Zoom");
        minimapZoom.setRange(MapConfig.MIN_ZOOM, MapConfig.MAX_ZOOM);
        minimapZoom.setStep(0.01f);
        addSlider(minimapZoom);
    }

    @Override
    public void onSliderChanged(UIElementSlider slider) {
        switch (slider.id) {
            case 303:
                Settings.defaultZoom = slider.getValue();
                break;
            case 305:
                Settings.minimapZoom = slider.getValue();
                break;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        NAPainter p = NAClient.get().getPlatform().painter;

        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);
        p.drawCenteredString("Settings", width / 2, headerY + 4, UITheme.TITLE_TEXT);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }
}
