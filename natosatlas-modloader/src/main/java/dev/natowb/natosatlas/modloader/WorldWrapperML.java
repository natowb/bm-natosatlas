package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.src.ModLoader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class WorldWrapperML implements WorldWrapper {

    private static final Minecraft mc = ModLoader.getMinecraftInstance();

    private final World world;
    private final String worldSaveName;

    public WorldWrapperML(World world, String worldSaveName) {
        this.world = world;
        this.worldSaveName = worldSaveName;
    }

    @Override
    public String getName() {
        return world.getProperties().getName();
    }

    @Override
    public String getSaveName() {
        return worldSaveName;
    }

    @Override
    public long getTime() {
        return world.getTime();
    }

    @Override
    public long getSeed() {
        return world.getSeed();
    }

    @Override
    public int getDimensionId() {
        return world.dimension.id;
    }

    @Override
    public boolean isServer() {
        return world.isRemote;
    }

    @Override
    public NABiome getBiome(NACoord blockCoord) {
        Biome biome = world.method_1781().getBiome(blockCoord.x, blockCoord.z);
        return new NABiome(biome.grassColor, biome.foliageColor);
    }

    @Override
    public List<NAEntity> getEntities() {
        List<NAEntity> entities = new ArrayList<>();

        for (Object o : world.entities) {
            if (!(o instanceof LivingEntity)) continue;
            if (o instanceof PlayerEntity) continue;

            LivingEntity e = (LivingEntity) o;

            NAEntity.NAEntityType type = NAEntity.NAEntityType.Mob;

            if (e instanceof AnimalEntity) {
                type = NAEntity.NAEntityType.Animal;
            }

            entities.add(new NAEntity(e.x, e.y, e.z, e.yaw, type).setTexturePath(e.getTexture()));
        }

        return entities;
    }

    @Override
    public List<NAEntity> getPlayers() {
        List<NAEntity> players = new ArrayList<>();

        for (Object o : world.players) {
            if (!(o instanceof PlayerEntity)) continue;
            PlayerEntity p = (PlayerEntity) o;
            players.add(new NAEntity(p.x, p.y, p.z, p.yaw, NAEntity.NAEntityType.Player));
        }

        return players;
    }

    @Override
    public NAEntity getPlayer() {
        PlayerEntity p = mc.player;
        return new NAEntity(p.x, p.y, p.z, p.yaw, NAEntity.NAEntityType.Player);
    }
}
