package dev.natowb.natosatlas.core.config;

public enum NacOption {

    MAP_RENDERER("options.mapRenderer", "Renderer", false, true),
    ENTITY_DISPLAY("options.entityDisplay", "Entities", false, true),
    MAP_GRID("options.mapGrid", "Grid", false, true),
    DEBUG_INFO("options.debugInfo", "Debug", false, true);

    private final boolean slider;
    private final boolean toggle;
    private final String key;
    private final String title;


    NacOption(String key, String title, boolean slider, boolean toggle) {
        this.key = key;
        this.slider = slider;
        this.toggle = toggle;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSlider() {
        return slider;
    }

    public boolean isToggle() {
        return toggle;
    }

    public int getId() {
        return ordinal();
    }

    public String getKey() {
        return key;
    }

    public static NacOption getById(int id) {
        for (NacOption o : values()) {
            if (o.ordinal() == id) return o;
        }
        return null;
    }
}
