package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.NatosAtlas;
import dev.natowb.natosatlas.core.map.MapScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.tick.GameTickEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import org.lwjgl.input.Keyboard;

import java.lang.invoke.MethodHandles;

public class NatosAtlasMod {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    public static KeyBinding KEY_BINDING_MAP;
    private boolean inWorld;
    private NatosAtlas nac;


    @EventListener
    public void init(InitFinishedEvent event) {
        nac = new NatosAtlas(new NacPlatformST());
    }


    @EventListener
    public void registerKeybinds(KeyBindingRegisterEvent event) {
        KEY_BINDING_MAP = new KeyBinding("key.natosatlas.map_keybind", Keyboard.KEY_M);
        event.keyBindings.add(NatosAtlasMod.KEY_BINDING_MAP);
    }

    @EventListener
    void onGameTick(GameTickEvent.End event) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        if (mc.world == null && inWorld) {
            inWorld = false;
            nac.onWorldLeft();
        }

        if (mc.world != null && !inWorld) {
            inWorld = true;
            nac.onWorldJoin();

        }

        if (inWorld && mc.player != null) {
            nac.onWorldUpdate();
        }
    }

    @EventListener
    public void handle(KeyStateChangedEvent event) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        if (Keyboard.getEventKeyState()) {
            if (Keyboard.isKeyDown(NatosAtlasMod.KEY_BINDING_MAP.code)) {
                if (event.environment == KeyStateChangedEvent.Environment.IN_GAME) {
                    NatosAtlas.get().platform.openNacScreen(new MapScreen(null));
                }
            }
        }
    }
}
