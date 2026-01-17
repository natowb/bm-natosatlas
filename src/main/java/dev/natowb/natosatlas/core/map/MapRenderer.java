package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.utils.NAPaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MapRenderer {

    private final List<MapLayer> layers = new ArrayList<>();
    private int activeLayer = 0;

    public MapRenderer() {
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

    public MapLayer getLayer() {
        return layers.get(activeLayer);
    }

    public int getTexture(NACoord coord) {
        MapRegion region = getLayer().cache().getRegion(coord);
        if (region == null) return -1;

        region.updateTexture();
        return region.getTextureId();
    }

    public void updateChunk(int worldChunkX, int worldChunkZ, NAChunk chunk) {
        for (MapLayer layer : layers) {
            buildChunkForLayer(worldChunkX, worldChunkZ, layer, chunk);
        }
    }

    public void processChunkSync(int worldChunkX, int worldChunkZ, NAChunk chunk) {
        updateChunk(worldChunkX, worldChunkZ, chunk);
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


    public void saveOneRegion() {
        layers.forEach(layer -> layer.cache().saveOneRegion());
    }

    public void exportLayers() {
        for (MapLayer layer : layers) {
            layer.cache().getStorage().exportFullMap(
                    NAPaths.geWorldDataPath().resolve("layer_" + layer.id() + ".png")
            );
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

    public void syncLoadedRegions(Set<Long> keep) {
        layers.forEach(l -> l.cache().syncLoadedRegions(keep));
    }
}
