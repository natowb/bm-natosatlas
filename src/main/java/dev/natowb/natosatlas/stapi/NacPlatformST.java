package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.ui.UIScaleInfo;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ScreenScaler;

import java.nio.file.Path;

public class NacPlatformST extends Platform {

    public NacPlatformST() {
        super(new NacPainterST(), new NacEntityProviderST(), new NacChunkProviderST(),
                new NacWorldProviderST());


    }

    @Override
    public UIScaleInfo getScaleInfo() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        ScreenScaler ss = new ScreenScaler(mc.options, mc.displayWidth, mc.displayHeight);
        return new UIScaleInfo(ss.scaleFactor, ss.getScaledWidth(), ss.getScaledHeight());
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
            mc.setScreen(new NacScreenWrapperST(screen));
        }
    }

    @Override
    public void playSound(String sound, float volume, float pitch) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        mc.soundManager.playSound(sound, volume, pitch);

    }
}
