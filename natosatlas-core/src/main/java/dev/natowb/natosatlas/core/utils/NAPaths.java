package dev.natowb.natosatlas.core.utils;

import dev.natowb.natosatlas.core.NatosAtlas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class NAPaths {
    private static Path mcPath;
    private static Path dataPath;
    private static Path worldDataPath;
    private static Path worldSavePath;
    private static String worldSaveName;


    public static String getWorldSaveName() {
        return worldSaveName;
    }

    public static Path getMinecraftPath() {
        return mcPath;
    }

    public static void updateBasePaths(Path _mcPath) {
        mcPath = _mcPath;
        dataPath = ensurePathExists(mcPath.resolve("natosatlas"));
        LogUtil.info("Set mcPath to {}", mcPath);
        LogUtil.info("Set dataPath to {}", dataPath);
    }

    public static void updateWorldPath(String saveName) {
        worldSaveName = saveName;
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
        return ensurePathExists(worldDataPath.resolve(String.format("regions/DIM%d/layer_%d", NatosAtlas.get().getCurrentWorld().getDimensionId(), layerId)));
    }

    public static Path getWorldSavePath() {
        return worldSavePath;
    }
}
