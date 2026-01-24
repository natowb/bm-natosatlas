package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.map.MapUpdater;
import dev.natowb.natosatlas.core.tasks.MapSaveScheduler;
import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;

import java.io.File;
import java.util.List;


public class NatosAtlas {


    private static NatosAtlas instance = null;

    public static NatosAtlas get() {
        return instance;
    }

    public final Platform platform;


    private MapUpdater mapUpdater;
    public final MapStorage storage = new MapStorage();
    public final MapCache cache = new MapCache(storage);
    public final MapTextureProvider textures;
    public final MapLayerManager layers;


    private WorldWrapper currentWorld;

    public WorldWrapper getCurrentWorld() {
        return currentWorld;
    }

    public NatosAtlas(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }

        LogUtil.info("Initializing NatosAtlas core");
        instance = this;

        this.platform = platform;
        this.layers = new MapLayerManager();
        this.textures = new MapTextureProvider();
        NAPaths.updateBasePaths(platform.getMinecraftDirectory());
        Settings.load();

        LogUtil.info("Initialization complete");
    }

    public void onWorldJoin(WorldWrapper world) {

        if (world == null) {
            LogUtil.error("joined invalid world");
            currentWorld = null;
            return;
        }

        currentWorld = world;
        mapUpdater = new MapUpdater(currentWorld, layers, cache);
        LogUtil.info("Joined world name={}, saveName={}", world.getName(), world.getSaveName());
        NAPaths.updateWorldPath(world.getSaveName());
        Waypoints.load();
        MapSaveScheduler.start();
    }

    public void onWorldLeft() {

        if (currentWorld == null) return;

        MapSaveScheduler.stop();
        cache.clear();
        mapUpdater = null;
        LogUtil.info("Left world: {}", currentWorld.getSaveName());
        currentWorld = null;
    }

    public void onWorldUpdate() {
        if (currentWorld == null) return;

        mapUpdater.tick();
        currentWorld.update();
        MapSaveScheduler.tick();
    }

    public void generateExistingChunks() {
        List<NARegionFile> regions = currentWorld.getRegionFiles();

        if (regions.isEmpty()) {
            LogUtil.info("No region metadata found.");
            return;
        }

        MapSaveScheduler.stop();

        LogUtil.info("Generating map data for all existing regions (this may take a while...)");

        int index = 0;
        int total = regions.size();

        for (NARegionFile naRegion : regions) {
            index++;

            NACoord regionCoord = naRegion.regionCoord;
            boolean success = false;

            try {
                MapRegion[] layers = new MapRegion[NatosAtlas.get().layers.getLayers().size()];
                for (int i = 0; i < layers.length; i++) {
                    layers[i] = new MapRegion();
                }

                for (NACoord chunkCoord : naRegion.iterateExistingChunks()) {
                    int layerIndex = 0;
                    for (MapLayer layer : NatosAtlas.get().layers.getLayers()) {
                        layer.renderer.applyChunkToRegion(layers[layerIndex], chunkCoord, layer.usesBlockLight);
                        layerIndex++;
                    }
                }

                for (int layerId = 0; layerId < layers.length; layerId++) {
                    File out = storage.getRegionPngFile(layerId, regionCoord);
                    storage.saveRegionBlocking(regionCoord, layers[layerId], out);
                }

                success = true;

            } catch (Exception ignored) {
            }

            if (success) {
                LogUtil.info("[{}/{}] Successfully generated region r({}, {})",
                        index, total, regionCoord.x, regionCoord.z);
            } else {
                LogUtil.info("[{}/{}] Failed to generate region r({}, {})",
                        index, total, regionCoord.x, regionCoord.z);
            }
        }

        LogUtil.info("Full region generation complete.");
        cache.clear();
        MapSaveScheduler.start();
    }
}

