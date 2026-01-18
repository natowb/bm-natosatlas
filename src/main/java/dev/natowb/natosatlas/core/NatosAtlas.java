package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NAWorldInfo;
import dev.natowb.natosatlas.core.map.MapManager;
import dev.natowb.natosatlas.core.tasks.MapSaveWorker;
import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.tasks.MapUpdateWorker;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.waypoint.Waypoints;

public class NatosAtlas {

    private static NatosAtlas instance = null;

    public static NatosAtlas get() {
        return instance;
    }

    public final Platform platform;
    public final MapManager regionManager;
    public NAWorldInfo worldInfo;

    public NatosAtlas(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }
        LogUtil.info("Initializing NatosAtlas core");
        instance = this;
        this.platform = platform;
        this.regionManager = new MapManager();
        NAPaths.updateBasePaths(platform.getMinecraftDirectory());
        Settings.load();
        LogUtil.info("Initialization complete");
    }


    public void onWorldJoin() {
        worldInfo = platform.worldProvider.getWorldInfo();
        LogUtil.info("Joined world: {}", worldInfo.worldName);
        NAPaths.updateWorldPath(worldInfo);
        Waypoints.load();
        MapSaveWorker.start();
        MapUpdateWorker.start();
    }

    public void onWorldLeft() {
        MapUpdateWorker.stop();
        MapSaveWorker.stop();
        regionManager.cleanup();

        LogUtil.info("Left world: {}", worldInfo.worldName);
        worldInfo = null;
    }

    public void onWorldUpdate() {

        if (worldInfo == null) {
            LogUtil.error("WHY ARE WE UPDATING WITHOUT A WORLD");
            return;
        }


        if (platform.worldProvider.getWorldInfo().worldDimension != worldInfo.worldDimension) {
            NAWorldInfo latestWorldInfo = platform.worldProvider.getWorldInfo();
            LogUtil.info("changed from DIM {} to DIM {}", worldInfo.worldDimension, latestWorldInfo.worldDimension);
            worldInfo = latestWorldInfo;
            NAPaths.updateWorldPath(worldInfo);
            regionManager.cleanup();
        }


        NAEntity player = NatosAtlas.get().platform.worldProvider.getPlayer();
        double px = player.x;
        double pz = player.z;

        int chunkX = (int) Math.floor(px / 16.0);
        int chunkZ = (int) Math.floor(pz / 16.0);

        regionManager.update(chunkX, chunkZ);
    }
}
