package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.wrapper.BlockAccess;

import java.nio.file.Path;

public abstract class Platform {
    public final PlatformPainter painter;

    public Platform(PlatformPainter painter,  BlockAccess blockAccess) {
        this.painter = painter;
        BlockAccess.setInstance(blockAccess);
    }

    public abstract Path getMinecraftDirectory();

    public abstract void openNacScreen(UIScreen screen);

    public abstract void playSound(String sound, float volume, float pitch);
}
