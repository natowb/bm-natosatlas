package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.glue.NacPlatform;
import dev.natowb.natosatlas.core.models.NacEntity;
import dev.natowb.natosatlas.core.regions.NacRegionCache;
import dev.natowb.natosatlas.core.regions.NacRegionManager;
import dev.natowb.natosatlas.core.storage.NacPngRegionStorage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NAC {

    public static final Logger logger = Logger.getLogger("NatosAtlas");
    private static NAC instance = null;

    public static NAC get() {
        return instance;
    }

    public final NacRegionCache regionCache;
    public final NacRegionManager regionManager;

    private void setupLogger() {
        logger.setUseParentHandlers(true);
        logger.setLevel(Level.ALL);
    }

    public NAC(NacPlatform platformAPI) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }
        instance = this;

        NacPlatform.setInstance(platformAPI);

        setupLogger();
        regionCache = new NacRegionCache(NacPlatform.get().chunkProvider, new NacPngRegionStorage());
        regionManager = new NacRegionManager(regionCache);
    }

    public void onWorldJoin() {
        regionCache.loadFromDisk();
        NACWaypoints.load();
        NACSettings.load();
    }

    public void onWorldLeft() {
        regionCache.clear();
    }

    public void onWorldUpdate() {
        NacEntity player = NacPlatform.get().entityProvider.getLocalPlayer();

        double px = player.x;
        double pz = player.z;

        int chunkX = (int) Math.floor(px / 16.0);
        int chunkZ = (int) Math.floor(pz / 16.0);

        regionManager.update(chunkX, chunkZ);
    }
}
