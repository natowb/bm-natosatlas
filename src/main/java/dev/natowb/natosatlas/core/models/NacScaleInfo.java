package dev.natowb.natosatlas.core.models;

public class NacScaleInfo {
    public final int scaleFactor;
    public final int scaledWidth;
    public final int scaledHeight;

    public NacScaleInfo(int scaleFactor, int scaledWidth, int scaledHeight) {
        this.scaleFactor = scaleFactor;
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
    }
}
