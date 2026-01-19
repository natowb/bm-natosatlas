package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAChunk;

public class MapRenderer {

    public void renderChunk(NACoord chunkCoord, NAChunk chunk) {

        NACoord regionCoord = new NACoord(chunkCoord.x >> 5, chunkCoord.z >> 5);

        for (MapLayer layer : NatosAtlas.get().layers.getLayers()) {
            renderChunkByLayer(regionCoord, chunkCoord, layer, chunk);
        }

        NatosAtlas.get().cache.markDirty(regionCoord);
    }

    private void renderChunkByLayer(NACoord regionCoord, NACoord chunkCoord, MapLayer layer, NAChunk chunk) {
        if (chunk == null) return;

        MapCache cache = NatosAtlas.get().cache;
        MapRegion region = cache.getRegion(layer.id, regionCoord);

        if (region == null) {
            region = new MapRegion();
            cache.put(layer.id, regionCoord, region);

            MapRegion diskLoaded = cache.getRegion(layer.id, regionCoord);
            if (diskLoaded != null) region = diskLoaded;
        }

        layer.renderer.applyChunkToRegion(region, chunkCoord, chunk, layer.usesBlockLight);
    }
}

