package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.NAPainter;
import dev.natowb.natosatlas.client.access.ScreenAccess;

public abstract class ClientPlatform {
    public final NAPainter painter;
    public final ScreenAccess screen;

    public ClientPlatform(NAPainter painter, ScreenAccess screen) {
        this.painter = painter;
        this.screen = screen;
    }
}
