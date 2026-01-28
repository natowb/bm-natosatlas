package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.access.PainterAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import dev.natowb.natosatlas.client.access.ClientBlockAccess;
import dev.natowb.natosatlas.client.access.ClientWorldAccess;

public abstract class NAClientPlatform {

    public final PainterAccess painter;
    public final ClientBlockAccess blocks;
    public final ClientWorldAccess world;

    public NAClientPlatform(PainterAccess painter, ClientBlockAccess blockAccess, ClientWorldAccess worldAccess) {
        this.painter = painter;
        this.blocks = blockAccess;
        this.world = worldAccess;
    }

    public abstract void openNacScreen(UIScreen screen);

    public abstract void playSound(String sound, float volume, float pitch);
}
