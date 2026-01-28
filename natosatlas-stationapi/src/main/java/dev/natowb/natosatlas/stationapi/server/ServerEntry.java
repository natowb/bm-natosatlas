package dev.natowb.natosatlas.stationapi.server;

import dev.natowb.natosatlas.client.access.BlockAccess;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.stationapi.client.BlockAccessST;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.event.tick.GameTickEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;

import java.lang.invoke.MethodHandles;

public class ServerEntry {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @EventListener
    public void init(InitFinishedEvent event) {
        BlockAccess.set(new BlockAccessST());
        NACore.initServer(FabricLoader.getInstance().getGameDir(), new ServerPlatformST());
    }

    @EventListener
    void onGameTick(GameTickEvent.End event) {
        NACore.tick();
    }
}
