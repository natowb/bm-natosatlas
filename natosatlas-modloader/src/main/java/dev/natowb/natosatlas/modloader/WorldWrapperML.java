package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;
import dev.natowb.natosatlas.core.wrapper.ChunkWrapper;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.src.ModLoader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionChunkStorage;
import net.minecraft.world.chunk.storage.RegionFile;

import java.io.File;
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

    @Override
    public ChunkWrapper getChunk(NACoord chunkCoord) {
        Chunk chunk = world.getChunk(chunkCoord.x, chunkCoord.z);
        if (chunk == null) return null;


        return new ChunkWrapper(chunk) {
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

    @Override
    public ChunkWrapper getChunkFromDisk(NACoord chunkCoord) {
        RegionChunkStorage chunkLoader = new RegionChunkStorage(NAPaths.getWorldSavePath().toFile());
        Chunk chunk = chunkLoader.loadChunk(mc.world, chunkCoord.x, chunkCoord.z);

        if (chunk == null) return null;

        return new ChunkWrapper(chunk) {
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

    @Override
    public List<NARegionFile> getRegionFiles() {
        List<NARegionFile> result = new ArrayList<>();

        File regionDir = new File(NAPaths.getWorldSavePath().toFile(), "region");
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
                        if (rf.hasChunkData(x, z)) {
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
