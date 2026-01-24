package dev.natowb.natosatlas.stationapi;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.map.MapScreen;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.tick.GameTickEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import org.lwjgl.input.Keyboard;

import java.lang.invoke.MethodHandles;

public class NatosAtlasST {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    public static KeyBinding KEY_BINDING_MAP;
    private NatosAtlasCore nac;

    @EventListener
    public void init(InitFinishedEvent event) {
        nac = new NatosAtlasCore(new PlatformST());
    }


    @EventListener
    public void registerKeybinds(KeyBindingRegisterEvent event) {
        KEY_BINDING_MAP = new KeyBinding("key.natosatlas.map_keybind", Keyboard.KEY_M);
        event.keyBindings.add(NatosAtlasST.KEY_BINDING_MAP);
    }


    @EventListener
    void onGameTick(GameTickEvent.End event) {
        nac.onTick();
    }

    @EventListener
    public void handle(KeyStateChangedEvent event) {
        if (NatosAtlasCore.get().isStopped()) return;

        if (Keyboard.getEventKeyState()) {
            if (Keyboard.isKeyDown(NatosAtlasST.KEY_BINDING_MAP.code)) {
                if (event.environment == KeyStateChangedEvent.Environment.IN_GAME) {
                    NatosAtlasCore.get().platform.openNacScreen(new MapScreen(null));
                }
            }
        }
    }
}
