package dev.natowb.natosatlas.core.models;

public class NacCanvasInfo {
    public final int width;
    public final int height;
    public final double scrollX;
    public final double scrollY;
    public final double zoom;
    public final int mouseX;
    public final int mouseY;

    public NacCanvasInfo(int width, int height, double scrollX, double scrollY, double zoom, int mouseX, int mouseY) {
        this.width = width;
        this.height = height;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.zoom = zoom;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
