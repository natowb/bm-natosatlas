package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.client.NAClientPlatform;
import dev.natowb.natosatlas.client.access.ScreenAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;

public class PlatformML extends NAClientPlatform {
    public PlatformML() {
        super(new PlatformPainterML(), new BlockAccessML(), new WorldAccessML(), new ScreenAccess() {
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
        });
    }
}
