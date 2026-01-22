package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.tasks.MapSaveWorker;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_CANVAS_REGION;

public class MapExporter {


    public static void exportAllLayers() {
        for (int i = 0; i < NatosAtlas.get().layers.getLayers().size(); i++) {
            MapExporter.exportMapLayer(i);
        }
    }

    public static void exportMapLayer(int layerId) {

        File outputFile = new File(NAPaths.getWorldDataPath().toFile(), String.format("exported_map_layer_%d.png", layerId));

        MapSaveWorker.stop();

        Path dir = NAPaths.getWorldMapStoragePath(layerId);

        if (!Files.exists(dir)) {
            LogUtil.error("Region directory does not exist: {}", dir);
            return;
        }

        File[] files = dir.toFile().listFiles((d, name) -> name.startsWith("region_") && name.endsWith(".png"));
        if (files == null || files.length == 0) {
            LogUtil.warn("No region PNGs found in {}", dir);
            return;
        }

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (File f : files) {
            String name = f.getName();
            try {
                String[] parts = name.substring(7, name.length() - 4).split("_");
                int rx = Integer.parseInt(parts[0]);
                int rz = Integer.parseInt(parts[1]);

                minX = Math.min(minX, rx);
                minZ = Math.min(minZ, rz);
                maxX = Math.max(maxX, rx);
                maxZ = Math.max(maxZ, rz);

            } catch (Exception e) {
                LogUtil.warn("Skipping invalid region filename: {}", name);
            }
        }

        int regionSize = BLOCKS_PER_CANVAS_REGION;

        int regionCountX = (maxX - minX + 1);
        int regionCountZ = (maxZ - minZ + 1);

        int finalW = regionCountX * regionSize;
        int finalH = regionCountZ * regionSize;

        LogUtil.info("Stitching {} regions into {}x{} pixels", files.length, finalW, finalH);

        BufferedImage stitched = new BufferedImage(finalW, finalH, BufferedImage.TYPE_INT_ARGB);

        for (File f : files) {
            String name = f.getName();
            try {
                String[] parts = name.substring(7, name.length() - 4).split("_");
                int rx = Integer.parseInt(parts[0]);
                int rz = Integer.parseInt(parts[1]);

                BufferedImage regionImg = ImageIO.read(f);
                if (regionImg == null) continue;

                int px = (rx - minX) * regionSize;
                int pz = (rz - minZ) * regionSize;

                stitched.getGraphics().drawImage(regionImg, px, pz, null);

            } catch (Exception e) {
                LogUtil.warn("Failed to stitch region file {}", name);
            }
        }

        try {
            ImageIO.write(stitched, "png", outputFile);
            LogUtil.info("Saved stitched map to {}", outputFile);
        } catch (IOException e) {
            LogUtil.error("Failed to save stitched map to {}", outputFile);
        }


        MapSaveWorker.start();
    }
}
