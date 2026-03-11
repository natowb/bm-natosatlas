package dev.natowb.natosatlas.stationapi.client;

import dev.natowb.natosatlas.client.platform.BlockAccess;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.stationapi.STBlockAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import org.lwjgl.input.Keyboard;

import java.lang.invoke.MethodHandles;

public class STClientEntry {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    public static final KeyBinding KEY_MAP = new KeyBinding("natosatlas.key.map", Keyboard.KEY_M);
    public static final KeyBinding KEY_WAYPOINTS = new KeyBinding("natosatlas.key.waypoints", Keyboard.KEY_J);
    public static final KeyBinding KEY_ADD_WAYPOINT = new KeyBinding("natosatlas.key.add_waypoint", Keyboard.KEY_K);


    @EventListener
    public void registerKeybinds(KeyBindingRegisterEvent event) {
        event.keyBindings.add(KEY_MAP);
        event.keyBindings.add(KEY_WAYPOINTS);
        event.keyBindings.add(KEY_ADD_WAYPOINT);
    }

    @EventListener
    public void init(InitFinishedEvent event) {
        BlockAccess.set(new STBlockAccess());
        ClientWorldAccess.set(new STClientWorldAccess());
        NACore.init(FabricLoader.getInstance().getGameDir());
        NACore.startClient(new STClientPlatform());
    }
}
