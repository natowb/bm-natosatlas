package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.platform.PlatformWorldProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class NacWorldProviderST implements PlatformWorldProvider {
    @Override
    public String getName() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        String name;
        if (mc.isWorldRemote()) {
            name = mc.options.lastServer;
        } else {
            name = mc.world.getProperties().getName();
        }
        return name;
    }

    @Override
    public boolean isRemote() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return mc.isWorldRemote();
    }

    @Override
    public int getDimension() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        return mc.player.dimensionId;
    }

    @Override
    public boolean isDaytime() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        long time = mc.world.getTime() % 24000L;
        return time < 12000L;
    }

}
