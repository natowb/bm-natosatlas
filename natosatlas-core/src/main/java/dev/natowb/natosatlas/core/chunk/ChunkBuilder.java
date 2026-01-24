package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NABiome;
import dev.natowb.natosatlas.core.data.NAChunk;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.map.MapCache;
import dev.natowb.natosatlas.core.layers.MapLayer;
import dev.natowb.natosatlas.core.map.MapRegion;
import dev.natowb.natosatlas.core.map.MapStorage;
import dev.natowb.natosatlas.core.tasks.MapSaveScheduler;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.access.BlockAccess;
import dev.natowb.natosatlas.core.access.WorldAccess;

import java.io.File;
import java.util.List;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_MINECRAFT_CHUNK;

public class ChunkBuilder {


    public static NAChunk buildChunkSurface(NACoord chunkCoord) {
        ChunkWrapper chunk = WorldAccess.getInstance().getChunk(chunkCoord);
        if (chunk == null) {
            return null;
        }
        return buildSurface(chunkCoord, chunk);
    }


    public static NAChunk buildChunkSurfaceFromDisk(NACoord chunkCoord) {
        ChunkWrapper chunk = WorldAccess.getInstance().getChunkFromDisk(chunkCoord);
        if (chunk == null) {
            return null;
        }
        return buildSurface(chunkCoord, chunk);
    }


    public static void rebuildExistingChunks(MapStorage storage, MapCache cache) {
        List<NARegionFile> regions = WorldAccess.getInstance().getRegionFiles();

        if (regions.isEmpty()) {
            LogUtil.info("No region metadata found.");
            return;
        }

        MapSaveScheduler.stop();

        LogUtil.info("Generating map data for all existing regions (this may take a while...)");

        int index = 0;
        int total = regions.size();

        for (NARegionFile naRegion : regions) {
            index++;

            NACoord regionCoord = naRegion.regionCoord;
            boolean success = false;

            try {
                MapRegion[] layers = new MapRegion[NatosAtlas.get().layers.getLayers().size()];
                for (int i = 0; i < layers.length; i++) {
                    layers[i] = new MapRegion();
                }

                for (NACoord chunkCoord : naRegion.iterateExistingChunks()) {
                    int layerIndex = 0;
                    for (MapLayer layer : NatosAtlas.get().layers.getLayers()) {
                        layer.renderer.applyChunkToRegion(layers[layerIndex], chunkCoord, layer.usesBlockLight);
                        layerIndex++;
                    }
                }

                for (int layerId = 0; layerId < layers.length; layerId++) {
                    File out = storage.getRegionPngFile(layerId, regionCoord);
                    storage.saveRegionBlocking(regionCoord, layers[layerId], out);
                }

                success = true;

            } catch (Exception ignored) {
            }

            if (success) {
                LogUtil.info("[{}/{}] Successfully generated region r({}, {})",
                        index, total, regionCoord.x, regionCoord.z);
            } else {
                LogUtil.info("[{}/{}] Failed to generate region r({}, {})",
                        index, total, regionCoord.x, regionCoord.z);
            }
        }

        LogUtil.info("Full region generation complete.");
        cache.clear();
        MapSaveScheduler.start();
    }


    private static NAChunk buildSurface(NACoord chunkCoord, ChunkWrapper chunk) {
        NAChunk nac = new NAChunk();
        for (int z = 0; z < BLOCKS_PER_MINECRAFT_CHUNK; z++) {
            for (int x = 0; x < BLOCKS_PER_MINECRAFT_CHUNK; x++) {
                int worldBlockX = chunkCoord.x * BLOCKS_PER_MINECRAFT_CHUNK + x;
                int worldBlockZ = chunkCoord.z * BLOCKS_PER_MINECRAFT_CHUNK + z;

                int height = chunk.getTopSolidBlockY(x, z) - 1;
                int aboveId = chunk.getBlockId(x, height + 1, z);
                if (BlockAccess.getInstance().isBlock(aboveId, BlockAccess.BlockIdentifier.SNOW)) {
                    height = height + 1;
                }

                int blockId = chunk.getBlockId(x, height, z);
                int depth = chunk.computeFluidDepth(x, height, z);
                int blockLight = chunk.getBlockLight(x, height + 1, z);
                int meta = chunk.getBlockMeta(x, height, z);
                NABiome biome = WorldAccess.getInstance().getBiome(NACoord.from(worldBlockX, worldBlockZ));
                nac.set(x, z, height, blockId, depth, blockLight, meta, biome);
            }
        }
        return nac;
    }

    public static NAChunk buildCaveChunk(NACoord chunkCoord) {
        int playerY = (int) WorldAccess.getInstance().getPlayer().y;
        ChunkWrapper chunk = WorldAccess.getInstance().getChunk(chunkCoord);
        if (chunk == null) return null;

        NAChunk caveChunk = new NAChunk();

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {

                int floorY = findTopmostCaveFloor(chunk, x, z, playerY);

                if (floorY < 0) {
                    caveChunk.set(x, z, -1, 0, 0, 0, 0, null);
                    continue;
                }

                int blockId = chunk.getBlockId(x, floorY, z);
                int blockLight = chunk.getBlockLight(x, floorY, z);
                int meta = chunk.getBlockMeta(x, floorY, z);

                int worldX = chunkCoord.x * 16 + x;
                int worldZ = chunkCoord.z * 16 + z;
                NABiome biome = WorldAccess.getInstance().getBiome(NACoord.from(worldX, worldZ));

                caveChunk.set(x, z, floorY, blockId, 0, blockLight, meta, biome);
            }
        }

        return caveChunk;
    }


    private static int findTopmostCaveFloor(ChunkWrapper chunk, int x, int z, int playerY) {


        int startY = Math.min(playerY, 127);

        for (int y = startY; y > 1; y--) {

            int block = chunk.getBlockId(x, y, z);

            if (block != 0) continue;

            if (chunk.getSkyLight(x, y, z) > 0) continue;

            for (int fy = y - 1; fy > 0; fy--) {
                int floorBlock = chunk.getBlockId(x, fy, z);
                if (floorBlock != 0) {
                    return fy;
                }
            }

            return -1;
        }

        return -1;
    }


}
