package dev.natowb.natosatlas.server.web.routes;

import com.sun.net.httpserver.HttpServer;
import dev.natowb.natosatlas.server.web.IOUtil;

import java.io.InputStream;

public class AssetsRoute {


    public static void register(HttpServer server) {
        registerStaticFile(server, "/map.css", "text/css", "/assets/natosatlas/www/map.css");
        registerStaticFile(server, "/map.js", "application/javascript", "/assets/natosatlas/www/map.js");
    }


    private static void registerStaticFile(HttpServer server, String route, String contentType, String resourcePath) {
        server.createContext(route, exchange -> {
            InputStream stream = AssetsRoute.class.getResourceAsStream(resourcePath);

            if (stream == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            byte[] bytes = null;
            try {
                bytes = IOUtil.readAll(stream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);

            stream.close();
            exchange.close();
        });
    }

}
