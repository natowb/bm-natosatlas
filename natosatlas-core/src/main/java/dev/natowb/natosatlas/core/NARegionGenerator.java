package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.chunk.ChunkRenderer;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.storage.NARegionStorage;
import dev.natowb.natosatlas.core.util.LogUtil;

import java.io.File;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class NARegionGenerator {

    private final List<NARegionFile> regions;
    private final Function<NACoord, ChunkWrapper> chunkProvider;
    private final BiFunction<Integer, NACoord, File> outputFileProvider;

    public NARegionGenerator(
            List<NARegionFile> regions,
            Function<NACoord, ChunkWrapper> chunkProvider,
            BiFunction<Integer, NACoord, File> outputFileProvider
    ) {
        this.regions = regions;
        this.chunkProvider = chunkProvider;
        this.outputFileProvider = outputFileProvider;
    }

    public void generateAll() {
        for (NARegionFile regionFile : regions) {
            generateRegion(regionFile);
        }
    }

    private void generateRegion(NARegionFile regionFile) {
        LogUtil.info("Processing region {}", regionFile.regionCoord);

        for (NALayer layer : LayerRegistry.getLayers()) {
            NARegionPixelData regionPixels = buildRegionPixels(regionFile, layer.id);
            File out = outputFileProvider.apply(layer.id, regionFile.regionCoord);
            NARegionStorage.get().saveRegionBlocking(regionFile.regionCoord, regionPixels, out);
        }
    }

    private NARegionPixelData buildRegionPixels(NARegionFile regionFile, int layerId) {
        NARegionPixelData region = new NARegionPixelData();
        NALayer layer = LayerRegistry.get(layerId);

        for (NACoord chunkCoord : regionFile.iterateExistingChunks()) {
            ChunkWrapper wrapper = chunkProvider.apply(chunkCoord);
            if (wrapper == null) continue;

            NAChunk chunk = layer.builder.build(chunkCoord, wrapper);
            ChunkRenderer.render(region, chunkCoord, chunk, layer.usesBlockLight);
        }
        return region;
    }
}
