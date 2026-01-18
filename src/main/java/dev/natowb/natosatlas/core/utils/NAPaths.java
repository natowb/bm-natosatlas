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
        LogUtil.debug("NAPaths", "Setup base paths: nmcPath={} dataPath={}", mcPath, dataPath);
        dataPath = ensurePathExists(mcPath.resolve("natosatlas"));

    }

    public static void updateWorldPath(NAWorldInfo info) {
        worldDataPath = ensurePathExists(dataPath.resolve(String.format("worlds/%s/", info.worldName)));
        worldSavePath = ensurePathExists(mcPath.resolve("saves/" + info.worldName));
        LogUtil.info("NAPaths", "Setup world paths, worldDataPath={}, worldSavePath={}", worldDataPath, worldSavePath);

    }

    public static Path ensurePathExists(Path path) {

        if (Files.exists(path)) {
            return path;
        }

        try {
            Files.createDirectories(path);
            LogUtil.debug("NAPaths", "ensured path {}", path);
        } catch (IOException e) {
            LogUtil.error("NAPaths", e, "Failed to ensure path {}", path);
        }
        return path;
    }


    public static Path getDataPath() {
        return dataPath;
    }

    public static Path geWorldDataPath() {
        return worldDataPath;
    }

    public static Path getWorldMapStoragePath(int layerId) {
        NAWorldInfo info = NatosAtlas.get().worldInfo;
        return ensurePathExists(worldDataPath.resolve(String.format("regions/DIM%d/layer_%d", info.worldDimension, layerId)));
    }

    public static Path getWorldSavePath() {
        return worldSavePath;
    }
}
