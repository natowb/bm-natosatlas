package dev.natowb.natosatlas.client.platform;

public abstract class ClientPlatform {
    public final NAPainter painter;
    public final ScreenAccess screen;

    public ClientPlatform(NAPainter painter, ScreenAccess screen) {
        this.painter = painter;
        this.screen = screen;
    }
}
