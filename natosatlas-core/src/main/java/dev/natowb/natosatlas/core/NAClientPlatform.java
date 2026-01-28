package dev.natowb.natosatlas.core;

import dev.natowb.natosatlas.core.access.PainterAccess;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.access.BlockAccess;
import dev.natowb.natosatlas.core.access.WorldAccess;

import java.nio.file.Path;

public abstract class NAClientPlatform {


    public NAClientPlatform(PainterAccess painter, BlockAccess blockAccess, WorldAccess worldAccess) {
        PainterAccess.setInstance(painter);
        BlockAccess.setInstance(blockAccess);
        WorldAccess.setInstance(worldAccess);
    }

    public abstract void openNacScreen(UIScreen screen);

    public abstract void playSound(String sound, float volume, float pitch);
}
