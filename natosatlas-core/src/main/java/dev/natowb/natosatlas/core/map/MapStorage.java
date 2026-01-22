package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.tasks.MapSaveWorker;
import dev.natowb.natosatlas.core.utils.LogUtil;
import dev.natowb.natosatlas.core.utils.NAPaths;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import static dev.natowb.natosatlas.core.utils.Constants.BLOCKS_PER_CANVAS_REGION;

public class MapStorage {

    private final BufferedImage reusableImage;
    private final ImageWriter pngWriter;
    private final ImageWriteParam pngParams;

    public MapStorage() {
        this.reusableImage = new BufferedImage(BLOCKS_PER_CANVAS_REGION, BLOCKS_PER_CANVAS_REGION, BufferedImage.TYPE_INT_ARGB);
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


    public File getRegionPngFile(int layerId, NACoord regionCoord) {
        return NAPaths.getWorldMapStoragePath(layerId).resolve("region_" + regionCoord.x + "_" + regionCoord.z + ".png").toFile();
    }

    public void saveRegion(int layerId, NACoord coord, MapRegion region) {
        MapSaveWorker.enqueue(this, coord, region, getRegionPngFile(layerId, coord));
    }


    public Optional<MapRegion> loadRegion(int layerId, NACoord coord) {
        File file = getRegionPngFile(layerId, coord);

        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                LogUtil.warn("Invalid PNG file for region {} at {}", coord, file);
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
            LogUtil.error("Failed to load region {} from {}", coord, file);
            return Optional.empty();
        }
    }


    public void saveRegionBlocking(NACoord regionCoord, MapRegion region, File file) {
        try {
            reusableImage.setRGB(
                    0, 0,
                    BLOCKS_PER_CANVAS_REGION,
                    BLOCKS_PER_CANVAS_REGION,
                    region.getPixels(),
                    0,
                    BLOCKS_PER_CANVAS_REGION
            );

            try (ImageOutputStream out = ImageIO.createImageOutputStream(file)) {
                pngWriter.setOutput(out);
                pngWriter.write(null, new IIOImage(reusableImage, null, null), pngParams);
            }
        } catch (IOException e) {
            LogUtil.error("Failed to save region {} to {}", regionCoord, file);
        }
    }
}
