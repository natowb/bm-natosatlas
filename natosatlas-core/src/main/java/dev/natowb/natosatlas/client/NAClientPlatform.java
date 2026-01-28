package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.PainterAccess;
import dev.natowb.natosatlas.client.access.ScreenAccess;
import dev.natowb.natosatlas.client.access.BlockAccess;
import dev.natowb.natosatlas.client.access.WorldAccess;

public abstract class NAClientPlatform {
    public final PainterAccess painter;
    public final WorldAccess world;
    public final ScreenAccess screen;

    public NAClientPlatform(PainterAccess painter, BlockAccess blockAccess, WorldAccess worldAccess, ScreenAccess screen) {
        this.painter = painter;
        this.world = worldAccess;
        this.screen = screen;
        BlockAccess.setInstance(blockAccess);
    }
}
