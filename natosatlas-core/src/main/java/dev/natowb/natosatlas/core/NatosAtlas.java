package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.layers.MapLayerManager;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.map.MapUpdater;
import dev.natowb.natosatlas.core.tasks.MapSaveScheduler;
import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.access.WorldAccess;


public class NatosAtlas {


    private static NatosAtlas instance = null;

    public static NatosAtlas get() {
        return instance;
    }

    public final Platform platform;


    private MapUpdater mapUpdater;
    public final MapStorage storage = new MapStorage();
    public final MapCache cache = new MapCache(storage);
    public final MapTextureProvider textures = new MapTextureProvider();
    public final MapLayerManager layers = new MapLayerManager();


    private String worldSaveName;

    private boolean running;

    public boolean isStopped() {
        return !running;
    }

    public NatosAtlas(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }

        instance = this;
        this.platform = platform;

        NAPaths.updateBasePaths(platform.getMinecraftDirectory());
        Settings.load();
    }


    public void onTick() {

        if (!WorldAccess.getInstance().exists() && running) {
            running = false;
            onLeave();
        }

        if (WorldAccess.getInstance().exists() && !running) {
            onJoin();
        }

        if (!running) return;

        mapUpdater.tick();
        MapSaveScheduler.tick();
        layers.tick();
    }

    private void onJoin() {
        worldSaveName = WorldAccess.getInstance().getSaveName();

        if (worldSaveName == null) {
            LogUtil.error("joined invalid world");
            return;
        }
        LogUtil.info("Joined world saveName={}", worldSaveName);

        running = true;
        mapUpdater = new MapUpdater(layers, cache);
        NAPaths.updateWorldPath(worldSaveName);
        Waypoints.load();
        MapSaveScheduler.start();
    }

    private void onLeave() {
        LogUtil.info("Left world: {}", worldSaveName);
        MapSaveScheduler.stop();
        cache.clear();
        worldSaveName = null;
        mapUpdater = null;
        running = false;
    }
}