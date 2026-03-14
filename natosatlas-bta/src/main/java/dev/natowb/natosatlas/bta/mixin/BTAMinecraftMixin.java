package dev.natowb.natosatlas.bta.mixin;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.ui.screens.waypoint.Waypoint;
import dev.natowb.natosatlas.client.ui.screens.waypoint.WaypointCreateScreen;
import dev.natowb.natosatlas.client.ui.screens.waypoint.WaypointListScreen;
import dev.natowb.natosatlas.client.ui.screens.waypoint.Waypoints;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.ui.screens.map.MapScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.gui.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.natowb.natosatlas.bta.client.BTAClient.*;

@Mixin(value = Minecraft.class, remap = false)
public class BTAMinecraftMixin {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    public PlayerLocal thePlayer;

    @Unique boolean deathTriggered = false;

    @Inject(method = "runTick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        NACore.tick();


        if (this.thePlayer == null) return;
        if (thePlayer.getHealth() > 0) deathTriggered = false;
        if (this.thePlayer.getHealth() <= 0 && !deathTriggered) {
            deathTriggered = true;
            Waypoints.add(new Waypoint(
                    "Last Death",
                    (int) this.thePlayer.x,
                    (int) this.thePlayer.y,
                    (int) this.thePlayer.z,
                    0xFFCC2222)
            );
        }
        if (currentScreen != null) return;

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
