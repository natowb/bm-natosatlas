package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAChunk;

public class MapRenderer {
    public void updateChunk(int worldChunkX, int worldChunkZ, NAChunk chunk) {
        for (MapLayer layer : NatosAtlas.get().layers.getLayers()) {
            buildChunkForLayer(worldChunkX, worldChunkZ, layer, chunk);
        }

        int regionChunkX = worldChunkX >> 5;
        int regionChunkZ = worldChunkZ >> 5;
        NatosAtlas.get().cache.markDirty(new NACoord(regionChunkX, regionChunkZ));
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

