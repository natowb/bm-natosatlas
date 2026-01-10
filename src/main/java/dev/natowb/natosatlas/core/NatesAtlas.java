package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.glue.INacChunkGenerator;
import dev.natowb.natosatlas.core.glue.INacEntityAdapter;
import dev.natowb.natosatlas.core.glue.INacFileProvider;
import dev.natowb.natosatlas.core.models.NacEntity;
import dev.natowb.natosatlas.core.painter.INacPainter;
import dev.natowb.natosatlas.core.regions.NacRegionCache;
import dev.natowb.natosatlas.core.regions.NacRegionManager;
import dev.natowb.natosatlas.core.storage.NacPngRegionStorage;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NatesAtlas {

    public static final Logger logger = Logger.getLogger("NatesAtlas");

    private static NatesAtlas INSTANCE = null;

    public static NatesAtlas getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("NatesAtlas has not been initialized yet!");
        }
        return INSTANCE;
    }

    public final NacRegionCache regionCache;
    public final NacRegionManager regionManager;

    public final INacFileProvider fileProvider;
    public final INacEntityAdapter entityAdapter;
    public final INacPainter painter;

    private void setupLogger() {
        java.util.logging.ConsoleHandler handler = new java.util.logging.ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
    }

    public NatesAtlas(INacFileProvider fileProvider, INacChunkGenerator chunkHandler, INacEntityAdapter entityAdapter, INacPainter painter) {

        if (INSTANCE != null) {
            throw new IllegalStateException("NatesAtlas instance already created!");
        }
        INSTANCE = this;

        setupLogger();

        this.fileProvider = fileProvider;
        this.entityAdapter = entityAdapter;
        this.painter = painter;

        regionCache = new NacRegionCache(chunkHandler, new NacPngRegionStorage(fileProvider));
        regionManager = new NacRegionManager(regionCache);
    }

    public void onWorldJoin() {
        Path dataDir = fileProvider.getDataDirectory();
        logger.info("data directory set to = " + dataDir.toAbsolutePath());
        regionCache.loadFromDisk();
    }

    public void onWorldLeft() {
        regionCache.clear();
    }

    public void onWorldUpdate() {
        NacEntity player = entityAdapter.getLocalPlayer();

        double px = player.worldX;
        double pz = player.worldZ;

        int chunkX = (int) Math.floor(px / 16.0);
        int chunkZ = (int) Math.floor(pz / 16.0);

        regionManager.update(chunkX, chunkZ);
    }
}
