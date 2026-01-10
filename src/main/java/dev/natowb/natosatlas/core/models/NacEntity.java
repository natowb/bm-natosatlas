package dev.natowb.natosatlas.core.models;

public class NacEntity {
    public final double worldX;
    public final double worldZ;
    public final double yaw;
    public final int iconIndex;

    public NacEntity(double worldX, double worldZ, double yaw, int iconIndex) {
        this.worldX = worldX;
        this.worldZ = worldZ;
        this.yaw = yaw;
        this.iconIndex = iconIndex;
    }
}
