package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NAWorldInfo;
import dev.natowb.natosatlas.core.map.*;
import dev.natowb.natosatlas.core.tasks.MapSaveScheduler;
import dev.natowb.natosatlas.core.tasks.MapSaveWorker;
import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.tasks.MapUpdateScheduler;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.waypoint.Waypoints;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAChunk;

import java.util.HashSet;
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
    public final MapRenderer renderer;
    public final MapTextureProvider textures;
    public final MapCache cache;
    public final MapLayerManager layers;

    private NAWorldInfo worldInfo;
    private String worldSaveName;
    private int updateTimer = 0;

    private int activeChunkX;
    private int activeChunkZ;


    private boolean enabled = false;

    public boolean isEnabled() {
        return enabled;
    }

    public NatosAtlas(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }

        LogUtil.info("Initializing NatosAtlas core");
        instance = this;

        this.platform = platform;
        this.renderer = new MapRenderer();
        this.layers = new MapLayerManager();
        this.textures = new MapTextureProvider();
        this.cache = new MapCache(new MapStorage());
        NAPaths.updateBasePaths(platform.getMinecraftDirectory());
        Settings.load();

        LogUtil.info("Initialization complete");
    }

    public void onWorldJoin(String saveName) {

        if (saveName == null) {
            LogUtil.error("Failed to get saveName for world, disabling map");
            enabled = false;
            return;
        }

        enabled = true;
        worldInfo = platform.worldProvider.getWorldInfo();
        worldSaveName = saveName;
        LogUtil.info("Joined world name={}, saveName={}", worldInfo.worldName, worldSaveName);
        NAPaths.updateWorldPath(saveName);
        Waypoints.load();
        MapSaveWorker.start();
    }

    public void onWorldLeft() {

        if (!enabled) return;

        MapSaveWorker.stop();
        cache.clear();

        LogUtil.info("Left world: {}", worldInfo.worldName);
        worldInfo = null;
    }

    public void onWorldUpdate() {
        if (!enabled) return;

        handleDimensionChange();
        updatePlayerChunk();
        updateActiveLayer();

        if (++updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            updateActiveRegions();
            updateNearbyChunks();
        }

        MapUpdateScheduler.run();
        MapSaveScheduler.run();
    }


    private void handleDimensionChange() {
        NAWorldInfo latest = platform.worldProvider.getWorldInfo();
        if (latest.worldDimension != worldInfo.worldDimension) {
            LogUtil.info("changed from DIM {} to DIM {}", worldInfo.worldDimension, latest.worldDimension);
            worldInfo = latest;
        }
    }

    private void updatePlayerChunk() {
        NAEntity player = platform.worldProvider.getPlayer();
        activeChunkX = (int) Math.floor(player.x / 16.0);
        activeChunkZ = (int) Math.floor(player.z / 16.0);
    }

    private void updateActiveLayer() {
        if (Settings.mapRenderMode == Settings.MapRenderMode.Day) {
            layers.setActiveLayer(0);
        } else if (Settings.mapRenderMode == Settings.MapRenderMode.Night) {
            layers.setActiveLayer(1);
        } else {
            long time = worldInfo.worldTime % 24000L;
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
                MapUpdateScheduler.enqueue(renderer, NACoord.from(wx, wz), chunk);
            }
        }
    }
}
