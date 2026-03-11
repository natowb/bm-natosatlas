package dev.natowb.natosatlas.client.ui.screens.settings;

import dev.natowb.natosatlas.core.storage.TextStorage;


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

        Settings.minimapRotateWithPlayer = getBoolean("minimapRotateWithPlayer", false);
        Settings.minimapZoom = getFloat("minimapZoom", 1f);
        Settings.minimapEntityDisplayMode = getEnum("minimapEntityDisplayMode", Settings.EntityDisplayMode.class, Settings.EntityDisplayMode.Player);
        Settings.minimapRenderMode = getEnum("minimapRenderMode", Settings.MapRenderMode.class, Settings.MapRenderMode.Auto);
        Settings.minimapEnabled = getBoolean("minimapEnabled", false);
        Settings.minimapPosition = getEnum("minimapPosition", Settings.MinimapPosition.class, Settings.MinimapPosition.TopRight);
        Settings.minimapScale = getFloat("minimapScale", 1f);


    }

    @Override
    protected void onSave() {
        put("mapGrid", Settings.mapGrid);
        put("debugInfo", Settings.debugInfo);
        put("defaultZoom", Settings.defaultZoom);
        put("entityDisplayMode", Settings.entityDisplayMode.name());
        put("mapRenderMode", Settings.mapRenderMode.name());
        put("showSlimeChunks", Settings.showSlimeChunks);

        put("minimapEnabled", Settings.minimapEnabled);
        put("minimapRotateWithPlayer", Settings.minimapRotateWithPlayer);
        put("minimapZoom", Settings.minimapZoom);
        put("minimapEntityDisplayMode", Settings.minimapEntityDisplayMode);
        put("minimapRenderMode", Settings.minimapRenderMode);
        put("minimapScale", Settings.minimapScale);
        put("minimapPosition", Settings.minimapPosition);
    }
}
