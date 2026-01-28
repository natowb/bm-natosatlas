package dev.natowb.natosatlas.stationapi;

import dev.natowb.natosatlas.client.NAClientPlatform;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class PlatformST extends NAClientPlatform {

    public PlatformST() {
        super(new PlatformPainterST(), new BlockAccessST(), new WorldAccessST());
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
