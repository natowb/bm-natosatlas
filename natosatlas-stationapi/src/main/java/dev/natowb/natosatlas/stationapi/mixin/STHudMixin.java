package dev.natowb.natosatlas.stationapi.mixin;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.ui.elements.UIScaleInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.ScreenScaler;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class STHudMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void natosatlas$drawMiniMap(float screenOpen, boolean mouseX, int mouseY, int par4, CallbackInfo ci) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        if (mc == null) return;
        ScreenScaler scaler = new ScreenScaler(mc.options, mc.displayWidth, mc.displayHeight);
        int scale = scaler.scaleFactor;
        int screenW = scaler.getScaledWidth();
        int screenH = scaler.getScaledHeight();

        mc.gameRenderer.setupHudRender();
        GL11.glEnable(3042);

        UIScaleInfo scaleInfo = new UIScaleInfo(scale, screenW, screenH);
        NAClient client = NAClient.get();
        if (client != null) {
            client.renderGui(scaleInfo);
        }

    }
}
