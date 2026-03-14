package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.bta.client.BTAClient;
import dev.natowb.natosatlas.core.util.LogUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class BTANatosAtlas implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {

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
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            BTAClient.setup();
        } else {
            LogUtil.warn("starting NatosAtlas as server... nothing implemented yet");
        }
    }

}
