package dev.natowb.natosatlas.core.glue;

import dev.natowb.natosatlas.core.models.NacEntity;

import java.util.List;

public interface INacEntityProvider {
    List<NacEntity> collectEntities();
    List<NacEntity> collectPlayers();
    NacEntity getLocalPlayer();
}



