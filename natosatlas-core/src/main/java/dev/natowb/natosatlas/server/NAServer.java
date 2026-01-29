package dev.natowb.natosatlas.server;

import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.server.web.WebServer;

import java.nio.file.Path;

public class NAServer {

    private boolean started;
    private final NAServerPlatform platform;
    private RegionFileWatcher overworldWatcher;
    private RegionFileWatcher netherWatcher;


    public NAServer(NAServerPlatform platform) {
        NAPaths.setWorldPaths(platform.getLevelName(), true);
        this.platform = platform;
        this.overworldWatcher = new RegionFileWatcher(platform);
        this.netherWatcher = new RegionFileWatcher(platform);
    }

    public synchronized void startServer() {
        if (!started) {
            started = true;
            Thread t = new Thread(this::start, "NAServer-Main");
            t.setDaemon(true);
            t.start();
        }
    }

    private void start() {
        ServerConfig.loadConfig(NAPaths.getDataPath().resolve("config.txt").toFile());
        LogUtil.setLoggingLevel(ServerConfig.logLevel);
        startWebServer();
        startRegionFileWatcher();
    }

    private void startWebServer() {
        WebServer webServer = new WebServer();
        Thread t = new Thread(() -> webServer.start(platform), "NAServer-Web");
        t.setDaemon(true);
        t.start();
    }


    private void startRegionFileWatcher() {
        overworldWatcher.start(0);
        netherWatcher.start(-1);
    }

}