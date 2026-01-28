package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.client.access.BlockAccess;
import dev.natowb.natosatlas.client.access.ClientWorldAccess;
import dev.natowb.natosatlas.core.NACore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class NatoAtlasBTA implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
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
        BlockAccess.set(new BlockAccessBTA());
        ClientWorldAccess.set(new WorldAccessBTA());
        NACore.initClient(FabricLoader.getInstance().getGameDir(), new PlatformBTA());
    }
}
