package dev.natowb.natosatlas.bta.client;

import dev.natowb.natosatlas.bta.BTABlockAccess;
import dev.natowb.natosatlas.client.platform.BlockAccess;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.core.NACore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class BTAClientEntry implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    private NACore nac;

    @Override
    public void onInitialize() {
    }

    @Override
    public void onRecipesReady() {
    }

    @Override
    public void initNamespaces() {
    }

    @Override
    public void beforeGameStart() {
    }

    @Override
    public void afterGameStart() {
        BlockAccess.set(new BTABlockAccess());
        ClientWorldAccess.set(new BTAClientWorldAccess());
        NACore.init(FabricLoader.getInstance().getGameDir());
        NACore.startClient(new BTAClientPlatform());
    }
}
