package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.platform.PlatformEntityProvider;
import dev.natowb.natosatlas.core.map.MapEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class NacEntityProviderST implements PlatformEntityProvider {
    @Override
    public List<MapEntity> collectEntities() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        List<MapEntity> entities = new ArrayList<>();

        for (Object o : mc.world.entities) {
            if (!(o instanceof LivingEntity e)) continue;

            if (e instanceof PlayerEntity) continue;

            int icon = getIconIndexForEntity(e);

            entities.add(new MapEntity(e.x, e.y, e.z, e.yaw, icon));
        }

        return entities;
    }

    @Override
    public List<MapEntity> collectPlayers() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        List<MapEntity> players = new ArrayList<>();

        for (Object o : mc.world.players) {
            if (!(o instanceof PlayerEntity p)) continue;

            int icon = (p == mc.player) ? 0 : 3;

            players.add(new MapEntity(p.x, p.y, p.z, p.yaw, icon));
        }

        return players;
    }

    @Override
    public MapEntity getLocalPlayer() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return new MapEntity(mc.player.x, mc.player.y, mc.player.z, mc.player.yaw, 0);
    }


    private int getIconIndexForEntity(LivingEntity e) {
        if (e instanceof AnimalEntity) return 1;
        if (e instanceof MobEntity) return 2;
        return 1;
    }
}
