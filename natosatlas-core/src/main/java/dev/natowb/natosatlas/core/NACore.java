package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.ClientPlatform;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.server.NAServer;
import dev.natowb.natosatlas.server.ServerPlatform;

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

    public static void init(Path mcPath) {
        if (initialized) return;
        initialized = true;
        NAPaths.setBasePaths(mcPath);
    }

    public static void startClient(ClientPlatform platform) {
        if (!initialized) {
            throw new IllegalStateException("Tried to start Natos Atlas client before NACore was initialized");
        }
        clientSession = new NAClient(platform);
    }

    public static void startServer(ServerPlatform platform) {
        if (!initialized) {
            throw new IllegalStateException("Tried to start Natos Atlas server before NACore was initialized");
        }
//        NAServer.getInstance(platform).startServer();

        LogUtil.error("NatosAtlas Serverside is currently disabled and or unimplemented");

    }


    public static void tick() {
        if (clientSession == null) return;
        clientSession.tick();
    }
}