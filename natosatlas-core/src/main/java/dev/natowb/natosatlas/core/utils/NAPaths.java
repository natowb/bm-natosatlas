package dev.natowb.natosatlas.core.utils;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.data.NAWorldInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NAPaths {
    private static Path mcPath;
    private static Path dataPath;
    private static Path worldDataPath;
    private static Path worldSavePath;


    public static void updateBasePaths(Path _mcPath) {
        mcPath = _mcPath;
        dataPath = ensurePathExists(mcPath.resolve("natosatlas"));
        LogUtil.info("Set mcPath to {}", mcPath);
        LogUtil.info("Set dataPath to {}", dataPath);
    }

    public static void updateWorldPath(String saveName) {
        worldDataPath = ensurePathExists(dataPath.resolve(String.format("worlds/%s/", saveName)));
        worldSavePath = ensurePathExists(mcPath.resolve("saves/" + saveName));
        LogUtil.info("Set worldDataPath to {}", worldDataPath);
        LogUtil.info("Set worldSavePath to {}", worldSavePath);
    }

    public static Path ensurePathExists(Path path) {

        if (Files.exists(path)) {
            return path;
        }

        try {
            Files.createDirectories(path);
            LogUtil.debug("Created directories for {}", path);
        } catch (IOException e) {
            LogUtil.error("Failed to create directories for {}", path);
        }
        return path;
    }


    public static Path getDataPath() {
        return dataPath;
    }

    public static Path getWorldDataPath() {
        return worldDataPath;
    }

    public static Path getWorldMapStoragePath(int layerId) {
        NAWorldInfo info = NatosAtlas.get().platform.worldProvider.getWorldInfo();
        return ensurePathExists(worldDataPath.resolve(String.format("regions/DIM%d/layer_%d", info.worldDimension, layerId)));
    }

    public static Path getWorldSavePath() {
        return worldSavePath;
    }
}
