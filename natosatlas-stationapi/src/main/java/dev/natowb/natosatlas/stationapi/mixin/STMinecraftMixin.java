package dev.natowb.natosatlas.stationapi.mixin;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.ui.screens.map.MapScreen;
import dev.natowb.natosatlas.client.ui.screens.waypoint.WaypointCreateScreen;
import dev.natowb.natosatlas.client.ui.screens.waypoint.WaypointListScreen;
import dev.natowb.natosatlas.core.NACore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.natowb.natosatlas.stationapi.client.STClientEntry.*;

@Mixin(value = Minecraft.class, remap = false)
public class STMinecraftMixin {

    @Shadow
    public Screen currentScreen;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (!NACore.isInitialized()) return;

        NACore.tick();

        if (currentScreen != null) return;

        if (Keyboard.isKeyDown(KEY_MAP.code)) {
            NAClient.get().getPlatform().screen.openNacScreen(new MapScreen(null));
        }

        if (Keyboard.isKeyDown(KEY_WAYPOINTS.code)) {
            NAClient.get().getPlatform().screen.openNacScreen(new WaypointListScreen(null));
        }

        if (Keyboard.isKeyDown(KEY_ADD_WAYPOINT.code)) {
            NAClient.get().getPlatform().screen.openNacScreen(new WaypointCreateScreen(null));
        }
    }
}
