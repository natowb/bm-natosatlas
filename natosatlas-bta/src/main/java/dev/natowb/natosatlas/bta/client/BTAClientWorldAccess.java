package dev.natowb.natosatlas.bta.client;

import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.core.util.LogUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.lang.text.TranslatableText;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkLoaderRegion;
import net.minecraft.core.world.save.mcregion.RegionFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BTAClientWorldAccess extends ClientWorldAccess {
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
    public String getOfflineSaveName() {
        if (mc.isMultiplayerWorld()) {
            return null;
        }
        return mc.currentWorld.saveHandler.getDataFile("fake").getParentFile().getParentFile().getName();
    }

    @Override
    public File getDimDirectory(File worldDir) {
        int dim = getWorldInfo().getDimensionId();
        return new File(worldDir, String.format("dimensions/%d", dim));
    }

    @Override
    public NABiome getBiome(NACoord blockCoord) {
        Biome biome = mc.currentWorld.getBiomeProvider().getBiome(blockCoord.x, 50, blockCoord.z);
        return new NABiome(biome.topBlock, biome.color, TranslatableText.text().trans(biome.translationKey).toString());
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
            NAEntity player = new NAEntity(p.x, p.y, p.z, p.yRot, NAEntity.NAEntityType.Player);
            player.setName(p.getDisplayName());
            if (p.isSneaking()) {
                player.isCrouching = true;
            }

            if (p.gamemode == Gamemode.spectator) {
                player.isSpectating = true;
            }
            players.add(player);
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
    public ChunkWrapper getChunkFromDisk(NACoord chunkCoord, File dimDir) {
        try {

            ChunkLoaderRegion loader = new ChunkLoaderRegion(dimDir);

            int cx = chunkCoord.x;
            int cz = chunkCoord.z;

            Chunk mcChunk = loader.loadChunk(mc.currentWorld, cx, cz);
            if (mcChunk == null) {
                LogUtil.warn("Chunk {} {} does not exist on disk", cx, cz);
                return null;
            }

            return new ChunkWrapper(mcChunk, mc.currentWorld.getHeightBlocks()) {

                @Override
                public int getBlockId(int x, int y, int z) {
                    return mcChunk.getBlockID(x, y, z);
                }

                @Override
                public int getBlockMeta(int x, int y, int z) {
                    return mcChunk.getBlockMetadata(x, y, z);
                }

                @Override
                public int getBlockLight(int x, int y, int z) {
                    return mcChunk.getBrightness(LightLayer.Block, x, y, z);
                }

                @Override
                public int getSkyLight(int x, int y, int z) {
                    return mcChunk.getBrightness(LightLayer.Sky, x, y, z);
                }
            };

        } catch (Exception e) {
            LogUtil.error("Failed to load chunk {}: {}", chunkCoord, e);
            return null;
        }
    }

    @Override
    public List<NARegionFile> getRegionFiles(File dimDir) {
        List<NARegionFile> result = new ArrayList<>();
        File regionDir = new File(dimDir, "region");

        File[] regionFiles = regionDir.listFiles((dir, name) ->
                name.endsWith(".mcr") || name.endsWith(".mca")
        );

        if (regionFiles == null) return result;

        int index = 0;

        for (File regionFile : regionFiles) {
            index++;

            boolean success = false;

            try {
                String name = regionFile.getName();
                String[] parts = name.substring(2, name.length() - 4).split("\\.");
                if (parts.length != 2) continue;

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
                LogUtil.info("[{}/{}] Failed to process region file: {}", index, regionFiles.length, regionFile.getName());
            }
        }
        return result;
    }
}