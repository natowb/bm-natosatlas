package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;

import java.io.File;

public final class Settings {

    private static final SettingsStorage STORAGE = new SettingsStorage();


    public enum EntityDisplayMode {All, Player, Nothing}

    public enum MapRenderMode {Day, Night, Auto}

    public static boolean mapGrid = true;
    public static boolean debugInfo = false;
    public static float defaultZoom = 0.5f;
    public static EntityDisplayMode entityDisplayMode = EntityDisplayMode.All;
    public static MapRenderMode mapRenderMode = MapRenderMode.Auto;

    private Settings() {
    }

    public static void load() {
        STORAGE.load(new File(NAPaths.getDataPath().toFile(), "settings.txt"));
        LogUtil.info("Loaded settings");
    }

    public static void save() {
        STORAGE.save(new File(NAPaths.getDataPath().toFile(), "settings.txt"));
        LogUtil.info("Saved settings");
    }
}
