package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.Profiler;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_CANVAS_REGION;

public class MapStorage {

    private final int layerId;
    private final BufferedImage reusableImage;
    private final ImageWriter pngWriter;
    private final ImageWriteParam pngParams;

    private boolean directoryCreated = false;

    public MapStorage(int layerId) {
        this.layerId = layerId;

        this.reusableImage = new BufferedImage(
                BLOCKS_PER_CANVAS_REGION,
                BLOCKS_PER_CANVAS_REGION,
                BufferedImage.TYPE_INT_RGB
        );

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
        this.pngWriter = writers.hasNext() ? writers.next() : null;

        if (pngWriter == null) {
            throw new IllegalStateException("No PNG writer available");
        }

        this.pngParams = pngWriter.getDefaultWriteParam();

        if (pngParams.canWriteCompressed()) {
            pngParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            pngParams.setCompressionQuality(1.0f);
        }
    }

    private Path getRegionDirectory() {
        Path dir = NatosAtlas.get().getWorldRegionDataPath().resolve("layer_" + layerId);

        if (!directoryCreated) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                LogUtil.error("RegionStorage", e, "Failed to create region directory {}", dir);
            }
            directoryCreated = true;
        }

        return dir;
    }

    public Path getRegionFile(MapRegionCoord coord) {
        return getRegionDirectory().resolve("region_" + coord.getX() + "_" + coord.getZ() + ".png");
    }

    public void saveRegion(MapRegionCoord coord, MapRegion region) {
        RegionSaveWorker.enqueue(this, coord, region);
    }


    public Optional<MapRegion> loadRegion(MapRegionCoord coord) {
        Path file = getRegionFile(coord);

        if (!Files.exists(file)) {
            return Optional.empty();
        }

        try {
            BufferedImage img = ImageIO.read(file.toFile());
            if (img == null) {
                LogUtil.warn("RegionStorage", "Invalid PNG file for region {} at {}", coord, file);
                return Optional.empty();
            }

            MapRegion region = new MapRegion();
            img.getRGB(
                    0, 0,
                    BLOCKS_PER_CANVAS_REGION,
                    BLOCKS_PER_CANVAS_REGION,
                    region.getPixels(),
                    0,
                    BLOCKS_PER_CANVAS_REGION
            );

            return Optional.of(region);

        } catch (IOException e) {
            LogUtil.error("RegionStorage", e, "Failed to load region {} from {}", coord, file);
            return Optional.empty();
        }
    }

    public void exportFullMap(Path outputFile) {
        Path regionDir = getRegionDirectory();

        try {
            Files.createDirectories(outputFile.getParent());
        } catch (IOException ignored) {
        }

        File[] regionFiles = regionDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));
        if (regionFiles == null || regionFiles.length == 0) {
            LogUtil.warn("MapExport", "No region PNGs found to export.");
            return;
        }

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (File f : regionFiles) {
            String name = f.getName();
            String[] parts = name.substring(7, name.length() - 4).split("_");

            int rx = Integer.parseInt(parts[0]);
            int rz = Integer.parseInt(parts[1]);

            minX = Math.min(minX, rx);
            maxX = Math.max(maxX, rx);
            minZ = Math.min(minZ, rz);
            maxZ = Math.max(maxZ, rz);
        }

        int regionsX = (maxX - minX) + 1;
        int regionsZ = (maxZ - minZ) + 1;

        int regionSize = BLOCKS_PER_CANVAS_REGION;

        int fullWidth = regionsX * regionSize;
        int fullHeight = regionsZ * regionSize;

        LogUtil.info("MapExport", "Exporting full map: {}x{} regions -> {}x{} px",
                regionsX, regionsZ, fullWidth, fullHeight);

        BufferedImage full = new BufferedImage(fullWidth, fullHeight, BufferedImage.TYPE_INT_ARGB);

        for (File f : regionFiles) {
            String name = f.getName();
            String[] parts = name.substring(7, name.length() - 4).split("_");

            int rx = Integer.parseInt(parts[0]);
            int rz = Integer.parseInt(parts[1]);

            int px = (rx - minX) * regionSize;
            int pz = (rz - minZ) * regionSize;

            try {
                BufferedImage regionImg = ImageIO.read(f);
                if (regionImg == null) {
                    LogUtil.warn("MapExport", "Skipping invalid region file {}", f);
                    continue;
                }

                full.getRaster().setRect(px, pz, regionImg.getRaster());

            } catch (IOException e) {
                LogUtil.error("MapExport", e, "Failed to read region {}", f);
            }
        }

        try {
            ImageIO.write(full, "png", outputFile.toFile());
            LogUtil.info("MapExport", "Full map exported to {}", outputFile);
        } catch (IOException e) {
            LogUtil.error("MapExport", e, "Failed to save full map to {}", outputFile);
        }
    }

    void saveRegionBlocking(MapRegionCoord coord, MapRegion region) {
        Profiler p = Profiler.start("saveRegion (" + coord.getX() + "," + coord.getZ() + ")");
        Path file = getRegionFile(coord);

        try {
            p.mark("setRGB");
            reusableImage.setRGB(
                    0, 0,
                    BLOCKS_PER_CANVAS_REGION,
                    BLOCKS_PER_CANVAS_REGION,
                    region.getPixels(),
                    0,
                    BLOCKS_PER_CANVAS_REGION
            );

            p.mark("open stream");
            try (ImageOutputStream out = ImageIO.createImageOutputStream(file.toFile())) {
                pngWriter.setOutput(out);
                p.mark("pngWriter.write");
                pngWriter.write(null, new IIOImage(reusableImage, null, null), pngParams);
                p.mark("write complete");
            }

        } catch (IOException e) {
            LogUtil.error("RegionStorage", e, "Failed to save region {} to {}", coord, file);
        }

        p.end();
    }
}
