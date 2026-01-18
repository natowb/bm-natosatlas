package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.utils.LogUtil;

public final class Settings {

    private static final SettingsStorage STORAGE = new SettingsStorage();


    public enum EntityDisplayMode {All, Player, Nothing}

    public enum MapRenderMode {Day, Night, Auto}

    public static boolean mapGrid = true;
    public static boolean debugInfo = false;
    public static float defaultZoom = 0.5f;
    public static EntityDisplayMode entityDisplayMode = EntityDisplayMode.All;
    public static MapRenderMode mapRenderMode = MapRenderMode.Auto;
    public static boolean slimeChunk = false;


    private Settings() {
    }

    public static void load() {
        STORAGE.load();
        LogUtil.info("Loaded settings");
    }

    public static void save() {
        STORAGE.save();
        LogUtil.info("Saved settings");
    }
}
