package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.glue.NacPlatform;
import dev.natowb.natosatlas.core.models.NacScaleInfo;
import dev.natowb.natosatlas.core.models.NacWorldInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ScreenScaler;

import java.nio.file.Path;

public class NacPlatformST extends NacPlatform {

    public NacPlatformST() {
        super(new NacPainterST(), new NacEntityProviderST(), new NacChunkProviderST());
    }

    @Override
    public NacWorldInfo getCurrentWorldInfo() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        String name;
        if (mc.isWorldRemote()) {
            name = mc.options.lastServer;
        } else {
            name = mc.world.getProperties().getName();
        }

        return new NacWorldInfo(name, mc.player.dimensionId == 0);
    }

    @Override
    public NacScaleInfo getScaleInfo() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        ScreenScaler ss = new ScreenScaler(mc.options, mc.displayWidth, mc.displayHeight);
        NacScaleInfo scaleInfo = new NacScaleInfo(ss.scaleFactor, ss.getScaledWidth(), ss.getScaledHeight());
        return scaleInfo;
    }

    @Override
    public Path getMinecraftDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }
}
