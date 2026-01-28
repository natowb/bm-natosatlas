package dev.natowb.natosatlas.stationapi.client;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.client.NAClientPaths;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionChunkStorage;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.storage.WorldSaveInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldAccessST extends ClientWorldAccess {
    private static final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

    @Override
    public boolean exists() {
        return mc.world != null;
    }

    @Override
    public String getName() {
        return mc.world.getProperties().getName();
    }

    @Override
    public int getWorldHeight() {
        return mc.world.getHeight();
    }

    @Override
    public String getSaveName() {
        if (mc.isWorldRemote()) {
            return mc.options.lastServer;
        }

        mc.world.attemptSaving(0);
        List<WorldSaveInfo> saves = mc.getWorldStorageSource().getAll();
        if (saves == null || saves.isEmpty()) return null;
        String currentName = mc.world.getProperties().getName();
        WorldSaveInfo best = null;
        long bestTime = Long.MIN_VALUE;
        for (WorldSaveInfo info : saves) {
            if (info.getName().equals(currentName)) {
                long t = info.getLastPlayed();
                if (t > bestTime) {
                    bestTime = t;
                    best = info;
                }
            }
        }

        if (best != null) {
            return best.getSaveName();
        }
        return null;
    }

    @Override
    public long getTime() {
        return mc.world.getTime();
    }

    @Override
    public long getSeed() {
        return mc.world.getSeed();
    }

    @Override
    public int getDimensionId() {
        return mc.world.dimension.id;
    }

    @Override
    public boolean hasCeiling() {
        return mc.world.dimension.hasCeiling;
    }

    @Override
    public boolean isServer() {
        return mc.world.isRemote;
    }

    @Override
    public NABiome getBiome(NACoord blockCoord) {
        Biome biome = mc.world.method_1781().getBiome(blockCoord.x, blockCoord.z);
        return new NABiome(biome.grassColor, biome.foliageColor);
    }

    @Override
    public List<NAEntity> getEntities() {
        List<NAEntity> entities = new ArrayList<>();

        for (Object o : mc.world.entities) {
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

        for (Object o : mc.world.players) {
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

    @Override
    public ChunkWrapper getChunk(NACoord chunkCoord) {
        Chunk chunk = mc.world.getChunk(chunkCoord.x, chunkCoord.z);
        if (chunk == null) return null;

        return new ChunkWrapper(chunk, getWorldHeight()) {
            @Override
            public int getBlockId(int x, int y, int z) {
                return ((Chunk) chunk).getBlockId(x, y, z);
            }

            @Override
            public int getBlockMeta(int x, int y, int z) {
                return ((Chunk) chunk).getBlockMeta(x, y, z);
            }

            @Override
            public int getBlockLight(int x, int y, int z) {
                return ((Chunk) chunk).getLight(LightType.BLOCK, x, y, z);
            }

            @Override
            public int getSkyLight(int x, int y, int z) {
                return ((Chunk) chunk).getLight(LightType.SKY, x, y, z);
            }
        };
    }
}
