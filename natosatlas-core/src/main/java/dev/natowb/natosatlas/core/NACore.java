package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.NAClientPlatform;
import dev.natowb.natosatlas.server.NAServer;
import dev.natowb.natosatlas.server.NAServerPlatform;
import dev.natowb.natosatlas.core.util.LogUtil;

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
        if (initialized) return;
        initialized = true;

        LogUtil.setLoggingLevel(LogUtil.LogLevel.INFO);
        session = new NAClient(minecraftPath, platform);
        LogUtil.info("Successfully initialized client");
    }

    public static void initServer(Path minecraftPath, NAServerPlatform platform) {
        if (initialized) return;
        initialized = true;

        session = new NAServer(minecraftPath, platform);
        LogUtil.info("Successfully initialized server");
    }


    public static void tick() {
        if (!initialized) return;
        session.tick();
    }
}