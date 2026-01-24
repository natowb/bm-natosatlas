package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.MapChunkRendererCave;
import dev.natowb.natosatlas.core.MapChunkRendererSurface;

import java.util.ArrayList;
import java.util.List;

public class MapLayerManager {

    private final List<MapLayer> layers = new ArrayList<>();
    private int activeLayer = 0;

    public MapLayerManager() {
        MapChunkRendererSurface surface = new MapChunkRendererSurface();
        layers.add(new MapLayer(0, "Day", surface, false));
        layers.add(new MapLayer(1, "Night", surface, true));
        layers.add(new MapLayer(2, "Cave", new MapChunkRendererCave(), true));
    }

    public void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            activeLayer = index;
        }
    }

    public MapLayer getActiveLayer() {
        return layers.get(activeLayer);
    }
    public List<MapLayer> getLayers() {
        return layers;
    }
}
