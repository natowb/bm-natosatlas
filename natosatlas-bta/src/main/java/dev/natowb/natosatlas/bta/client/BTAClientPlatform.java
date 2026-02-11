package dev.natowb.natosatlas.bta.client;

import dev.natowb.natosatlas.client.ClientPlatform;
import dev.natowb.natosatlas.client.access.ScreenAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.sound.SoundCategory;

public class BTAClientPlatform extends ClientPlatform {
    public BTAClientPlatform() {
        super(new BTAPainter(), new ScreenAccess() {
            @Override
            public void openNacScreen(UIScreen screen) {
                Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                if (screen == null) {
                    mc.displayScreen(null);
                } else {
                    mc.displayScreen(new BTAScreenWrapper(screen));
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
