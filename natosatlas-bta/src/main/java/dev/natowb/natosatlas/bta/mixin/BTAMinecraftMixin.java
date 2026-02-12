package dev.natowb.natosatlas.bta.mixin;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.map.MapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public class BTAMinecraftMixin {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(method = "runTick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (!NACore.isInitialized()) return;

        NACore.tick();

        if (currentScreen != null) return;

        if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
            NAClient.get().getPlatform().screen.openNacScreen(new MapScreen(null));
        }
    }
}
