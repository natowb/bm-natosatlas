package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.config.*;

public final class NacSettings {

    public enum EntityDisplayMode {
        ALL,
        ONLY_PLAYER,
        NONE
    }

    public enum SelectedMapRenderer {
        Vanilla,
        Smooth
    }

    public static final NacEnumConfigOption<EntityDisplayMode> ENTITY_DISPLAY_MODE =
            new NacEnumConfigOption<>("entityDisplayMode", EntityDisplayMode.ALL);

    public static final NacEnumConfigOption<SelectedMapRenderer> MAP_RENDERER =
            new NacEnumConfigOption<>("renderMode", SelectedMapRenderer.Vanilla);

    public static final NacBoolConfigOption MAP_GRID =
            new NacBoolConfigOption("showGrid", true);

    public static final NacBoolConfigOption DEBUG_INFO =
            new NacBoolConfigOption("showDebugInfo", false);

    public static final NacFloatConfigOption DEFAULT_ZOOM =
            new NacFloatConfigOption("defaultZoom", 0.5f);



    public static NacConfigOption getOption(NacOption option) {
        switch (option) {
            case MAP_RENDERER:
                return MAP_RENDERER;
            case ENTITY_DISPLAY:
                return ENTITY_DISPLAY_MODE;
            case MAP_GRID:
                return MAP_GRID;
            case DEBUG_INFO:
                return DEBUG_INFO;
        }
        return null;
    }


    public static void load() {
        NacConfigStorage.load();
    }

    public static void save() {
        NacConfigStorage.save();
    }

    private NacSettings() {
    }
}
