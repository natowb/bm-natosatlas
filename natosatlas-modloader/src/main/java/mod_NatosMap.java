import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.MapScreen;
import dev.natowb.natosatlas.core.wrapper.WorldWrapper;
import dev.natowb.natosatlas.modloader.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import net.minecraft.world.storage.WorldSaveInfo;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class mod_NatosMap extends BaseMod {

    private static final KeyBinding KEY_BINDING_MAP = new KeyBinding("Nato's Map", Keyboard.KEY_M);
    private boolean inWorld;
    private NatosAtlas nac;

    private String getWorldSaveName() {
        Minecraft mc = ModLoader.getMinecraftInstance();
        if (mc.isWorldRemote()) {
            return mc.options.lastServer;
        }

        mc.world.attemptSaving(0);
        List<WorldSaveInfo> saves = mc.getWorldStorageSource().getAll();
        if (saves == null || saves.isEmpty()) return null;
        String currentName = mc.world.getProperties().getName();
        WorldSaveInfo best = null;
        long bestTime = Long.MIN_VALUE;
        for (WorldSaveInfo info : saves) {
            if (info.getName().equals(currentName)) {
                long t = info.getLastPlayed();
                if (t > bestTime) {
                    bestTime = t;
                    best = info;
                }
            }
        }

        if (best != null) {
            return best.getSaveName();
        }
        return null;
    }


    @Override
    public void ModsLoaded() {
        ModLoader.RegisterKey(this, KEY_BINDING_MAP, false);
        ModLoader.SetInGameHook(this, true, true);
        ModLoader.SetInGUIHook(this, true, true);

        nac = new NatosAtlas(new PlatformML());
    }

    @Override
    public boolean OnTickInGUI(Minecraft mc, Screen gui) {
        if (inWorld && mc.world == null) {
            inWorld = false;
            nac.onWorldLeft();
        }
        return true;
    }

    @Override
    public boolean OnTickInGame(Minecraft mc) {
        String worldSave = getWorldSaveName();

        if (worldSave == null) {
            return true;
        }

        if (!inWorld) {
            inWorld = true;
            nac.onWorldJoin(new WorldWrapperML(mc.world, worldSave));
        }

        nac.onWorldUpdate();
        return true;
    }

    @Override
    public void KeyboardEvent(KeyBinding key) {
        if (!inWorld) return;

        Minecraft mc = ModLoader.getMinecraftInstance();
        if (key.code != KEY_BINDING_MAP.code) return;
        if (mc.currentScreen == null) {
            nac.platform.openNacScreen(new MapScreen(null));
        }
    }

    @Override
    public String Version() {
        return NatosAtlas.class.getPackage().getImplementationVersion();
    }
}