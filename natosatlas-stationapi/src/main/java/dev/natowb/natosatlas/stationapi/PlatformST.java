package dev.natowb.natosatlas.stationapi;

import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.nio.file.Path;

public class PlatformST extends Platform {

    public PlatformST() {
        super(new PlatformPainterST(), new PlatformWorldProviderST(), new BlockAccessST());
    }


    @Override
    public Path getMinecraftDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public void openNacScreen(UIScreen screen) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        if (screen == null) {
            mc.setScreen(null);
        } else {
            mc.setScreen(new UIScreenWrapperST(screen));
        }
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        mc.soundManager.playSound(sound, volume, pitch);

    }
}
