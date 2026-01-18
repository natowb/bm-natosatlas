package dev.natowb.natosatlas.core.map;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.utils.LogUtil;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Optional;

import static dev.natowb.natosatlas.core.utils.Constants.*;

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
                BufferedImage.TYPE_INT_ARGB
        );

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
        this.pngWriter = writers.hasNext() ? writers.next() : null;
        this.pngParams = pngWriter != null ? pngWriter.getDefaultWriteParam() : null;
    }

    private Path getRegionDirectory() {
        Path dir = NatosAtlas.get().getWorldRegionDataPath().resolve("layer_" + layerId);

        if (!directoryCreated) {
            try {
                Files.createDirectories(dir);
            } catch (IOException ignored) {
            }
            directoryCreated = true;
        }

        return dir;
    }

    public Path getRegionFile(MapRegionCoord coord) {
        return getRegionDirectory().resolve("region_" + coord.getX() + "_" + coord.getZ() + ".png");
    }

    public void saveRegion(MapRegionCoord coord, MapRegion region) {
        Path file = getRegionFile(coord);

        try {
            reusableImage.setRGB(
                    0, 0,
                    BLOCKS_PER_CANVAS_REGION,
                    BLOCKS_PER_CANVAS_REGION,
                    region.getPixels(),
                    0,
                    BLOCKS_PER_CANVAS_REGION
            );

            try (ImageOutputStream out = ImageIO.createImageOutputStream(file.toFile())) {
                pngWriter.setOutput(out);
                pngWriter.write(null, new IIOImage(reusableImage, null, null), pngParams);
            }

        } catch (IOException e) {
            LogUtil.error("RegionStorage", e, "Failed to save region {} to {}", coord, file);
        }
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
}
