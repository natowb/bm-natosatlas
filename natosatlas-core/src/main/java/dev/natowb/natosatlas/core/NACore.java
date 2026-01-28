package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.settings.Settings;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;

import java.nio.file.Path;

public final class NACore {

    private static boolean initialized;
    private static NASession session;


    private NACore() {
    }

    public static boolean isInitialized() {
        return initialized;
    }


    public static NAClient getClient() {
        return (NAClient) session;
    }


    public static void initClient(Path minecraftPath, NAClientPlatform platform) {
        if (initialized) return;
        initialized = true;


        LogUtil.setLoggingLevel(LogUtil.LogLevel.INFO);
        NAPaths.updateBasePaths(minecraftPath);
        Settings.load();

        session = new NAClient(platform);
        LogUtil.info("Successfully initialized client");
    }

    public static void initServer(Path minecraftPath) {
        if (initialized) return;
        initialized = true;

        LogUtil.setLoggingLevel(LogUtil.LogLevel.INFO);
        NAPaths.updateBasePaths(minecraftPath);
        Settings.load();
        LogUtil.info("Successfully initialized server");
    }

    public static void tick() {
        if (!initialized) return;
        session.tick();
    }
}