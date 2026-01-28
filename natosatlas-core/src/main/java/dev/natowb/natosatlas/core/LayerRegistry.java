package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.chunk.ChunkCaveRenderer;
import dev.natowb.natosatlas.core.chunk.ChunkSurfaceRenderer;
import dev.natowb.natosatlas.core.data.NALayer;

import java.util.ArrayList;
import java.util.List;

public class LayerRegistry {

    private static final List<NALayer> LAYERS = new ArrayList<>();

    static {
        ChunkSurfaceRenderer surface = new ChunkSurfaceRenderer();
        LAYERS.add(new NALayer(0, "Day", surface, false));
        LAYERS.add(new NALayer(1, "Night", surface, true));
        LAYERS.add(new NALayer(2, "Cave", new ChunkCaveRenderer(), true));
    }

    public static List<NALayer> getLayers() {
        return LAYERS;
    }

    public static NALayer get(int id) {
        return LAYERS.get(id);
    }
}
