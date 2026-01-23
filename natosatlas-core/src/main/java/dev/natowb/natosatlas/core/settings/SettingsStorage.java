package dev.natowb.natosatlas.core.settings;

import dev.natowb.natosatlas.core.persistence.TextStorage;


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
        Settings.showSlimeChunks = getBoolean("showSlimeChunks", false);
        Settings.useReiMinimapWaypointStorage = getBoolean("useReiMinimapWaypointStorage", false);
    }

    @Override
    protected void onSave() {
        put("mapGrid", Settings.mapGrid);
        put("debugInfo", Settings.debugInfo);
        put("defaultZoom", Settings.defaultZoom);
        put("entityDisplayMode", Settings.entityDisplayMode.name());
        put("mapRenderMode", Settings.mapRenderMode.name());
        put("showSlimeChunks", Settings.showSlimeChunks);
        put("useReiMinimapWaypointStorage", Settings.useReiMinimapWaypointStorage);
    }
}
