package dev.natowb.natosatlas.core.regions;

import dev.natowb.natosatlas.core.NacSettings;
import dev.natowb.natosatlas.core.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacChunk;
import dev.natowb.natosatlas.core.models.NacRegionData;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class NacRegionStorage {

    private static final byte VERSION = 1;

    private void debug(String msg) {
        if (NacSettings.DEBUG_INFO.getValue()) {
            System.out.println("[MapStorage] " + msg);
        }
    }

    public Path getRegionFile(int rx, int rz) {
        return NacPlatformAPI.get()
                .getRegionDataDirectory()
                .toPath()
                .resolve("region_" + rx + "_" + rz + ".bin");
    }

    public void saveRegion(int rx, int rz, NacRegionData region) {
        Path file = getRegionFile(rx, rz);

        debug("Saving region (" + rx + ", " + rz + ") → " + file);

        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeByte(VERSION);

            int count = 0;
            for (int cx = 0; cx < 32; cx++) {
                for (int cz = 0; cz < 32; cz++) {
                    if (region.getChunk(cx, cz) != null) count++;
                }
            }

            debug(" → Chunk count: " + count);

            out.writeInt(count);

            for (int cx = 0; cx < 32; cx++) {
                for (int cz = 0; cz < 32; cz++) {

                    NacChunk chunk = region.getChunk(cx, cz);
                    if (chunk == null) continue;

                    out.writeByte(cx);
                    out.writeByte(cz);
                    out.writeShort(0);

                    writeIntArray(out, chunk.blockIds);
                    writeIntArray(out, chunk.heights);
                    writeIntArray(out, chunk.waterDepths);
                }
            }

            debug(" → Region saved successfully.");

        } catch (IOException e) {
            System.err.println("Failed to save region: " + file);
            e.printStackTrace();
        }
    }

    private void writeIntArray(DataOutputStream out, int[] arr) throws IOException {
        for (int v : arr) out.writeInt(v);
    }

    public Optional<NacRegionData> loadRegion(int rx, int rz) {
        Path file = getRegionFile(rx, rz);

        if (!Files.exists(file)) {
            debug("Region file missing for (" + rx + ", " + rz + "): " + file);
            return Optional.empty();
        }

        debug("Loading region (" + rx + ", " + rz + ") ← " + file);

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {

            byte version = in.readByte();
            if (version != VERSION) {
                System.err.println("Unsupported region version: " + version + " in " + file);
                return Optional.empty();
            }

            int chunkCount = in.readInt();
            debug(" → Chunk count: " + chunkCount);

            NacRegionData region = new NacRegionData(rx, rz);

            for (int i = 0; i < chunkCount; i++) {
                int cx = in.readByte() & 0xFF;
                int cz = in.readByte() & 0xFF;
                in.readShort();

                NacChunk chunk = new NacChunk();
                readIntArray(in, chunk.blockIds);
                readIntArray(in, chunk.heights);
                readIntArray(in, chunk.waterDepths);

                region.writeChunk(cx, cz, chunk);
            }

            debug(" → Region loaded successfully.");

            return Optional.of(region);

        } catch (IOException e) {
            System.err.println("Failed to load region: " + file);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void readIntArray(DataInputStream in, int[] arr) throws IOException {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.readInt();
        }
    }

    public Map<Long, NacRegionData> loadAllRegions() {
        Map<Long, NacRegionData> result = new HashMap<>();
        Path dir = NacPlatformAPI.get().getRegionDataDirectory().toPath();

        debug("Scanning region directory: " + dir);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "region_*.bin")) {
            for (Path file : stream) {

                String name = file.getFileName().toString();
                String[] parts = name.split("_");
                if (parts.length < 3) continue;

                try {
                    int rx = Integer.parseInt(parts[1]);
                    int rz = Integer.parseInt(parts[2].replace(".bin", ""));

                    debug(" → Found region file: " + name);

                    loadRegion(rx, rz).ifPresent(region -> {
                        long key = (((long) rx) << 32) ^ (rz & 0xffffffffL);
                        result.put(key, region);
                    });

                } catch (NumberFormatException ignored) {
                    debug(" → Skipping invalid region filename: " + name);
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to list regions: " + dir);
            e.printStackTrace();
        }

        debug("Loaded " + result.size() + " regions from disk.");

        return result;
    }
}
