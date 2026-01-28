package dev.natowb.natosatlas.core.chunk;

import dev.natowb.natosatlas.core.cache.NARegionPixelCache;
import dev.natowb.natosatlas.core.io.NARegionStorage;
import dev.natowb.natosatlas.core.io.LogUtil;

public class ChunkBuilderOld {


    public static void rebuildExistingChunks(NARegionStorage storage, NARegionPixelCache cache) {
        LogUtil.error("Nato has disabled this functionality and forgot to bring it back..... hes an idiot");

//        List<NARegionFile> regions = NACore.getClient().getPlatform().world.getRegionFiles();
//
//        if (regions.isEmpty()) {
//            LogUtil.info("No region metadata found.");
//            return;
//        }
//
//        SaveScheduler.stop();
//
//        LogUtil.info("Generating map data for all existing regions (this may take a while...)");
//
//        int index = 0;
//        int total = regions.size();
//
//        for (NARegionFile naRegion : regions) {
//            index++;
//
//            NACoord regionCoord = naRegion.regionCoord;
//            boolean success = false;
//
//            try {
//                NARegionPixelData[] layers = new NARegionPixelData[LayerRegistry.getLayers().size()];
//                for (int i = 0; i < layers.length; i++) {
//                    layers[i] = new NARegionPixelData();
//                }
//
//                for (NACoord chunkCoord : naRegion.iterateExistingChunks()) {
//                    int layerIndex = 0;
//                    for (NALayer layer : LayerRegistry.getLayers()) {
//                        layer.renderer.applyChunkToRegion(layers[layerIndex], chunkCoord, layer.usesBlockLight, true);
//                        layerIndex++;
//                    }
//                }
//
//                for (int layerId = 0; layerId < layers.length; layerId++) {
//                    File out = storage.getRegionPngFile(layerId, regionCoord);
//                    storage.saveRegionBlocking(regionCoord, layers[layerId], out);
//                }
//
//                success = true;
//
//            } catch (Exception ignored) {
//            }
//
//            if (success) {
//                LogUtil.info("[{}/{}] Successfully generated region r({}, {})",
//                        index, total, regionCoord.x, regionCoord.z);
//            } else {
//                LogUtil.info("[{}/{}] Failed to generate region r({}, {})",
//                        index, total, regionCoord.x, regionCoord.z);
//            }
//        }
//
//        LogUtil.info("Full region generation complete.");
//        cache.clear();
//        SaveScheduler.start();
    }
}
