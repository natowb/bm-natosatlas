package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacEntity;
import dev.natowb.natosatlas.core.regions.NacRegionCache;
import dev.natowb.natosatlas.core.regions.NacRegionManager;
import dev.natowb.natosatlas.core.regions.NacRegionStorage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NacMod {

    public static final Logger logger = Logger.getLogger("NatosAtlas");
    private static NacMod instance = null;

    public static NacMod get() {
        return instance;
    }

    public final NacRegionCache regionCache;
    public final NacRegionManager regionManager;

    private void setupLogger() {
        logger.setUseParentHandlers(true);
        logger.setLevel(Level.ALL);
    }

    public NacMod(NacPlatformAPI platformAPI) {
        if (instance != null) {
            throw new IllegalStateException("NatosAtlas instance already created!");
        }
        instance = this;

        NacPlatformAPI.setInstance(platformAPI);

        setupLogger();
        regionCache = new NacRegionCache(new NacRegionStorage());
        regionManager = new NacRegionManager(regionCache);
    }

    public void onWorldJoin() {
        regionCache.loadFromDisk();
        NacWaypoints.load();
        NacSettings.load();
    }

    public void onWorldLeft() {
        regionCache.clear();
    }

    public void onWorldUpdate() {
        NacEntity player = NacPlatformAPI.get().entityProvider.getLocalPlayer();

        double px = player.x;
        double pz = player.z;

        int chunkX = (int) Math.floor(px / 16.0);
        int chunkZ = (int) Math.floor(pz / 16.0);

        regionManager.update(chunkX, chunkZ);
    }
}
