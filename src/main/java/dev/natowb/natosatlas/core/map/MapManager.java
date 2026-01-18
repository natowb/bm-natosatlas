package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.utils.Profiler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MapManager {

    private static final int RADIUS = 8;
    private static final int TPS = 20;

    private static final int SAVE_INTERVAL = TPS;
    private static final int UPDATE_INTERVAL = TPS;

    private final List<MapLayer> layers = new ArrayList<>();
    private int activeLayer = 0;

    private final Set<Long> activeRegions = new HashSet<>();
    private final Set<Long> visibleRegions = new HashSet<>();

    private int activeChunkX, activeChunkZ;
    private int saveTimer = 0;
    private int updateTimer = 0;

    public MapManager() {

        layers.add(new MapLayer(
                0, "Surface Day",
                new MapRegionCache(new MapStorage(0)),
                new MapChunkRendererSurface(),
                false,
                0, 256
        ));

        layers.add(new MapLayer(
                1, "Surface Night",
                new MapRegionCache(new MapStorage(1)),
                new MapChunkRendererSurface(),
                true,
                0, 256
        ));

    }


    public void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            activeLayer = index;
        }
    }

    public int getTotalCacheSize() {
        AtomicInteger size = new AtomicInteger();
        layers.forEach(l -> size.addAndGet(l.cache().getCacheSize()));
        return size.get();
    }

    public int getTotalDirtyQueueSize() {
        AtomicInteger size = new AtomicInteger();
        layers.forEach(l -> size.addAndGet(l.cache().getDirtyQueueSize()));
        return size.get();
    }

    public int getTotalPngCacheSize() {
        AtomicInteger size = new AtomicInteger();
        layers.forEach(l -> size.addAndGet(l.cache().getPngCacheSize()));
        return size.get();
    }


    public void cleanup() {
        layers.forEach(l -> l.cache().clear());
    }

    public int getTexture(NACoord coord) {
        MapLayer layer = layers.get(activeLayer);
        MapRegion region = layer.cache().getRegion(coord);
        if (region == null) return -1;

        region.updateTexture();
        return region.getTextureId();
    }

    public int getActiveChunkX() {
        return activeChunkX;
    }

    public int getActiveChunkZ() {
        return activeChunkZ;
    }

    public int getLoadedRegionCount() {
        return activeRegions.size();
    }


    public void update(int playerChunkX, int playerChunkZ) {
        this.activeChunkX = playerChunkX;
        this.activeChunkZ = playerChunkZ;
        updateSelectedLayer();
        Profiler p = Profiler.start("MapManager.update");

        if (++updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            updateActiveRegions(playerChunkX, playerChunkZ);
            p.mark("updateActiveRegions");
            updateNearbyChunks(playerChunkX, playerChunkZ);
            p.mark("updateNearbyChunks");
        }

        if (++saveTimer >= SAVE_INTERVAL) {
            saveTimer = 0;
            layers.forEach(layer -> layer.cache().saveOneRegion());
        }
        p.mark("saveRegions");
        p.end();
    }

    private void updateSelectedLayer() {
        if (Settings.mapRenderMode == Settings.MapRenderMode.Day) {
            setActiveLayer(0);
            return;
        }

        if (Settings.mapRenderMode == Settings.MapRenderMode.Night) {
            setActiveLayer(1);
            return;
        }

        long time = NatosAtlas.get().platform.worldProvider.getWorldInfo().worldTime % 24000L;
        setActiveLayer(time < 12000L ? 0 : 1);
    }


    public void updateCanvasVisibleRegions(Set<Long> visible) {
        visibleRegions.clear();
        visibleRegions.addAll(visible);
        syncLifetime();
    }

    private void syncLifetime() {
        Set<Long> keep = new HashSet<>(activeRegions);
        keep.addAll(visibleRegions);
        layers.forEach(l -> l.cache().syncLoadedRegions(keep));
    }

    private void updateActiveRegions(int playerChunkX, int playerChunkZ) {
        activeRegions.clear();

        int regionX = playerChunkX >> 5;
        int regionZ = playerChunkZ >> 5;

        for (int rx = regionX - 1; rx <= regionX + 1; rx++) {
            for (int rz = regionZ - 1; rz <= regionZ + 1; rz++) {
                activeRegions.add(new NACoord(rx, rz).toKey());
            }
        }

        syncLifetime();
    }

    private void updateNearbyChunks(int playerChunkX, int playerChunkZ) {
        for (int deltaChunkX = -RADIUS; deltaChunkX <= RADIUS; deltaChunkX++) {
            for (int deltaChunkZ = -RADIUS; deltaChunkZ <= RADIUS; deltaChunkZ++) {
                if (deltaChunkX * deltaChunkX + deltaChunkZ * deltaChunkZ > RADIUS * RADIUS) continue;
                int worldChunkX = playerChunkX + deltaChunkX;
                int worldChunkZ = playerChunkZ + deltaChunkZ;
                NAChunk surface = NatosAtlas.get().platform.worldProvider.buildSurface(NACoord.from(worldChunkX, worldChunkZ));
                updateChunk(worldChunkX, worldChunkZ, surface);
            }
        }
    }

    public void updateChunk(int worldChunkX, int worldChunkZ, NAChunk chunk) {
        for (MapLayer layer : layers) {
            buildChunkForLayer(worldChunkX, worldChunkZ, layer, chunk);
        }
    }

    private void buildChunkForLayer(int worldChunkX, int worldChunkZ, MapLayer layer, NAChunk chunk) {
        if (chunk == null) return;
        int regionChunkX = worldChunkX >> 5;
        int regionChunkZ = worldChunkZ >> 5;
        NACoord regionCoord = new NACoord(regionChunkX, regionChunkZ);
        MapRegionCache cache = layer.cache();
        MapRegion region = cache.getRegion(regionCoord);
        if (region == null) {
            region = new MapRegion();
            cache.put(regionCoord, region);
            MapRegion diskLoaded = cache.getRegion(regionCoord);
            if (diskLoaded != null) region = diskLoaded;
        }
        layer.renderer().applyChunkToRegion(region, worldChunkX, worldChunkZ, chunk, layer.usesBlockLight());
        cache.markDirty(regionCoord);
    }

    public void exportLayers() {
        for (MapLayer layer : layers) {
            layer.cache().getStorage().exportFullMap(NatosAtlas.get().getWorldDataPath().resolve("layer_" + layer.id() + ".png"));
        }
    }


}
