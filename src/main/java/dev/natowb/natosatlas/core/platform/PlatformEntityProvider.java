package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.data.NAEntity;

import java.util.List;

public interface PlatformEntityProvider {
    List<NAEntity> collectEntities();
    List<NAEntity> collectPlayers();
    NAEntity getLocalPlayer();
}



