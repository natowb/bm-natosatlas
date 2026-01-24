package dev.natowb.natosatlas.bta.mixin;

import dev.natowb.natosatlas.bta.NatoAtlasBTA;
import dev.natowb.natosatlas.bta.WorldWrapperBTA;
import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.MapScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.world.save.SaveFile;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {

    @Shadow
    public PlayerLocal thePlayer;

    @Shadow
    public WorldClient currentWorld;


    @Unique
    private String getWorldSaveName() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        if (mc.isMultiplayerWorld()) {
            return mc.gameSettings.lastServer.name;
        }

        mc.currentWorld.pauseScreenSave(0);
        List<SaveFile> saves = mc.getSaveFormat().getSaveFileList();
        if (saves == null || saves.isEmpty()) return null;
        String currentName = mc.currentWorld.getLevelData().getWorldName();
        SaveFile best = null;
        long bestTime = Long.MIN_VALUE;
        for (SaveFile info : saves) {
            if (info.getDisplayName().equals(currentName)) {
                long t = info.getLastTimePlayed();
                if (t > bestTime) {
                    bestTime = t;
                    best = info;
                }
            }
        }

        if (best != null) {
            return best.getFileName();
        }
        return null;
    }


    @Inject(method = "checkBoundInputs", at = @At("TAIL"), remap = false)
    private void checkBoundInputs(InputDevice currentInputDevice, CallbackInfoReturnable<Boolean> cir) {

        if (!NatoAtlasBTA.inWorld) {
            return;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
            NatosAtlas.get().platform.openNacScreen(new MapScreen(null));
        }
    }

    @Inject(method = "runTick", at = @At("TAIL"), remap = false)
    private void onConstructed(CallbackInfo ci) {
        if (currentWorld == null && NatoAtlasBTA.inWorld) {
            NatoAtlasBTA.inWorld = false;
            NatoAtlasBTA.nac.onWorldLeft();
        }

        if (currentWorld != null && !NatoAtlasBTA.inWorld) {
            String worldSaveName = getWorldSaveName();
            if (worldSaveName == null) return;
            NatoAtlasBTA.inWorld = true;
            NatoAtlasBTA.nac.onWorldJoin(new WorldWrapperBTA(currentWorld, worldSaveName));
        }

        if (NatoAtlasBTA.inWorld && thePlayer != null) {
            NatoAtlasBTA.nac.onWorldUpdate();
        }
    }
}
