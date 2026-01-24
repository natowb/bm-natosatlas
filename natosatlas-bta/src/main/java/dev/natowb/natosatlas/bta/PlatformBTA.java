package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.platform.Platform;
import dev.natowb.natosatlas.core.ui.elements.UIScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.sound.SoundCategory;

import java.nio.file.Path;

public class PlatformBTA extends Platform {
	public PlatformBTA() {
		super(new PlatformPainterBTA(),  new BlockAccessBTA());
	}

	@Override
	public Path getMinecraftDirectory() {
		return FabricLoader.getInstance().getGameDir();
	}

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
}
