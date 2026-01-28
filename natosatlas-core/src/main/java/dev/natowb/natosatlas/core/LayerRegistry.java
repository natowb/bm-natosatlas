package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.chunk.NAChunkBuilderSurface;
import dev.natowb.natosatlas.core.data.NALayer;

import java.util.ArrayList;
import java.util.List;

public class LayerRegistry {

    private static final List<NALayer> LAYERS = new ArrayList<>();

    static {
        LAYERS.add(new NALayer(0, "Day", new NAChunkBuilderSurface(), false));
        LAYERS.add(new NALayer(1, "Night", new NAChunkBuilderSurface(), true));
    }

    public static List<NALayer> getLayers() {
        return LAYERS;
    }

    public static NALayer get(int id) {
        return LAYERS.get(id);
    }
}
