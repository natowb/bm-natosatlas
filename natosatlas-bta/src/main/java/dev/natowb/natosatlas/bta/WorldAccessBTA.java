package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;
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
import net.minecraft.core.world.save.mcregion.RegionFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldAccessBTA extends ClientWorldAccess {
    private static final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

    @Override
    public int getWorldHeight() {
        return mc.currentWorld.getHeightBlocks();
    }

    @Override
    public boolean exists() {
        return mc.currentWorld != null;
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

        if (best != null) {
            return best.getFileName();
        }
        return null;
    }

    @Override
    public String getName() {
        return mc.currentWorld.getLevelData().getWorldName();
    }

    @Override
    public long getTime() {
        return mc.currentWorld.getWorldTime();
    }

    @Override
    public long getSeed() {
        return mc.currentWorld.getRandomSeed();
    }

    @Override
    public int getDimensionId() {
        return mc.currentWorld.dimension.id;
    }

    @Override
    public boolean hasCeiling() {
        return mc.currentWorld.getWorldType().hasCeiling();
    }

    @Override
    public boolean isServer() {
        return mc.currentWorld.isClientSide;
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


        return new ChunkWrapper(chunk, getWorldHeight()) {

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
    public ChunkWrapper getChunkFromDisk(NACoord chunkCoord) {
        return null;
    }

    @Override
    public List<NARegionFile> getRegionFiles() {
        List<NARegionFile> result = new ArrayList<>();

        File regionDir = new File(NAPaths.getWorldSavePath().toFile(), String.format("dimensions/%d/region", mc.currentWorld.dimension.id));
        File[] regionFiles = regionDir.listFiles((dir, name) -> name.endsWith(".mcr") || name.endsWith(".mca"));
        if (regionFiles == null || regionFiles.length == 0) {
            return result;
        }

        int index = 0;

        for (File regionFile : regionFiles) {
            index++;

            boolean success = false;

            try {
                String name = regionFile.getName();
                String[] parts = name.substring(2, name.length() - 4).split("\\.");
                if (parts.length != 2) {
                    continue;
                }

                int rx = Integer.parseInt(parts[0]);
                int rz = Integer.parseInt(parts[1]);
                NACoord regionCoord = new NACoord(rx, rz);

                NARegionFile naRegion = new NARegionFile(regionFile, regionCoord);

                RegionFile rf = new RegionFile(regionFile);
                for (int x = 0; x < NARegionFile.CHUNKS_PER_REGION; x++) {
                    for (int z = 0; z < NARegionFile.CHUNKS_PER_REGION; z++) {
                        if (rf.chunkExists(x, z)) {
                            naRegion.chunkExists[x][z] = true;
                        }
                    }
                }
                rf.close();

                result.add(naRegion);
                success = true;

            } catch (Exception ignored) {
            }

            if (success) {
                LogUtil.info("[{}/{}] Successfully processed region file: {}", index, regionFiles.length, regionFile.getName());

            } else {
                LogUtil.info("[{}/{}] Failed to processed region file: {}", index, regionFiles.length, regionFile.getName());
            }
        }

        return result;
    }
}