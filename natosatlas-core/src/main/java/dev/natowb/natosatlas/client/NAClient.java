package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.map.MapLayerController;
import dev.natowb.natosatlas.client.settings.Settings;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.NASession;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;
import dev.natowb.natosatlas.core.io.SaveScheduler;
import dev.natowb.natosatlas.client.map.MapUpdater;
import dev.natowb.natosatlas.core.cache.NARegionPixelCache;
import dev.natowb.natosatlas.client.waypoint.Waypoints;

public class NAClient implements NASession {

    private boolean inWorld;
    private String worldSaveName;
    private int dim;
    private final NAClientPlatform platform;
    private final MapLayerController layerController = new MapLayerController();

    public NAClient(NAClientPlatform platform) {
        this.platform = platform;
        Settings.load();
    }

    public MapLayerController getLayerController() {
        return layerController;
    }

    public NAClientPlatform getPlatform() {
        return platform;
    }


    @Override
    public void tick() {
        boolean worldExists = platform.world.exists();

        if (!worldExists && inWorld) {
            inWorld = false;
            onWorldLeft();
            return;
        }

        if (worldExists && !inWorld) {
            inWorld = true;
            dim = NACore.getClient().getPlatform().world.getDimensionId();
            worldSaveName = NACore.getClient().getPlatform().world.getSaveName();
            onWorldJoined(worldSaveName, dim);
        }

        if (!inWorld) return;

        int currentDim = NACore.getClient().getPlatform().world.getDimensionId();
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

        LogUtil.info("Client joined world={} dim={}", worldSaveName, dim);
    }

    private void onWorldLeft() {
        SaveScheduler.stop();
        NARegionPixelCache.get().clear();
        LogUtil.info("Client left world {}", worldSaveName);
    }

    private void onDimensionChange(int newDim) {
        LogUtil.info("Client dimension changed to {}", newDim);
        NARegionPixelCache.get().clear();
    }

    private void onWorldTick() {
        MapUpdater.get().tick();
        SaveScheduler.tick();
        layerController.tick();
    }
}
