package dev.natowb.natosatlas.core.storage;

import dev.natowb.natosatlas.core.glue.INacFileProvider;
import dev.natowb.natosatlas.core.models.NacRegionData;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NacBinRegionStorage implements INacRegionStorage {

    private final INacFileProvider accessor;

    public NacBinRegionStorage(INacFileProvider accessor) {
        this.accessor = accessor;
    }

    @Override
    public Path getRegionFile(int rx, int rz) {
        return accessor.getRegionDirectory().resolve("region_" + rx + "_" + rz + ".bin");
    }

    @Override
    public void saveRegion(int rx, int rz, NacRegionData region) {
        Path file = getRegionFile(rx, rz);

        int[] pixels = region.getPixels();
        byte[] buffer = new byte[pixels.length * 4];

        // TODO: remember idiot this is argb format. not rgba
        for (int i = 0, p = 0; i < pixels.length; i++) {
            int v = pixels[i];
            buffer[p++] = (byte) (v >>> 24);
            buffer[p++] = (byte) (v >>> 16);
            buffer[p++] = (byte) (v >>> 8);
            buffer[p++] = (byte) v;
        }

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
            out.write(buffer);
        } catch (IOException e) {
            System.err.println("Failed to save BIN region: " + file);
        }
    }

    @Override
    public Optional<NacRegionData> loadRegion(int rx, int rz) {
        Path file = getRegionFile(rx, rz);
        if (!Files.exists(file)) return Optional.empty();

        try (InputStream in = new BufferedInputStream(Files.newInputStream(file))) {

            NacRegionData region = new NacRegionData();
            int[] pixels = region.getPixels();
            byte[] buffer = new byte[pixels.length * 4];

            int read = in.read(buffer);
            if (read != buffer.length) return Optional.empty();

            // TODO: remember idiot this is argb format. not rgba
            for (int i = 0, p = 0; i < pixels.length; i++) {
                int b1 = (buffer[p++] & 0xFF) << 24;
                int b2 = (buffer[p++] & 0xFF) << 16;
                int b3 = (buffer[p++] & 0xFF) << 8;
                int b4 = (buffer[p++] & 0xFF);
                pixels[i] = b1 | b2 | b3 | b4;
            }

            return Optional.of(region);

        } catch (IOException e) {
            System.err.println("Failed to load BIN region: " + file);
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, NacRegionData> loadAllRegions() {
        Map<Long, NacRegionData> result = new HashMap<>();
        Path dir = accessor.getRegionDirectory();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "region_*.bin")) {
            for (Path file : stream) {
                String name = file.getFileName().toString();
                String[] parts = name.split("_");
                if (parts.length < 3) continue;

                try {
                    int rx = Integer.parseInt(parts[1]);
                    int rz = Integer.parseInt(parts[2].replace(".bin", ""));

                    loadRegion(rx, rz).ifPresent(region -> {
                        long key = (((long) rx) << 32) ^ (rz & 0xffffffffL);
                        result.put(key, region);
                    });

                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            System.err.println("Failed to list BIN regions: " + dir);
        }

        return result;
    }
}
