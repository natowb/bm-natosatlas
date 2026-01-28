package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.NACore;
import net.fabricmc.api.ModInitializer;
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
        nac = new NACore(new PlatformBTA());
    }
}
