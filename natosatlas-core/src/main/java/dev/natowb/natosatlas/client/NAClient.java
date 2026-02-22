package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import dev.natowb.natosatlas.client.cache.NARegionTextureCache;
import dev.natowb.natosatlas.client.saving.MapSaver;
import dev.natowb.natosatlas.client.settings.Settings;
import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NAClientSession;
import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.data.NALayer;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.client.map.MapUpdater;
import dev.natowb.natosatlas.client.waypoint.Waypoints;

public class NAClient implements NAClientSession {

    private static NAClient instance;

    private final MapUpdater mapUpdater = MapUpdater.get();
    private final MapSaver mapSaver = MapSaver.get();

    public static NAClient get() {
        return instance;
    }

    private boolean inWorld;
    private String worldSaveName;
    private int dim;
    private final ClientPlatform platform;

    public NAClient(ClientPlatform platform) {
        if (instance != null) {
            LogUtil.error("tried to create NAClient when one already exists");
            throw new RuntimeException();
        }

        NAClient.instance = this;
        this.platform = platform;

        LayerRegistry.getLayers().add(new NALayer(2, "Cave", new NAChunkBuilderCave(), true, false));
        Settings.load();

        LogUtil.setLoggingLevel(LogUtil.LogLevel.DEBUG);
    }


    public ClientPlatform getPlatform() {
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
        LogUtil.info("[Client] Joined world (world={} dim={})", worldSaveName, dim);

        NAPaths.setWorldPaths(worldSaveName, false);

        Waypoints.load();
        mapSaver.start();
    }

    private void onWorldLeft() {
        LogUtil.info("[Client] Left world (world={})", worldSaveName);

        mapSaver.stop();
        NARegionTextureCache.clear();
    }

    private void onDimensionChange(int newDim) {
        LogUtil.info("[Client] Changed dimension (dim={newDim})", newDim);

        NARegionTextureCache.clear();
    }

    private void onWorldTick() {
        mapUpdater.tick();
        mapSaver.saveNextBatch();
    }
}
