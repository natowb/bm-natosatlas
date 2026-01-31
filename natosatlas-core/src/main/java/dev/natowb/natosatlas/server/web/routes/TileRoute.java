package dev.natowb.natosatlas.server.web.routes;

import com.sun.net.httpserver.HttpServer;
import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.util.LogUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public class TileRoute {

    public static void register(HttpServer server) {
        server.createContext("/tiles", exchange -> {
            String path = exchange.getRequestURI().getPath();

            try {
                String[] parts = path.split("/");

                // NOTE: this is the format of the parts
                //      /tiles/{dim}/{layerId}/{rx}/{rz}.png
                if (parts.length != 6) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                if (!parts[2].matches("-?\\d+") ||     // dim
                        !parts[3].matches("\\d+") ||      // layerId
                        !parts[4].matches("-?\\d+") ||    // rx
                        !parts[5].matches("-?\\d+\\.png")) { // rz.png
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                int dim = Integer.parseInt(parts[2]);
                int layerId = Integer.parseInt(parts[3]);
                int rx = Integer.parseInt(parts[4]);
                int rz = Integer.parseInt(parts[5].replace(".png", ""));

                // FIXME: once we have server rendering for caves / nether we can enable this
                if(dim != 0) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }


                Path basePath = NAPaths.getWorldMapStoragePath(layerId, dim, true);
                Path tilePath = basePath
                        .resolve("region_" + rx + "_" + rz + ".png")
                        .normalize()
                        .toAbsolutePath();

                Path realBasePath = basePath.toRealPath();
                Path realTilePath = tilePath.toRealPath();

                if (!realTilePath.startsWith(realBasePath)) {
                    LogUtil.error("Tile path attempted escape: {}", realTilePath);
                    exchange.sendResponseHeaders(403, -1);
                    return;
                }

                if (!Files.exists(tilePath)) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }

                byte[] bytes = Files.readAllBytes(tilePath);
                exchange.getResponseHeaders().add("Content-Type", "image/png");
                exchange.getResponseHeaders().add("Cache-Control", "public, max-age=86400");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);

            } catch (Exception e) {
                LogUtil.error("Error serving tile request: {}", path, e);
                exchange.sendResponseHeaders(500, -1);
            } finally {
                exchange.close();
            }
        });
    }
}

