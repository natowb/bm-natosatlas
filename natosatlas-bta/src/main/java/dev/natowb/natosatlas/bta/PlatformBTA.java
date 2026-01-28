package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.client.NAClientPlatform;
import dev.natowb.natosatlas.client.access.ScreenAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.sound.SoundCategory;

public class PlatformBTA extends NAClientPlatform {
    public PlatformBTA() {
        super(new PlatformPainterBTA(), new ScreenAccess() {
            @Override
            public void openNacScreen(UIScreen screen) {
                Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                if (screen == null) {
                    mc.displayScreen(null);
                } else {
                    mc.displayScreen(new UIScreenWrapperBTA(screen));
                }
            }

            @Override
            public void playSound(String sound, float volume, float pitch) {
                Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                mc.sndManager.playSound(sound, SoundCategory.GUI_SOUNDS, volume, pitch);
            }
        });
    }
}
