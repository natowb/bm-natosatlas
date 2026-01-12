package dev.natowb.natosatlas.core.regions;

import dev.natowb.natosatlas.core.glue.NacPlatform;
import dev.natowb.natosatlas.core.models.NacRegionData;

public class NacRegionManager {

    private final NacRegionCache regionCache;
    private static final int RADIUS = 8;
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_SAVE_INTERVAL = 1;
    private static final int SAVE_INTERVAL_TICKS = TICKS_PER_SECOND * SECONDS_PER_SAVE_INTERVAL;


    private int activeChunkX, activeChunkZ;
    private int saveTimer = 0;

    public int getActiveChunkX() {
        return activeChunkX;
    }

    public int getActiveChunkZ() {
        return activeChunkZ;
    }

    public NacRegionManager(NacRegionCache regionCache) {
        this.regionCache = regionCache;
    }


    public void update(int playerChunkX, int playerChunkZ) {

        if (!NacPlatform.get().getCurrentWorldInfo().isPlayerInOverworld) {
            return;
        }

        this.activeChunkX = playerChunkX;
        this.activeChunkZ = playerChunkZ;

        updateNearbyChunks(playerChunkX, playerChunkZ);

        saveTimer++;
        if (saveTimer >= SAVE_INTERVAL_TICKS) {
            saveTimer = 0;
            regionCache.saveOneRegion();
        }

    }

    private void updateNearbyChunks(int playerChunkX, int playerChunkZ) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {

                if (dx * dx + dz * dz > RADIUS * RADIUS) continue;

                int chunkX = playerChunkX + dx;
                int chunkZ = playerChunkZ + dz;

                int[] pixels = regionCache.chunkHandler.getChunkPixels(chunkX, chunkZ);

                regionCache.writeChunk(chunkX, chunkZ, pixels);
            }
        }
    }

    public int getTexture(int rx, int rz) {
        NacRegionData tile = regionCache.getTile(rx, rz);
        if (tile == null) return -1;
        tile.updateTexture();
        return tile.getTextureId();
    }

    public int getCacheSize() {
        return regionCache.get().size();
    }
}
