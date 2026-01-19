package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;

import java.nio.file.Path;

public class PlatformML extends Platform {
    public PlatformML() {
        super(new PlatformPainterML(), new PlatformWorldProviderML());
    }

    @Override
    public Path getMinecraftDirectory() {
        return Minecraft.getRunDirectory().toPath();
    }

    @Override
    public void openNacScreen(UIScreen screen) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        if (screen == null) {
            mc.setScreen(null);
        } else {
            mc.setScreen(new UIScreenWrapperML(screen));
        }
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        mc.soundManager.playSound(sound, volume, pitch);
    }
}
