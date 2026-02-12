package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.NARegionGenerator;
import dev.natowb.natosatlas.core.data.*;
import dev.natowb.natosatlas.core.util.LogUtil;

import java.io.File;
import java.util.List;

public class ClientMapGenerator {

    public static void generateClientRegions() {
        ClientWorldAccess access = ClientWorldAccess.get();

        if (access == null || access.getWorldInfo() == null) {
            LogUtil.warn("No world loaded, cannot generate regions");
            return;
        }

        File worldDir = NAPaths.getWorldSavePath().toFile();
        List<NARegionFile> regions = access.getRegionFiles(ClientWorldAccess.get().getDimDirectory(worldDir));

        if (regions.isEmpty()) {
            LogUtil.info("No region files found for client world");
            return;
        }

        LogUtil.info("Client queued {} regions for generation", regions.size());
        NARegionGenerator generator = new NARegionGenerator(regions, access::getChunkFromDisk, ClientMapGenerator::buildOutputFile);

        generator.generateAll();
    }

    private static File buildOutputFile(int layerId, NACoord regionCoord) {
        int dim = ClientWorldAccess.get().getWorldInfo().getDimensionId();
        boolean isMultiplayer = ClientWorldAccess.get().getWorldInfo().isMultiplayer();
        File baseDir = NAPaths.getWorldMapStoragePath(layerId, dim, !isMultiplayer).toFile();

        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        return new File(baseDir, "region_" + regionCoord.x + "_" + regionCoord.z + ".png");
    }
}
