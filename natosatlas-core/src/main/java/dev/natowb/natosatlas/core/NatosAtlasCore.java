package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.layers.MapLayerManager;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.map.MapUpdater;
import dev.natowb.natosatlas.core.io.SaveScheduler;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;
import dev.natowb.natosatlas.core.texture.TextureProvider;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.access.WorldAccess;

public class NatosAtlasCore {


    private static NatosAtlasCore instance = null;

    public static NatosAtlasCore get() {
        return instance;
    }

    public final NatosAtlasPlatform platform;


    private MapUpdater mapUpdater;
    public final MapStorage storage = new MapStorage();
    public final MapCache cache = new MapCache(storage);
    public final TextureProvider textures = new TextureProvider();
    public final MapLayerManager layers = new MapLayerManager();


    private String worldSaveName;
    private int dim;
    private boolean running;

    public boolean isStopped() {
        return !running;
    }

    public NatosAtlasCore(NatosAtlasPlatform platform) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }

        instance = this;
        this.platform = platform;
        LogUtil.setLoggingLevel(LogUtil.LogLevel.INFO);
        NAPaths.updateBasePaths(platform.getMinecraftDirectory());
        Settings.load();
    }


    public void onTick() {
        if (!WorldAccess.get().exists() && running) {
            running = false;
            onLeave();
        }

        if (WorldAccess.get().exists() && !running) {
            onJoin();
        }

        if (!running) return;

        int currentDim = WorldAccess.get().getDimensionId();
        if (dim != currentDim) {
            dim = currentDim;
            cache.clear();
            return;
        }

        mapUpdater.tick();
        SaveScheduler.tick();
        layers.tick();
    }

    private void onJoin() {
        worldSaveName = WorldAccess.get().getSaveName();

        if (worldSaveName == null) {
            LogUtil.error("joined invalid world");
            return;
        }
        LogUtil.info("Joined world saveName={}", worldSaveName);

        running = true;
        mapUpdater = new MapUpdater(layers, cache);
        NAPaths.updateWorldPath(worldSaveName);
        Waypoints.load();
        SaveScheduler.start();
    }

    private void onLeave() {
        LogUtil.info("Left world: {}", worldSaveName);
        SaveScheduler.stop();
        cache.clear();
        worldSaveName = null;
        mapUpdater = null;
        running = false;
    }
}