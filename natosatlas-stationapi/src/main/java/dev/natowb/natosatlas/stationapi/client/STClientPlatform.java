package dev.natowb.natosatlas.stationapi.client;

import dev.natowb.natosatlas.client.ClientPlatform;
import dev.natowb.natosatlas.client.access.ScreenAccess;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class STClientPlatform extends ClientPlatform {
    public STClientPlatform() {
        super(new STPainter(), new ScreenAccess() {
            @Override
            public void openNacScreen(UIScreen screen) {
                Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                if (screen == null) {
                    mc.setScreen(null);
                } else {
                    mc.setScreen(new STScreenWrapper(screen));
                }
            }

            @Override
            public void playSound(String sound, float volume, float pitch) {
                Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
                mc.soundManager.playSound(sound, volume, pitch);

            }
        });
    }
}
