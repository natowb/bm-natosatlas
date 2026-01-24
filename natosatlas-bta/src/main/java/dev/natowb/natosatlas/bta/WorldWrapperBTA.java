package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.wrapper.ChunkWrapper;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkLoaderRegion;
import net.minecraft.core.world.save.mcregion.RegionFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldWrapperBTA implements WorldWrapper {
    private static final Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

    private final World world;
    private final String worldSaveName;

    public WorldWrapperBTA(World world, String worldSaveName) {
        this.world = world;
        this.worldSaveName = worldSaveName;
    }

    @Override
    public String getName() {
        return world.getLevelData().getWorldName();
    }

    @Override
    public String getSaveName() {
        return worldSaveName;
    }

    @Override
    public long getTime() {
        return world.getWorldTime();
    }

    @Override
    public long getSeed() {
        return world.getRandomSeed();
    }

    @Override
    public int getDimensionId() {
        return world.dimension.id;
    }

    @Override
    public boolean isServer() {
        return world.isClientSide;
    }

    @Override
    public NABiome getBiome(NACoord blockCoord) {
        Biome biome = world.getBiomeProvider().getBiome(blockCoord.x, 50, blockCoord.z);
        return new NABiome(biome.topBlock, biome.color);
    }

    @Override
    public List<NAEntity> getEntities() {
        List<NAEntity> entities = new ArrayList<>();

        for (Object o : world.loadedEntityList) {
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

        for (Player p : world.players) {
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
        Chunk chunk = world.getChunkFromChunkCoords(chunkCoord.x, chunkCoord.z);
        if (chunk == null) return null;


        return new ChunkWrapper(chunk) {
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