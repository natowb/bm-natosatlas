package dev.natowb.natosatlas.core.regions;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacChunk;
import dev.natowb.natosatlas.core.models.NacRegionData;
import dev.natowb.natosatlas.core.renderer.NacRegionRendererDefault;

public class NacRegionManager {

    private static final int RADIUS = 8;
    private static final int TICKS_PER_SECOND = 20;

    private static final int SAVE_INTERVAL_TICKS = 30 * TICKS_PER_SECOND;
    private static final int UPDATE_INTERVAL_TICKS = 1 * TICKS_PER_SECOND;

    private final NacRegionCache cache;

    private int activeChunkX, activeChunkZ;
    private int saveTimer = 0;
    private int updateTimer = 0;

    public NacRegionManager(NacRegionCache cache) {
        this.cache = cache;
    }

    public int getActiveChunkX() {
        return activeChunkX;
    }

    public int getActiveChunkZ() {
        return activeChunkZ;
    }

    public void update(int playerChunkX, int playerChunkZ) {

        if (!NacPlatformAPI.get().getCurrentWorldInfo().isPlayerInOverworld) {
            return;
        }

        this.activeChunkX = playerChunkX;
        this.activeChunkZ = playerChunkZ;

        if (++updateTimer >= UPDATE_INTERVAL_TICKS) {
            updateTimer = 0;
            updateNearbyChunks(playerChunkX, playerChunkZ);
        }

        if (++saveTimer >= SAVE_INTERVAL_TICKS) {
            saveTimer = 0;
            cache.saveOneRegion();
        }
    }

    private void updateNearbyChunks(int playerChunkX, int playerChunkZ) {
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {

                if (dx * dx + dz * dz > RADIUS * RADIUS) continue;

                int chunkX = playerChunkX + dx;
                int chunkZ = playerChunkZ + dz;

                NacChunk chunk = NacPlatformAPI.get().chunkProvider.buildChunk(chunkX, chunkZ);
                if (chunk != null) {
                    cache.writeChunk(chunkX, chunkZ, chunk);
                }
            }
        }
    }

    public int getTexture(int rx, int rz) {
        NacRegionData region = cache.getRegion(rx, rz);
        if (region == null) return -1;
        region.updateTexture(new NacRegionRendererDefault());
        return region.getTextureId();
    }

    public int getCacheSize() {
        return cache.getCacheSize();
    }
}
