package dev.natowb.natosatlas.core.models;

public class NacEntity {
    public final double x;
    public final double z;
    public final double yaw;
    public final int iconIndex;

    public NacEntity(double x, double z, double yaw, int iconIndex) {
        this.x = x;
        this.z = z;
        this.yaw = yaw;
        this.iconIndex = iconIndex;
    }
}
