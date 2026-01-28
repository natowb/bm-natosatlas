package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.NAClientPlatform;
import dev.natowb.natosatlas.server.NAServer;
import dev.natowb.natosatlas.server.NAServerPlatform;

import java.nio.file.Path;

public final class NACore {


    private static boolean initialized;
    private static NAClientSession clientSession;

    private NACore() {
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isInitialized() {
        return initialized;
    }


    public static void initClient(Path minecraftPath, NAClientPlatform platform) {
        if (initialized) return;
        initialized = true;
        NAPaths.setBasePaths(minecraftPath);

        clientSession = new NAClient(platform);
    }

    public static void initServer(Path minecraftPath, NAServerPlatform platform) {
        if (initialized) return;
        initialized = true;
        NAPaths.setBasePaths(minecraftPath);

        NAServer server = new NAServer(platform);
        server.startServer();
    }


    public static void tick() {
        if (!initialized) return;
        if (clientSession == null) return;
        clientSession.tick();
    }
}