package dev.natowb.natosatlas.client.ui.screens.waypoint;

public class Waypoint {

    public final String name;
    public final int x;
    public final int y;
    public final int z;
    public int color;

    public boolean visible;

    public Waypoint(String name, int x, int y, int z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.visible = true;
        this.color = 0xFFFFFF;
    }

    public Waypoint(String name, int x, int y, int z, int argb) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.visible = true;
        this.color = argb;
    }
}
