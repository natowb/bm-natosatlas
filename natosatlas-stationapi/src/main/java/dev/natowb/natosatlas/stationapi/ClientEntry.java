package dev.natowb.natosatlas.stationapi;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.client.map.MapScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.tick.GameTickEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import org.lwjgl.input.Keyboard;

import java.lang.invoke.MethodHandles;

public class ClientEntry {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    public static KeyBinding KEY_BINDING_MAP;

    @EventListener
    public void init(InitFinishedEvent event) {
        NACore.initClient(FabricLoader.getInstance().getGameDir(),new PlatformST());
    }


    @EventListener
    public void registerKeybinds(KeyBindingRegisterEvent event) {
        KEY_BINDING_MAP = new KeyBinding("key.natosatlas.map_keybind", Keyboard.KEY_M);
        event.keyBindings.add(ClientEntry.KEY_BINDING_MAP);
    }


    @EventListener
    void onGameTick(GameTickEvent.End event) {
        NACore.tick();
    }

    @EventListener
    public void handle(KeyStateChangedEvent event) {
        if (!NACore.isInitialized()) return;

        if (Keyboard.getEventKeyState()) {
            if (Keyboard.isKeyDown(ClientEntry.KEY_BINDING_MAP.code)) {
                if (event.environment == KeyStateChangedEvent.Environment.IN_GAME) {
                    NACore.getClient().getPlatform().openNacScreen(new MapScreen(null));
                }
            }
        }
    }
}
