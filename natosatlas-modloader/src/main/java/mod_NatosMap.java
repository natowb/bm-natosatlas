import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.MapScreen;
import dev.natowb.natosatlas.modloader.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import org.lwjgl.input.Keyboard;


public class mod_NatosMap extends BaseMod {

    private static final KeyBinding KEY_BINDING_MAP = new KeyBinding("Nato's Map", Keyboard.KEY_M);
    private NatosAtlas nac;

    @Override
    public void ModsLoaded() {
        ModLoader.RegisterKey(this, KEY_BINDING_MAP, false);
        ModLoader.SetInGameHook(this, true, true);
        ModLoader.SetInGUIHook(this, true, true);
        nac = new NatosAtlas(new PlatformML());
    }

    @Override
    public boolean OnTickInGUI(Minecraft mc, Screen gui) {
        NatosAtlas.get().onTick();
        return true;
    }

    @Override
    public boolean OnTickInGame(Minecraft mc) {
        NatosAtlas.get().onTick();
        return true;
    }

    @Override
    public void KeyboardEvent(KeyBinding key) {
        if(NatosAtlas.get().isStopped()) return;
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