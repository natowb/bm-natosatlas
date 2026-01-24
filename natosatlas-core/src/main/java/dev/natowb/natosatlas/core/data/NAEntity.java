package dev.natowb.natosatlas.core.data;

import java.util.HashMap;
import java.util.Map;

public class NAEntity {
    public final NAEntityType type;
    public final double x;
    public final double y;
    public final double z;
    public final int chunkX;
    public final int chunkZ;
    public final int localX;
    public final int localZ;
    public final double yaw;
    public String texturePath = "/mob/char.png";

    public NAEntity(double x, double y, double z, double yaw, NAEntityType type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.type = type;
        this.chunkX = (int) Math.floor(x / 16.0);
        this.chunkZ = (int) Math.floor(z / 16.0);
        this.localX = ((int) Math.floor(x)) & 15;
        this.localZ = ((int) Math.floor(z)) & 15;
    }

    public NAEntity setTexturePath(String texturePath) {
        this.texturePath = texturePath;
        return this;
    }

    public enum NAEntityType {
        Player,
        Mob,
        Animal,
        Waypoint
    }

    public static class UV {
        public final float u1, v1, u2, v2;

        public UV(float u1, float v1, float u2, float v2) {
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }
    }

    public static final Map<String, UV> UV_MAP = new HashMap<>();

    static {
        UV_MAP.put("default", new UV(
                1f / 8f,
                1f / 4f,
                2f / 8f,
                2f / 4f
        ));

        UV_MAP.put("squid", new UV(14f / 64f, 15f / 32f, 22f / 64f, 23f / 32f));
        UV_MAP.put("spider", new UV(39f / 64f, 12f / 32f, 49f / 64f, 20f / 32f));
        UV_MAP.put("chicken", new UV(2f / 64f, 3f / 32f, 8f / 64f, 9f / 32f));
        UV_MAP.put("cow", new UV(6f / 64f, 6f / 32f, 14f / 64f, 14f / 32f));
        UV_MAP.put("sheep", new UV(7f / 64f, 7f / 32f, 15f / 64f, 15f / 32f));
        UV_MAP.put("ghast", new UV(16f / 64f, 16f / 32f, 32 / 64f, 32f / 32f));
        UV_MAP.put("wolf", new UV(4f / 64f, 4f / 32f, 10f / 64f, 10f / 32f));
    }

    public static UV getUV(String texturePath) {
        for (String key : UV_MAP.keySet()) {
            if (!key.equals("default") && texturePath.contains(key))
                return UV_MAP.get(key);
        }
        return UV_MAP.get("default");
    }
}
