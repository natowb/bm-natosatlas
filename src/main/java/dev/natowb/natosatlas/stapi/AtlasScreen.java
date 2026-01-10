package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.models.NacScaleInfo;
import dev.natowb.natosatlas.core.renderer.NacRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ScreenScaler;

public class AtlasScreen extends Screen {
    Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
    private final NacRenderer mapRenderer;

    public AtlasScreen() {
        this.mapRenderer = new NacRenderer();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        ScreenScaler ss = new ScreenScaler(mc.options, mc.displayWidth, mc.displayHeight);
        NacScaleInfo scaleInfo = new NacScaleInfo(ss.scaleFactor, ss.getScaledWidth(), ss.getScaledHeight());
        mapRenderer.draw(mouseX, mouseY, width, height, scaleInfo);
    }
}
