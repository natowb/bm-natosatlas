package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;

import java.io.File;

public final class Settings {

    private static final SettingsStorage STORAGE = new SettingsStorage();


    public enum EntityDisplayMode {All, Player, Nothing}

    public enum MapRenderMode {Day, Night, Cave, Auto}

    public static boolean mapGrid = true;
    public static boolean debugInfo = false;
    public static boolean showSlimeChunks = false;
    public static float defaultZoom = 0.5f;
    public static boolean useReiMinimapWaypointStorage = false;
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
