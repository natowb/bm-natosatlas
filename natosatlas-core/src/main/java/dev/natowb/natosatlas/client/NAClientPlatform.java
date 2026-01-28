package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.PainterAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import dev.natowb.natosatlas.client.access.BlockAccess;
import dev.natowb.natosatlas.client.access.ClientWorldAccess;

public abstract class NAClientPlatform {

    public final PainterAccess painter;
    public final ClientWorldAccess world;

    public NAClientPlatform(PainterAccess painter, BlockAccess blockAccess, ClientWorldAccess worldAccess) {
        this.painter = painter;
        this.world = worldAccess;
        BlockAccess.setInstance(blockAccess);
    }

    public abstract void openNacScreen(UIScreen screen);

    public abstract void playSound(String sound, float volume, float pitch);
}
