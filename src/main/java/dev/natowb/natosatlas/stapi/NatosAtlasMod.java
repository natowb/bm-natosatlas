package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.NAC;
import dev.natowb.natosatlas.stapi.screens.AtlasScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.tick.GameTickEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.lang.invoke.MethodHandles;

public class NatosAtlasMod {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();
    public static final Logger LOGGER = NAMESPACE.getLogger();

    public static KeyBinding KEY_BINDING_MAP;
    private boolean inWorld;
    private final NAC nac;


    public NatosAtlasMod() {
        nac = new NAC(new NacPlatformST());
    }


    @EventListener
    public void registerKeybinds(KeyBindingRegisterEvent event) {
        KEY_BINDING_MAP = new KeyBinding("key.natoworldmap.map_keybind", Keyboard.KEY_M);
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
                    mc.setScreen(new AtlasScreen());
                }

                if (event.environment == KeyStateChangedEvent.Environment.IN_GUI && mc.currentScreen instanceof AtlasScreen) {
                    mc.setScreen(null);
                }
            }
        }
    }
}
