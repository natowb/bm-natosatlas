package dev.natowb.natosatlas.core.map;

public class MapEntity {
    public final double x;
    public final double y;
    public final double z;
    public final double yaw;
    public final int iconIndex;

    public MapEntity(double x, double y, double z, double yaw, int iconIndex) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.iconIndex = iconIndex;
    }
}
