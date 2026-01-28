package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.PainterAccess;
import dev.natowb.natosatlas.client.access.ScreenAccess;

public abstract class NAClientPlatform {
    public final PainterAccess painter;
    public final ScreenAccess screen;

    public NAClientPlatform(PainterAccess painter, ScreenAccess screen) {
        this.painter = painter;
        this.screen = screen;
    }
}
