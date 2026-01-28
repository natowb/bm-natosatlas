package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.NASession;
import dev.natowb.natosatlas.core.chunk.ChunkRenderer;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;
import dev.natowb.natosatlas.core.io.NARegionStorage;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class NAServer implements NASession {

    private boolean started;
    private final NAServerPlatform platform;
    private final Queue<NARegionFile> regionQueue = new ArrayDeque<>();

    public NAServer(NAServerPlatform platform) {
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
                    processNextRegion();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private void processNextRegion() {
            NARegionFile regionFile = regionQueue.poll();

            if (regionFile == null) {
                refreshRegionQueue();
                regionFile = regionQueue.poll();

                if (regionFile == null) {
                    LogUtil.info("No regions to process");
                    return;
                }
            }

            LogUtil.info("Processing region {}", regionFile.regionCoord);

            for (NALayer layer : LayerRegistry.getLayers()) {
                NARegionPixelData regionPixels = buildRegionPixels(regionFile, layer.id);
                File out = buildOutputFile(layer.id, regionFile.regionCoord);
                NARegionStorage.get().saveRegionBlocking(regionFile.regionCoord, regionPixels, out);
            }
        }


        private void refreshRegionQueue() {
            List<NARegionFile> regions = platform.getRegionFiles();
            regionQueue.addAll(regions);
            LogUtil.info("Requeued {} regions", regions.size());
        }

        private NARegionPixelData buildRegionPixels(NARegionFile regionFile, int layerId) {
            NARegionPixelData region = new NARegionPixelData();

            for (NACoord chunkCoord : regionFile.iterateExistingChunks()) {
                ChunkWrapper wrapper = platform.getChunk(chunkCoord);
                if (wrapper == null) continue;

                NALayer layer = LayerRegistry.get(layerId);
                NAChunk chunk = layer.builder.build(chunkCoord, wrapper);
                ChunkRenderer.render(region, chunkCoord, chunk, layer.usesBlockLight);
            }

            return region;
        }
    }

    private File buildOutputFile(int layerId, NACoord regionCoord) {
        String levelName = platform.getLevelName();

        File baseDir = NAPaths.getDataPath()
                .resolve("maps")
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

