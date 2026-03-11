package dev.natowb.natosatlas.bta.mixin;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.ui.screens.settings.SettingsScreen;
import dev.natowb.natosatlas.client.ui.screens.waypoint.WaypointCreateScreen;
import dev.natowb.natosatlas.client.ui.screens.waypoint.WaypointListScreen;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.ui.screens.map.MapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.natowb.natosatlas.bta.client.BTAClientEntry.*;

@Mixin(value = Minecraft.class, remap = false)
public class BTAMinecraftMixin {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(method = "runTick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (!NACore.isInitialized() || currentScreen != null) return;

        NACore.tick();

        if (Keyboard.isKeyDown(KEY_MAP.getKeyCode())) {
            NAClient.get().getPlatform().screen.openNacScreen(new MapScreen(null));
        }

        if (Keyboard.isKeyDown(KEY_WAYPOINTS.getKeyCode())) {
            NAClient.get().getPlatform().screen.openNacScreen(new WaypointListScreen(null));
        }

        if (Keyboard.isKeyDown(KEY_ADD_WAYPOINT.getKeyCode())) {
            NAClient.get().getPlatform().screen.openNacScreen(new WaypointCreateScreen(null));
        }
    }
}
