package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.save.SaveFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldAccessBTA extends ClientWorldAccess {
    private static final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

    @Override
    public NAWorldInfo getWorldInfo() {
        if (mc.currentWorld == null) return null;
        int worldHeight = 128;
        String name = mc.currentWorld.getLevelData().getWorldName();
        long time = mc.currentWorld.getWorldTime();
        long seed = mc.currentWorld.getRandomSeed();
        int dimensionId = mc.currentWorld.dimension.id;
        boolean hasCeiling = mc.currentWorld.getWorldType().hasCeiling();
        boolean multiplayer = mc.isMultiplayerWorld();

        return new NAWorldInfo(worldHeight, name, time, seed, dimensionId, hasCeiling, multiplayer);
    }

    @Override
    public String getSaveName() {
        if (mc.isMultiplayerWorld()) {
            return mc.gameSettings.lastServer.name;
        }

        mc.currentWorld.pauseScreenSave(0);
        List<SaveFile> saves = mc.getSaveFormat().getSaveFileList();
        if (saves == null || saves.isEmpty()) return null;

        String currentName = mc.currentWorld.getLevelData().getWorldName();
        SaveFile best = null;
        long bestTime = Long.MIN_VALUE;

        for (SaveFile info : saves) {
            if (info.getDisplayName().equals(currentName)) {
                long t = info.getLastTimePlayed();
                if (t > bestTime) {
                    bestTime = t;
                    best = info;
                }
            }
        }

        return best != null ? best.getFileName() : null;
    }

    @Override
    public NABiome getBiome(NACoord blockCoord) {
        Biome biome = mc.currentWorld.getBiomeProvider().getBiome(blockCoord.x, 50, blockCoord.z);
        return new NABiome(biome.topBlock, biome.color);
    }

    @Override
    public List<NAEntity> getEntities() {
        List<NAEntity> entities = new ArrayList<>();

        for (Object o : mc.currentWorld.loadedEntityList) {
            if (!(o instanceof Mob)) continue;
            if (o instanceof Player) continue;

            Mob e = (Mob) o;

            NAEntity.NAEntityType type = NAEntity.NAEntityType.Mob;

            if (e instanceof MobAnimal) {
                type = NAEntity.NAEntityType.Animal;
            }

            entities.add(new NAEntity(e.x, e.y, e.z, e.yRot, type).setTexturePath(e.getEntityTexture()));
        }

        return entities;
    }

    @Override
    public List<NAEntity> getPlayers() {
        List<NAEntity> players = new ArrayList<>();

        for (Player p : mc.currentWorld.players) {
            players.add(new NAEntity(p.x, p.y, p.z, p.yRot, NAEntity.NAEntityType.Player));
        }

        return players;
    }

    @Override
    public NAEntity getPlayer() {
        Player p = mc.thePlayer;
        return new NAEntity(p.x, p.y, p.z, p.yRot, NAEntity.NAEntityType.Player);
    }


    @Override
    public ChunkWrapper getChunk(NACoord chunkCoord) {
        Chunk chunk = mc.currentWorld.getChunkFromChunkCoords(chunkCoord.x, chunkCoord.z);
        if (chunk == null) return null;

        return new ChunkWrapper(chunk, mc.currentWorld.getHeightBlocks()) {
            @Override
            public int getBlockId(int x, int y, int z) {
                return ((Chunk) chunk).getBlockID(x, y, z);
            }

            @Override
            public int getBlockMeta(int x, int y, int z) {
                return ((Chunk) chunk).getBlockMetadata(x, y, z);
            }

            @Override
            public int getBlockLight(int x, int y, int z) {
                return ((Chunk) chunk).getBrightness(LightLayer.Block, x, y, z);
            }

            @Override
            public int getSkyLight(int x, int y, int z) {
                return ((Chunk) chunk).getBrightness(LightLayer.Sky, x, y, z);
            }
        };
    }

    @Override
    public List<NARegionFile> getRegionFiles(File dimDir) {
        return new ArrayList<>();
    }
}