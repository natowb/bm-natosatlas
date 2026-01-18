package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.map.MapEntity;

import java.util.List;

public interface PlatformEntityProvider {
    List<MapEntity> collectEntities();
    List<MapEntity> collectPlayers();
    MapEntity getLocalPlayer();
}



