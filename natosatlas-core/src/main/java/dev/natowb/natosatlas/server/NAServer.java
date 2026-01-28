package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NARegionGenerator;
import dev.natowb.natosatlas.core.NASession;
import dev.natowb.natosatlas.core.chunk.ChunkRenderer;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.core.storage.NARegionStorage;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class NAServer implements NASession {

    private boolean started;
    private final NAServerPlatform platform;
    private final Queue<NARegionFile> regionQueue = new ArrayDeque<>();

    private final Path minecraftPath;

    public NAServer(Path minecraftPath, NAServerPlatform platform) {
        this.minecraftPath = minecraftPath;
        this.platform = platform;
    }

    @Override
    public void tick() {
        if (!started) {
            start();
            started = true;
        }
    }

    private void start() {
        LogUtil.info("Server Started");

        List<NARegionFile> regions = platform.getRegionFiles();
        regionQueue.addAll(regions);
        LogUtil.info("Queued {} regions for processing", regions.size());

        Thread worker = new Thread(new RegionWorker(), "NAServer-RegionWorker");
        worker.setDaemon(true);
        worker.start();
    }

    private class RegionWorker implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    processNextBatch();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private void processNextBatch() {
            List<NARegionFile> regions = platform.getRegionFiles();
            if (regions.isEmpty()) {
                LogUtil.info("No regions to process");
                return;
            }

            NARegionGenerator generator = new NARegionGenerator(regions, platform::getChunk, NAServer.this::buildOutputFile);
            generator.generateAll();
        }
    }


    private File buildOutputFile(int layerId, NACoord regionCoord) {
        String levelName = platform.getLevelName();

        File baseDir = minecraftPath
                .resolve("natosatlas")
                .resolve(levelName)
                .resolve(String.valueOf(layerId))
                .toFile();

        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        String fileName = "region_" + regionCoord.x + "_" + regionCoord.z + ".png";
        return new File(baseDir, fileName);
    }
}

