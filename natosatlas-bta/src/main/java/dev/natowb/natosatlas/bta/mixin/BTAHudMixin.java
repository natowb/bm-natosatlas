package dev.natowb.natosatlas.bta.mixin;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.ui.elements.UIScaleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.HudIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HudIngame.class)
public class BTAHudMixin {
    @Inject(method = "renderGameOverlay", at = @At("TAIL"))
    private void natosatlas$drawMiniMap(float partialTicks, boolean flag, int mouseX, int mouseY, CallbackInfo ci) {

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.resolution == null) return;

        int scale = mc.resolution.getScale();
        int screenW = mc.resolution.getScaledWidthScreenCoords();
        int screenH = mc.resolution.getScaledHeightScreenCoords();

        UIScaleInfo scaleInfo = new UIScaleInfo(scale, screenW, screenH);
        NAClient client = NAClient.get();
        if (client != null) {
            client.renderGui(scaleInfo);
        }

    }
}
