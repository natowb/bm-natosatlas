package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.map.MapManager;
import dev.natowb.natosatlas.core.map.RegionSaveWorker;
import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.waypoint.Waypoints;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NatosAtlas {

    private static NatosAtlas instance = null;

    public static NatosAtlas get() {
        return instance;
    }

    public final Platform platform;
    public final MapManager regionManager;

    private Path dataPath;
    private Path worldDataPath;
    private Path worldRegionDataPath;

    public NatosAtlas(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }
        instance = this;

        this.platform = platform;

        LogUtil.info("NatosAtlas", "Initializing NatosAtlas core");
        setDataPath();
        Settings.load();

        regionManager = new MapManager();

        LogUtil.info("NatosAtlas", "Initialization complete");
    }


    public void onWorldJoin() {
        LogUtil.info("NatosAtlas", "World join detected, loading waypoints");
        updateWorldDataPath();
        updateWorldRegionDataPath();
        Waypoints.load();
        RegionSaveWorker.start();
    }

    public void onWorldLeft() {
        LogUtil.info("NatosAtlas", "World left, clearing region cache");
        RegionSaveWorker.stop();
        regionManager.cleanup();

    }

    public void onWorldUpdate() {
        NAEntity player = NatosAtlas.get().platform.entityProvider.getLocalPlayer();

        double px = player.x;
        double pz = player.z;

        int chunkX = (int) Math.floor(px / 16.0);
        int chunkZ = (int) Math.floor(pz / 16.0);

        regionManager.update(chunkX, chunkZ);
    }

    public Path getDataPath() {
        return dataPath;
    }

    public Path getWorldRegionDataPath() {
        Path path = worldRegionDataPath.resolve("DIM" + platform.worldProvider.getWorldInfo().worldDimension);
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            LogUtil.error("Platform", e, "Failed to create dimensional region directory {}", path);
            throw new RuntimeException("Failed to create dimensional region directory: " + path, e);
        }
        return path;
    }

    public Path getWorldDataPath() {
        return worldDataPath;
    }

    private void setDataPath() {
        dataPath = platform.getMinecraftDirectory().resolve("natosatlas");

        try {
            Files.createDirectories(dataPath);
            LogUtil.debug("Platform", "Ensured data directory {}", dataPath);
        } catch (IOException e) {
            LogUtil.error("Platform", e, "Failed to create data directory {}", dataPath);
            throw new RuntimeException("Failed to create data directory: " + dataPath, e);
        }
    }


    private void updateWorldRegionDataPath() {
        worldRegionDataPath = getWorldDataPath().resolve("regions");
        try {
            Files.createDirectories(worldRegionDataPath);
            LogUtil.debug("Platform", "Ensured region directory {}", worldRegionDataPath);
        } catch (IOException e) {
            LogUtil.error("Platform", e, "Failed to create regions directory {}", worldRegionDataPath);
            throw new RuntimeException("Failed to create regions directory: " + worldRegionDataPath, e);
        }
    }

    private void updateWorldDataPath() {
        worldDataPath = getDataPath().resolve("worlds/" + platform.worldProvider.getWorldInfo().worldName);
        try {
            Files.createDirectories(worldDataPath);
            LogUtil.debug("Platform", "Ensured world directory {}", worldDataPath);
        } catch (IOException e) {
            LogUtil.error("Platform", e, "Failed to create world directory {}", worldDataPath);
            throw new RuntimeException("Failed to create world directory: " + worldDataPath, e);
        }
    }
}
