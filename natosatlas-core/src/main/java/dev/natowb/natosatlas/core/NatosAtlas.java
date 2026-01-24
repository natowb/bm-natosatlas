package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.tasks.MapSaveScheduler;
import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.tasks.MapUpdateScheduler;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NatosAtlas {

    private static final int RADIUS = 8;
    private static final int TPS = 20;
    private static final int UPDATE_INTERVAL = TPS;

    private static NatosAtlas instance = null;

    public static NatosAtlas get() {
        return instance;
    }

    private final Set<Long> activeRegions = new HashSet<>();
    private final Set<Long> visibleRegions = new HashSet<>();

    public final Platform platform;
    public final MapTextureProvider textures;
    public final MapCache cache;
    public final MapLayerManager layers;

    private int updateTimer = 0;

    private int activeChunkX;
    private int activeChunkZ;


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
        this.cache = new MapCache(new MapStorage());
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
        LogUtil.info("Joined world name={}, saveName={}", world.getName(), world.getSaveName());
        NAPaths.updateWorldPath(world.getSaveName());
        Waypoints.load();
        MapSaveScheduler.start();
        MapUpdateScheduler.start();
    }

    public void onWorldLeft() {

        if (currentWorld == null) return;

        MapUpdateScheduler.stop();
        MapSaveScheduler.stop();
        cache.clear();

        LogUtil.info("Left world: {}", currentWorld.getName());
        currentWorld = null;
    }

    public void onWorldUpdate() {
        if (currentWorld == null) return;
        updatePlayerChunk();
        updateActiveLayer();

        if (++updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            updateActiveRegions();
            updateNearbyChunks();
        }

        MapUpdateScheduler.tick();
        MapSaveScheduler.tick();
    }

    private void updatePlayerChunk() {
        NAEntity player = NatosAtlas.get().getCurrentWorld().getPlayer();
        activeChunkX = (int) Math.floor(player.x / 16.0);
        activeChunkZ = (int) Math.floor(player.z / 16.0);
    }

    private void updateActiveLayer() {
        if (Settings.mapRenderMode == Settings.MapRenderMode.Day) {
            layers.setActiveLayer(0);
        } else if (Settings.mapRenderMode == Settings.MapRenderMode.Night) {
            layers.setActiveLayer(1);
        } else {
            long time = currentWorld.getTime() % 24000L;
            layers.setActiveLayer(time < 12000L ? 0 : 1);
        }
    }


    public void updateCanvasVisibleRegions(Set<Long> visible) {
        visibleRegions.clear();
        visibleRegions.addAll(visible);
        syncLifetime();
    }

    private void updateActiveRegions() {
        activeRegions.clear();

        int regionX = activeChunkX >> 5;
        int regionZ = activeChunkZ >> 5;

        for (int rx = regionX - 1; rx <= regionX + 1; rx++) {
            for (int rz = regionZ - 1; rz <= regionZ + 1; rz++) {
                activeRegions.add(new NACoord(rx, rz).toKey());
            }
        }

        syncLifetime();
    }

    private void syncLifetime() {
        Set<Long> keep = new HashSet<>(activeRegions);
        keep.addAll(visibleRegions);
        cache.syncLoadedRegions(keep);
    }

    private void updateNearbyChunks() {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx * dx + dz * dz > RADIUS * RADIUS) continue;

                int wx = activeChunkX + dx;
                int wz = activeChunkZ + dz;

                NAChunk chunk = platform.worldProvider.getChunk(NACoord.from(wx, wz));
                MapUpdateScheduler.enqueue(NACoord.from(wx, wz), chunk);
            }
        }
    }

    public void generateExistingChunks() {
        List<NARegionFile> regions = NatosAtlas.get().platform.worldProvider.getRegionMetadata();

        if (regions.isEmpty()) {
            LogUtil.info("No region metadata found.");
            return;
        }

        MapUpdateScheduler.stop();
        MapSaveScheduler.stop();

        LogUtil.info("Generating map data for all existing regions (this may take a while...)");

        MapCache cache = NatosAtlas.get().cache;
        MapStorage storage = cache.getStorage();

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
                    NAChunk chunk = NatosAtlas.get().platform.worldProvider.getChunkFromDisk(chunkCoord);
                    if (chunk == null) continue;

                    int layerIndex = 0;
                    for (MapLayer layer : NatosAtlas.get().layers.getLayers()) {
                        layer.renderer.applyChunkToRegion(
                                layers[layerIndex],
                                chunkCoord,
                                chunk,
                                layer.usesBlockLight
                        );
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
        MapUpdateScheduler.start();
    }
}

