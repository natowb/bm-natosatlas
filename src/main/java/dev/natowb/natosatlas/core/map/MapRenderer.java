package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAChunk;

import java.util.ArrayList;
import java.util.List;

public class MapRenderer {

    private final List<MapLayer> layers = new ArrayList<>();
    private int activeLayer = 0;

    public MapRenderer() {
        MapChunkRendererSurface surfaceRenderer = new MapChunkRendererSurface();
        layers.add(new MapLayer(0, "Surface Day", surfaceRenderer, false));
        layers.add(new MapLayer(1, "Surface Night", surfaceRenderer, true));
    }

    public void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            activeLayer = index;
        }
    }

    public MapLayer getLayer() {
        return layers.get(activeLayer);
    }

    public List<MapLayer> getLayers() {
        return this.layers;
    }

    protected int getTexture(NACoord coord) {
        MapCache cache = NatosAtlas.get().cache;
        MapRegion region = cache.getRegion(activeLayer, coord);
        if (region == null) return -1;

        region.updateTexture();
        return region.getTextureId();
    }

    public void updateChunk(int worldChunkX, int worldChunkZ, NAChunk chunk) {
        for (MapLayer layer : layers) {
            buildChunkForLayer(worldChunkX, worldChunkZ, layer, chunk);
        }

        int regionChunkX = worldChunkX >> 5;
        int regionChunkZ = worldChunkZ >> 5;
        NACoord regionCoord = new NACoord(regionChunkX, regionChunkZ);
        NatosAtlas.get().cache.markDirty(regionCoord);
    }

    public void processChunkSync(int worldChunkX, int worldChunkZ, NAChunk chunk) {
        updateChunk(worldChunkX, worldChunkZ, chunk);
    }

    private void buildChunkForLayer(int worldChunkX, int worldChunkZ, MapLayer layer, NAChunk chunk) {
        if (chunk == null) return;

        int regionChunkX = worldChunkX >> 5;
        int regionChunkZ = worldChunkZ >> 5;
        NACoord regionCoord = new NACoord(regionChunkX, regionChunkZ);

        MapCache cache = NatosAtlas.get().cache;
        MapRegion region = cache.getRegion(layer.id, regionCoord);

        if (region == null) {
            region = new MapRegion();
            cache.put(layer.id, regionCoord, region);

            MapRegion diskLoaded = cache.getRegion(layer.id, regionCoord);
            if (diskLoaded != null) region = diskLoaded;
        }

        layer.renderer.applyChunkToRegion(region, worldChunkX, worldChunkZ, chunk, layer.usesBlockLight);
    }
}
