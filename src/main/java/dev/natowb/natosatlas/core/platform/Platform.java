package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import dev.natowb.natosatlas.core.utils.LogUtil;

import java.nio.file.Path;

public abstract class Platform {
    public final PlatformPainter painter;
    public final PlatformWorldProvider worldProvider;

    public Platform(PlatformPainter painter, PlatformWorldProvider worldProvider) {
        this.painter = painter;
        this.worldProvider = worldProvider;
        LogUtil.info("Platform", "Platform initialized");
    }

    public abstract UIScaleInfo getScaleInfo();

    public abstract Path getMinecraftDirectory();

    public abstract void openNacScreen(UIScreen screen);

    public abstract void playSound(String sound, float volume, float pitch);
}
