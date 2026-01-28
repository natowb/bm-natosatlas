package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.NAClientPlatform;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.io.NAPaths;
import dev.natowb.natosatlas.server.NAServer;
import dev.natowb.natosatlas.server.NAServerPlatform;

import java.nio.file.Path;

public final class NACore {

    private static boolean initialized;
    private static NASession session;


    private NACore() {
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isInitialized() {
        return initialized;
    }

    public static void initClient(Path minecraftPath, NAClientPlatform platform) {
        if (!init(minecraftPath)) return;

        session = new NAClient(platform);
        LogUtil.info("Successfully initialized client");
    }

    public static void initServer(Path minecraftPath, NAServerPlatform platform) {
        if (!init(minecraftPath)) return;
        session = new NAServer(platform);
        LogUtil.info("Successfully initialized server");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean init(Path minecraftPath) {
        if (initialized) return false;
        initialized = true;

        LogUtil.setLoggingLevel(LogUtil.LogLevel.INFO);
        NAPaths.updateBasePaths(minecraftPath);
        return true;
    }

    public static void tick() {
        if (!initialized) return;
        session.tick();
    }
}