package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.persistence.TextStorage;
import dev.natowb.natosatlas.core.utils.NAPaths;

import java.io.File;

public final class SettingsStorage extends TextStorage {


    @Override
    protected String getName() {
        return "Settings";
    }

    @Override
    protected void onLoad() {
        Settings.mapGrid = getBoolean("mapGrid", true);
        Settings.debugInfo = getBoolean("debugInfo", false);
        Settings.defaultZoom = getFloat("defaultZoom", 1f);
        Settings.entityDisplayMode = getEnum("entityDisplayMode", Settings.EntityDisplayMode.class, Settings.EntityDisplayMode.Player);
        Settings.mapRenderMode = getEnum("mapRenderMode", Settings.MapRenderMode.class, Settings.MapRenderMode.Auto);
    }

    @Override
    protected void onSave() {
        put("mapGrid", Settings.mapGrid);
        put("debugInfo", Settings.debugInfo);
        put("defaultZoom", Settings.defaultZoom);
        put("entityDisplayMode", Settings.entityDisplayMode.name());
        put("mapRenderMode", Settings.mapRenderMode.name());
    }
}
