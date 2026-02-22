package dev.natowb.natosatlas.client.ui.screens.map;

import java.util.Set;

public interface MapStage {
    void draw(MapContext ctx, Set<Long> visibleRegions, int activeLayer);
}