package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import dev.natowb.natosatlas.client.cache.NARegionTextureCache;
import dev.natowb.natosatlas.client.map.MapLayerController;
import dev.natowb.natosatlas.client.saving.SaveScheduler;
import dev.natowb.natosatlas.client.settings.Settings;
import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NASession;
import dev.natowb.natosatlas.core.data.NALayer;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.client.map.MapUpdater;
import dev.natowb.natosatlas.client.waypoint.Waypoints;

import java.nio.file.Path;

public class NAClient implements NASession {

    private static NAClient instance;

    public static NAClient get() {
        return instance;
    }


    private boolean inWorld;
    private String worldSaveName;
    private int dim;
    private final NAClientPlatform platform;
    private final MapLayerController layerController = new MapLayerController();


    public NAClient(Path minecraftPath, NAClientPlatform platform) {
        if (instance != null) {
            LogUtil.error("tried to create NAClient when one already exists");
            throw new RuntimeException();
        }

        NAClient.instance = this;
        this.platform = platform;

        NAClientPaths.updateBasePaths(minecraftPath);
        LayerRegistry.getLayers().add(new NALayer(2, "Cave", new NAChunkBuilderCave(), true));
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
        boolean worldExists = ClientWorldAccess.get().getWorldInfo() != null;

        if (!worldExists && inWorld) {
            inWorld = false;
            onWorldLeft();
            return;
        }

        if (worldExists && !inWorld) {
            worldSaveName = ClientWorldAccess.get().getSaveName();
            if (worldSaveName == null) {
                return;
            }

            inWorld = true;
            dim = ClientWorldAccess.get().getWorldInfo().getDimensionId();
            onWorldJoined(worldSaveName, dim);
        }

        if (!inWorld) return;

        int currentDim = ClientWorldAccess.get().getWorldInfo().getDimensionId();
        if (dim != currentDim) {
            dim = currentDim;
            onDimensionChange(dim);
            return;
        }
        onWorldTick();
    }

    private void onWorldJoined(String worldSaveName, int dim) {
        NAClientPaths.updateWorldPath(worldSaveName);
        Waypoints.load();
        SaveScheduler.start();

        LogUtil.info("Client joined world={} dim={}", worldSaveName, dim);
    }

    private void onWorldLeft() {
        SaveScheduler.stop();
        NARegionTextureCache.clear();
        LogUtil.info("Client left world {}", worldSaveName);
    }

    private void onDimensionChange(int newDim) {
        LogUtil.info("Client dimension changed to {}", newDim);
        NARegionTextureCache.clear();
    }

    private void onWorldTick() {
        MapUpdater.get().tick();
        SaveScheduler.tick();
        layerController.tick();
    }
}
