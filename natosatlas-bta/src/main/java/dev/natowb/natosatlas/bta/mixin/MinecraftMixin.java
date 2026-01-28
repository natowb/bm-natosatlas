package dev.natowb.natosatlas.bta.mixin;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.map.MapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.InputDevice;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {

    @Inject(method = "runTick", at = @At("TAIL"), remap = false)
    private void onConstructed(CallbackInfo ci) {
        NACore.tick();
    }

    @Inject(method = "checkBoundInputs", at = @At("TAIL"), remap = false)
    private void checkBoundInputs(InputDevice currentInputDevice, CallbackInfoReturnable<Boolean> cir) {
        if (!NACore.isInitialized()) return;
        if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
            NACore.getClient().getPlatform().openNacScreen(new MapScreen(null));
        }
    }
}
