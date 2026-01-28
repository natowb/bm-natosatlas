package dev.natowb.natosatlas.server.web.routes;

import com.sun.net.httpserver.HttpServer;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.server.NAServerPlatform;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlayerRoute {

    public static void register(HttpServer server, NAServerPlatform platform) {
        server.createContext("/players", exchange -> {
            try {
                if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }

                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");

                if (parts.length != 3 || !parts[2].matches("-?\\d+")) {
                    exchange.sendResponseHeaders(400, -1);
                    return;
                }

                int dim = Integer.parseInt(parts[2]);
                // FIXME: once we have server rendering for caves / nether we can enable this
                if(dim != 0) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                List<NAEntity> players = platform.getPlayers(dim);

                StringBuilder sb = new StringBuilder();
                sb.append("[");

                for (int i = 0; i < players.size(); i++) {
                    NAEntity p = players.get(i);

                    sb.append("{")
                            .append("\"x\":").append(p.x).append(",")
                            .append("\"y\":").append(p.y).append(",")
                            .append("\"z\":").append(p.z).append(",")
                            .append("\"yaw\":").append(p.yaw).append(",")
                            .append("\"type\":\"").append(p.type.name()).append("\",")
                            .append("\"name\":\"").append(p.name).append("\"")
                            .append("}");

                    if (i < players.size() - 1) sb.append(",");
                }

                sb.append("]");

                byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);

            } catch (Exception e) {
                LogUtil.error("Error serving /players", e);
                exchange.sendResponseHeaders(500, -1);
            } finally {
                exchange.close();
            }
        });
    }
}
