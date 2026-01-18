package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.platform.PlatformEntityProvider;
import dev.natowb.natosatlas.core.data.NAEntity;
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
    public List<NAEntity> collectEntities() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        List<NAEntity> entities = new ArrayList<>();

        for (Object o : mc.world.entities) {
            if (!(o instanceof LivingEntity e)) continue;

            if (e instanceof PlayerEntity) continue;


            NAEntity.NAEntityType type = NAEntity.NAEntityType.Mob;

            if (e instanceof AnimalEntity) {
                type = NAEntity.NAEntityType.Animal;
            }

            entities.add(new NAEntity(e.x, e.y, e.z, e.yaw, type));
        }

        return entities;
    }

    @Override
    public List<NAEntity> collectPlayers() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        List<NAEntity> players = new ArrayList<>();

        for (Object o : mc.world.players) {
            if (!(o instanceof PlayerEntity p)) continue;
            players.add(new NAEntity(p.x, p.y, p.z, p.yaw, NAEntity.NAEntityType.Player));
        }

        return players;
    }

    @Override
    public NAEntity getLocalPlayer() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return new NAEntity(mc.player.x, mc.player.y, mc.player.z, mc.player.yaw, NAEntity.NAEntityType.Player);
    }
}
