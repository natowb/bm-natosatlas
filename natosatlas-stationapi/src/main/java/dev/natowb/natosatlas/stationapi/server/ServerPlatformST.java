package dev.natowb.natosatlas.stationapi.server;

import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.server.ServerPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.LightType;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFile;
import net.modificationstation.stationapi.impl.world.chunk.FlattenedWorldChunkLoader;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ServerPlatformST implements ServerPlatform {

    @Override
    public String getLevelName() {
        MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        String worldName = server.properties.getProperty("level-name", "world");
        return worldName;
    }


    @Override
    public List<NARegionFile> getRegionFiles(int dim) {
        List<NARegionFile> result = new ArrayList<>();
        File regionDir = getRegionDirectory(dim).toFile();

        File[] regionFiles = regionDir.listFiles((dir, name) ->
                name.endsWith(".mcr") || name.endsWith(".mca")
        );

        if (regionFiles == null) return result;

        for (File regionFile : regionFiles) {
            NARegionFile rf = getRegionFile(regionFile);
            if (rf != null) {
                result.add(rf);
            }
        }

        return result;
    }

    @Override
    public NARegionFile getRegionFile(File regionFile) {
        try {
            String name = regionFile.getName();

            if (!name.startsWith("r.") ||
                    !(name.endsWith(".mca") || name.endsWith(".mcr"))) {
                return null;
            }

            String core = name.substring(2, name.length() - 4);
            String[] parts = core.split("\\.");
            if (parts.length != 2) return null;

            int rx = Integer.parseInt(parts[0]);
            int rz = Integer.parseInt(parts[1]);
            NACoord regionCoord = new NACoord(rx, rz);

            NARegionFile naRegion = new NARegionFile(regionFile, regionCoord);

            RegionFile rf = new RegionFile(regionFile);
            try {
                for (int x = 0; x < NARegionFile.CHUNKS_PER_REGION; x++) {
                    for (int z = 0; z < NARegionFile.CHUNKS_PER_REGION; z++) {
                        if (rf.hasChunkData(x, z)) {
                            naRegion.chunkExists[x][z] = true;
                        }
                    }
                }
            } finally {
                rf.close();
            }

            return naRegion;

        } catch (Exception e) {
            return null;
        }
    }

    private File getDimensionDirectory(int dim) {

        MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        String worldName = server.properties.getProperty("level-name", "world");
        Path worldDir = FabricLoader.getInstance().getGameDir().resolve(worldName);
        if (dim == 0) {
            return worldDir.toFile();
        } else if (dim == -1) {
            return worldDir.resolve("DIM-1").toFile();
        }
        return null;
    }

    @Override
    public Path getRegionDirectory(int dim) {
        File dimDir = getDimensionDirectory(dim);
        File regionDir = new File(dimDir, "region");
        return regionDir.toPath();
    }

    @Override
    public List<NAEntity> getPlayers(int dim) {
        List<NAEntity> players = new ArrayList<>();
        MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();

        ServerWorld world = server.getWorld(dim);
        if (world == null) return players;

        for (Object o : world.players) {
            PlayerEntity p = (PlayerEntity) o;
            NAEntity player = new NAEntity(p.x, p.y, p.z, p.yaw, NAEntity.NAEntityType.Player);
            player.setName(p.name != null ? p.name : "Unknown");
            players.add(player);
        }
        return players;
    }

    @Override
    public ChunkWrapper getChunk(int dim, NACoord chunkCoord) {
        try {
            MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();

            File dimDir = getDimensionDirectory(dim);

            if (dimDir == null) return null;

            ServerWorld world = server.getWorld(dim);
            if (world == null) return null;


            FlattenedWorldChunkLoader loader = new FlattenedWorldChunkLoader(dimDir);

            int cx = chunkCoord.x;
            int cz = chunkCoord.z;


            //FIXME: we override system out here as the chunkloader prints annoying shit to the
            var originalOut = System.out;
            System.setOut(new PrintStream(OutputStream.nullOutputStream()));

            Chunk mcChunk;
            try {
                mcChunk = loader.loadChunk(world, cx, cz);
            } finally {
                System.setOut(originalOut);
            }

            if (mcChunk == null) {
                LogUtil.warn("Chunk {} {} does not exist on disk", cx, cz);
                return null;
            }

            return new ChunkWrapper(mcChunk, world.getHeight()) {

                @Override
                public int getBlockId(int x, int y, int z) {
                    return mcChunk.getBlockId(x, y, z);
                }

                @Override
                public int getBlockMeta(int x, int y, int z) {
                    return mcChunk.getBlockMeta(x, y, z);
                }

                @Override
                public int getBlockLight(int x, int y, int z) {
                    return mcChunk.getLight(LightType.BLOCK, x, y, z);
                }

                @Override
                public int getSkyLight(int x, int y, int z) {
                    return mcChunk.getLight(LightType.SKY, x, y, z);
                }
            };

        } catch (Exception e) {
            LogUtil.error("Failed to load chunk {}: {}", chunkCoord, e);
            return null;
        }
    }

}
