package dev.natowb.natosatlas.core.storage;

import dev.natowb.natosatlas.core.glue.INacFileProvider;
import dev.natowb.natosatlas.core.models.NacRegionData;
import dev.natowb.natosatlas.core.utils.NacConstants;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class NacPngRegionStorage implements INacRegionStorage {

    private final INacFileProvider accessor;

    private final BufferedImage bufferedImage;
    private final int[] pixelBuffer;

    public NacPngRegionStorage(INacFileProvider accessor) {
        this.accessor = accessor;

        int size = NacConstants.BLOCKS_PER_CANVAS_REGION;
        this.bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = new int[size * size];
    }

    @Override
    public Path getRegionFile(int rx, int rz) {
        return accessor.getRegionDirectory().resolve("region_" + rx + "_" + rz + ".png");
    }

    @Override
    public void saveRegion(int rx, int rz, NacRegionData region) {
        Path file = getRegionFile(rx, rz);
        int size = NacConstants.BLOCKS_PER_CANVAS_REGION;

        bufferedImage.setRGB(0, 0, size, size, region.getPixels(), 0, size);

        try {
            ImageIO.write(bufferedImage, "png", file.toFile());
        } catch (IOException e) {
            System.err.println("Failed to save PNG region: " + file);
        }
    }

    @Override
    public Optional<NacRegionData> loadRegion(int rx, int rz) {
        Path file = getRegionFile(rx, rz);
        if (!Files.exists(file)) return Optional.empty();

        try (ImageInputStream stream = ImageIO.createImageInputStream(file.toFile())) {

            if (stream == null) return Optional.empty();

            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) return Optional.empty();

            ImageReader reader = readers.next();
            reader.setInput(stream, true);

            BufferedImage img = reader.read(0);
            int size = NacConstants.BLOCKS_PER_CANVAS_REGION;

            if (img.getWidth() != size || img.getHeight() != size) return Optional.empty();

            img.getRGB(0, 0, size, size, pixelBuffer, 0, size);

            NacRegionData region = new NacRegionData();
            System.arraycopy(pixelBuffer, 0, region.getPixels(), 0, pixelBuffer.length);

            return Optional.of(region);

        } catch (IOException e) {
            System.err.println("Failed to load PNG region: " + file);
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, NacRegionData> loadAllRegions() {
        Map<Long, NacRegionData> result = new HashMap<>();
        Path dir = accessor.getRegionDirectory();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "region_*.png")) {
            for (Path file : stream) {
                String name = file.getFileName().toString();

                int first = name.indexOf('_');
                int second = name.indexOf('_', first + 1);
                int dot = name.indexOf('.');

                if (first < 0 || second < 0 || dot < 0) continue;

                try {
                    int rx = Integer.parseInt(name.substring(first + 1, second));
                    int rz = Integer.parseInt(name.substring(second + 1, dot));

                    loadRegion(rx, rz).ifPresent(region -> {
                        long key = (((long) rx) << 32) ^ (rz & 0xffffffffL);
                        result.put(key, region);
                    });

                } catch (NumberFormatException ignored) {
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to list PNG regions: " + dir);
        }

        return result;
    }
}
