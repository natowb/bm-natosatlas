package dev.natowb.natosatlas.stationapi.client;

import dev.natowb.natosatlas.client.access.BlockAccess;
import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.stationapi.STBlockAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.init.InitFinishedEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;

import java.lang.invoke.MethodHandles;

public class ClientEntry {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @EventListener
    public void init(InitFinishedEvent event) {
        BlockAccess.set(new STBlockAccess());
        ClientWorldAccess.set(new STClientWorldAccess());
        NACore.init(FabricLoader.getInstance().getGameDir());
        NACore.startClient(new STClientPlatform());
    }
}
