package dev.natowb.natosatlas.client.ui.screens.settings;

import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.core.NAPaths;

import java.io.File;

public final class Settings {

    private static final SettingsStorage STORAGE = new SettingsStorage();

    public enum EntityDisplayMode {All, Player, Nothing}

    public enum MapRenderMode {Day, Night, Cave, Auto}

    //FIXME: i should probably seperate these out better.

    // World map settings
    public static boolean mapGrid = true;
    public static boolean debugInfo = false;
    public static boolean showSlimeChunks = false;
    public static float defaultZoom = 0.5f;
    public static EntityDisplayMode entityDisplayMode = EntityDisplayMode.All;
    public static MapRenderMode mapRenderMode = MapRenderMode.Auto;


    // Minimap settings
    public static boolean minimapRotateWithPlayer = false;
    public static boolean minimapEnabled = false;
    public static float minimapZoom = 0.5f;
    public static EntityDisplayMode minimapEntityDisplayMode = EntityDisplayMode.All;
    public static MapRenderMode minimapRenderMode = MapRenderMode.Auto;


    private Settings() {
    }

    public static void load() {
        STORAGE.load(new File(NAPaths.getDataPath().toFile(), "settings.txt"));
        LogUtil.debug("Loaded settings");
    }

    public static void save() {
        STORAGE.save(new File(NAPaths.getDataPath().toFile(), "settings.txt"));
        LogUtil.debug("Saved settings");
    }
}
