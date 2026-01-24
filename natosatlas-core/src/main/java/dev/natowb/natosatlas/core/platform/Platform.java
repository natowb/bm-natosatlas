package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.access.BlockAccess;
import dev.natowb.natosatlas.core.access.WorldAccess;

import java.nio.file.Path;

public abstract class Platform {
    public final PlatformPainter painter;

    public Platform(PlatformPainter painter, BlockAccess blockAccess, WorldAccess worldAccess) {
        this.painter = painter;
        BlockAccess.setInstance(blockAccess);
        WorldAccess.setInstance(worldAccess);
    }

    public abstract Path getMinecraftDirectory();

    public abstract void openNacScreen(UIScreen screen);

    public abstract void playSound(String sound, float volume, float pitch);
}
