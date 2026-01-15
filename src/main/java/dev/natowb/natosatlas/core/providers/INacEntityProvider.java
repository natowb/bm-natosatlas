package dev.natowb.natosatlas.core.providers;

import dev.natowb.natosatlas.core.models.NacEntity;

import java.util.List;

public interface INacEntityProvider {
    List<NacEntity> collectEntities();
    List<NacEntity> collectPlayers();
    NacEntity getLocalPlayer();
}



