package dev.natowb.natosatlas.core.glue;

import dev.natowb.natosatlas.core.models.NacScaleInfo;
import dev.natowb.natosatlas.core.models.NacWorldInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class NacPlatformAPI {

    private static final String DATA_FOLDER = "natosatlas";

    private static NacPlatformAPI instance;
    public static void setInstance(NacPlatformAPI impl) {
        instance = impl;
    }
    public static NacPlatformAPI get() {
        return instance;
    }

    public final INacPainter painter;
    public final INacEntityProvider entityProvider;
    public final INacChunkProvider chunkProvider;

    public NacPlatformAPI(INacPainter painter, INacEntityProvider entityProvider, INacChunkProvider chunkProvider) {
        this.painter = painter;
        this.entityProvider = entityProvider;
        this.chunkProvider = chunkProvider;
    }

    public abstract NacWorldInfo getCurrentWorldInfo();
    public abstract NacScaleInfo getScaleInfo();

    public abstract Path getMinecraftDirectory();


    public File getDataDirectory() {
        Path configPath = getMinecraftDirectory().resolve(DATA_FOLDER);

        try {
            Files.createDirectories(configPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory: " + configPath, e);
        }

        return configPath.toFile();
    }


    public File getRegionDataDirectory() {
        Path regionDir = getWorldDirectory().toPath().resolve("regions");
        try {
            Files.createDirectories(regionDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create regions directory: " + regionDir, e);
        }

        return regionDir.toFile();
    }

    public File getWorldDirectory() {
        Path dataPath = getDataDirectory().toPath().resolve("worlds/" + getCurrentWorldInfo().name);
        try {
            Files.createDirectories(dataPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create world directory: " + dataPath, e);
        }
        return dataPath.toFile();
    }


}
