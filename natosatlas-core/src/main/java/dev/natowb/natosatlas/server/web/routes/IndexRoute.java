package dev.natowb.natosatlas.server.web.routes;

import com.sun.net.httpserver.HttpServer;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.server.web.IOUtil;

import java.io.InputStream;

public class IndexRoute {
    public static void register(HttpServer server) {
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (!path.equals("/") && !path.equals("/index.html")) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            InputStream stream = IOUtil.getWebInputStream();
            if (stream == null) {
                LogUtil.error("index.html not found in resources");
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
                return;
            }

            try {

                byte[] bytes = IOUtil.readAll(stream);

                exchange.getResponseHeaders().add("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } catch (Exception e) {
                LogUtil.error("Error serving index.html", e);
                exchange.sendResponseHeaders(500, -1);
            } finally {
                if (stream != null) try {
                    stream.close();
                } catch (Exception ignored) {
                }
                exchange.close();
            }
        });
    }

}
