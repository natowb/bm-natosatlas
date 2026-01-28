package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.access.WorldAccess;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;
import dev.natowb.natosatlas.core.io.SaveScheduler;
import dev.natowb.natosatlas.core.layers.MapLayerHandler;
import dev.natowb.natosatlas.core.map.MapUpdater;
import dev.natowb.natosatlas.core.map.NARegionCache;
import dev.natowb.natosatlas.core.waypoint.Waypoints;

public class NAClient implements NASession {

    private boolean inWorld;
    private String worldSaveName;
    private int dim;
    private final NAClientPlatform platform;


    public NAClient(NAClientPlatform platform) {
        this.platform = platform;
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
        NARegionCache.get().clear();
        LogUtil.info("Client left world {}", worldSaveName);
    }

    private void onDimensionChange(int newDim) {
        LogUtil.info("Client dimension changed to {}", newDim);
        NARegionCache.get().clear();
    }

    private void onWorldTick() {
        MapLayerHandler.get().tick();
        MapUpdater.get().tick();
        SaveScheduler.tick();
    }
}

