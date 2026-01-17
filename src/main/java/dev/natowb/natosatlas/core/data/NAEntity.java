package dev.natowb.natosatlas.core.data;

public class NAEntity {
    public final NAEntityType type;
    public final double x;
    public final double y;
    public final double z;
    public final double yaw;

    public NAEntity(double x, double y, double z, double yaw, NAEntityType type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.type = type;
    }

    public enum NAEntityType {
        Player,
        Mob,
        Animal,
        Waypoint
    }
}
