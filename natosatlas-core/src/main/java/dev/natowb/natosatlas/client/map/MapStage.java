package dev.natowb.natosatlas.client.map;

import java.util.Set;

public interface MapStage {
    void draw(MapContext ctx, Set<Long> visibleRegions);
}

