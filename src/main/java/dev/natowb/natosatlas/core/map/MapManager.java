package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;

import java.util.*;

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

    public void cleanup() {
        layers.forEach(l -> l.cache().clear());
    }

    public int getTexture(MapRegionCoord coord) {
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

        autoSelectLayer();

        if (++updateTimer >= UPDATE_INTERVAL) {
            updateTimer = 0;
            updateActiveRegions(playerChunkX, playerChunkZ);
            updateNearbyChunks(playerChunkX, playerChunkZ);
        }

        if (++saveTimer >= SAVE_INTERVAL) {
            saveTimer = 0;
            layers.forEach(layer -> layer.cache().saveOneRegion());
        }
    }

    private void autoSelectLayer() {
        boolean day = NatosAtlas.get().platform.worldProvider.isDaytime();
        setActiveLayer(day ? 0 : 1);
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
                activeRegions.add(new MapRegionCoord(rx, rz).toKey());
            }
        }

        syncLifetime();
    }

    private void updateNearbyChunks(int playerChunkX, int playerChunkZ) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {

                if (dx * dx + dz * dz > RADIUS * RADIUS) continue;

                int chunkX = playerChunkX + dx;
                int chunkZ = playerChunkZ + dz;

                MapChunk surface = NatosAtlas.get().platform.chunkProvider.buildSurface(chunkX, chunkZ);

                for (MapLayer layer : layers) {
                    buildChunkForLayer(chunkX, chunkZ, layer, surface);
                }
            }
        }
    }


    private void buildChunkForLayer(int chunkX, int chunkZ, MapLayer layer, MapChunk chunk) {
        if (chunk == null) return;

        int rx = chunkX >> 5;
        int rz = chunkZ >> 5;
        MapRegionCoord coord = new MapRegionCoord(rx, rz);

        MapRegionCache cache = layer.cache();
        MapRegion region = cache.getRegion(coord);

        if (region == null) {
            region = new MapRegion();
            cache.put(coord, region);

            MapRegion diskLoaded = cache.getRegion(coord);
            if (diskLoaded != null) region = diskLoaded;
        }

        layer.renderer().applyChunkToRegion(region, chunkX, chunkZ, chunk, layer.usesBlockLight());
        cache.markDirty(coord);
    }
}
