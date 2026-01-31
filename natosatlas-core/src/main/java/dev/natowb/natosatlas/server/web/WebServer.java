package dev.natowb.natosatlas.server.web;

import com.sun.net.httpserver.HttpServer;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.server.ServerPlatform;
import dev.natowb.natosatlas.server.ServerConfig;
import dev.natowb.natosatlas.server.web.routes.AssetsRoute;
import dev.natowb.natosatlas.server.web.routes.IndexRoute;
import dev.natowb.natosatlas.server.web.routes.PlayerRoute;
import dev.natowb.natosatlas.server.web.routes.TileRoute;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public class WebServer {


    public void start(ServerPlatform platform) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(ServerConfig.webHost, ServerConfig.webPort), 0);
            server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(
                    8,
                    new ThreadFactory() {
                        private int count = 1;

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r);
                            t.setName("NAServer-Web" + count++);
                            t.setDaemon(true);
                            return t;
                        }
                    }
            ));

            IndexRoute.register(server);
            AssetsRoute.register(server);
            if(ServerConfig.showPlayers) {
                PlayerRoute.register(server, platform);
            }
            TileRoute.register(server);

            server.start();
            LogUtil.info("WebServer running on port {}", ServerConfig.webPort);

        } catch (IOException e) {
            LogUtil.error("Failed to start WebServer", e);
        }
    }
}
