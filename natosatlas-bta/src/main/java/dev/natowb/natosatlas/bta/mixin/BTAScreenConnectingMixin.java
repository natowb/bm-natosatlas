package dev.natowb.natosatlas.bta.mixin;


import dev.natowb.natosatlas.core.NAClientSession;
import dev.natowb.natosatlas.core.NACore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenConnecting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScreenConnecting.class, remap = false)
public class BTAScreenConnectingMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Minecraft minecraft, String hostName, int port, CallbackInfo ci) {

        if (!NACore.isInitialized()) return;
        NAClientSession session = NACore.getClientSession();
        if(session == null) return;

        session.setServerName(String.format("%s-%d", hostName, port));
    }
}