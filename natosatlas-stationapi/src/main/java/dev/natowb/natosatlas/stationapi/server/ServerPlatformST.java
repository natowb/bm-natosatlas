package dev.natowb.natosatlas.stationapi.server;

import dev.natowb.natosatlas.core.chunk.ChunkBuilderOld;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.server.NAServerPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.RegionFile;
import net.modificationstation.stationapi.impl.world.chunk.FlattenedWorldChunkLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerPlatformST implements NAServerPlatform {

    @Override
    public String getLevelName() {
        MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        String worldName = server.properties.getProperty("level-name", "world");
        return worldName;
    }

    @Override
    public List<NARegionFile> getRegionFiles() {
        List<NARegionFile> result = new ArrayList<>();

        MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
        String worldName = server.properties.getProperty("level-name", "world");
        File worldDir = FabricLoader.getInstance().getGameDir().resolve(worldName).toFile();
        File regionDir = new File(worldDir, "region");

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
                LogUtil.info("[{}/{}] Failed to process region file: {}", index, regionFiles.length, regionFile.getName());
            }
        }
        return result;
    }

    @Override
    public ChunkWrapper getChunk(NACoord chunkCoord) {
        try {
            MinecraftServer server = (MinecraftServer) FabricLoader.getInstance().getGameInstance();
            File worldDir = FabricLoader.getInstance().getGameDir().resolve(server.properties.getProperty("level-name", "world")).toFile();
            FlattenedWorldChunkLoader loader = new FlattenedWorldChunkLoader(worldDir);

            int cx = chunkCoord.x;
            int cz = chunkCoord.z;

            Chunk mcChunk = loader.loadChunk(server.getWorld(0), cx, cz);
            if (mcChunk == null) {
                LogUtil.warn("Chunk {} {} does not exist on disk", cx, cz);
                return null;
            }

            return new ChunkWrapper(mcChunk, 128) {

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
