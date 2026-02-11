package dev.natowb.natosatlas.modloader.client;

import dev.natowb.natosatlas.client.ClientPlatform;
import dev.natowb.natosatlas.client.access.ScreenAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;

public class MLClientPlatform extends ClientPlatform {
    public MLClientPlatform() {
        super(new MLPainter(),  new ScreenAccess() {
            @Override
            public void openNacScreen(UIScreen screen) {
                Minecraft mc = ModLoader.getMinecraftInstance();
                if (screen == null) {
                    mc.setScreen(null);
                } else {
                    mc.setScreen(new MLScreenWrapper(screen));
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
