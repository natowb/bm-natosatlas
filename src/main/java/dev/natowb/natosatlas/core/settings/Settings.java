package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.utils.LogUtil;

public final class Settings {

    private static final SettingsStorage STORAGE = new SettingsStorage();


    public enum EntityDisplayMode {ALL, ONLY_PLAYER, NONE}

    public static boolean mapGrid = true;
    public static boolean debugInfo = false;
    public static float defaultZoom = 0.5f;
    public static EntityDisplayMode entityDisplayMode = EntityDisplayMode.ALL;

    private Settings() {}

    public static void load() {
        STORAGE.load();
        LogUtil.info("Settings", "loaded settings");
    }

    public static void save() {
        STORAGE.save();
        LogUtil.info("Settings", "saved settings");

    }
}
