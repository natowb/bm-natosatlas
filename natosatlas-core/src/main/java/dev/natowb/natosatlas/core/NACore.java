package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;
import dev.natowb.natosatlas.core.io.SaveScheduler;
import dev.natowb.natosatlas.core.layers.MapLayerHandler;
import dev.natowb.natosatlas.core.map.MapUpdater;
import dev.natowb.natosatlas.core.map.NARegionCache;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.access.WorldAccess;

public class NACore {

    private static NACore instance;

    public static NACore get() {
        return instance;
    }

    public final NatosAtlasPlatform platform;

    private MapUpdater mapUpdater;
    private boolean running;
    private String worldSaveName;
    private int dim;

    public boolean isStopped() {
        return !running;
    }

    public NACore(NatosAtlasPlatform platform) {
        if (instance != null)
            throw new IllegalStateException("NatosAtlas instance already created!");

        instance = this;
        this.platform = platform;

        LogUtil.setLoggingLevel(LogUtil.LogLevel.INFO);
        NAPaths.updateBasePaths(platform.getMinecraftDirectory());
        Settings.load();
    }

    public void onTick() {

        if (!WorldAccess.get().exists() && running) {
            running = false;
            onWorldLeft();
            return;
        }

        if (WorldAccess.get().exists() && !running) {
            running = true;
            dim = WorldAccess.get().getDimensionId();
            worldSaveName = WorldAccess.get().getSaveName();
            onWorldJoined(worldSaveName, dim);
        }

        if (!running) return;

        int currentDim = WorldAccess.get().getDimensionId();
        if (dim != currentDim) {
            dim = currentDim;
            onDimensionChange(dim);
            return;
        }

        onWorldTick();
    }

    private void onWorldJoined(String worldSaveName, int dim) {
        NAPaths.updateWorldPath(worldSaveName);
        Waypoints.load();
        SaveScheduler.start();

        mapUpdater = new MapUpdater(MapLayerHandler.get(), NARegionCache.get());

        LogUtil.info("Joined world={} dim={}", worldSaveName, dim);
    }

    private void onWorldLeft() {
        SaveScheduler.stop();
        NARegionCache.get().clear();

        mapUpdater = null;

        LogUtil.info("Left world {}", worldSaveName);
    }

    private void onDimensionChange(int newDim) {
        LogUtil.info("Dimension changed to {}", newDim);

        NARegionCache.get().clear();
        mapUpdater = new MapUpdater(MapLayerHandler.get(), NARegionCache.get());
    }

    private void onWorldTick() {
        mapUpdater.tick();
        SaveScheduler.tick();
        MapLayerHandler.get().tick();
    }
}