package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.NatosAtlas;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class NatoAtlasBTA implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
    private NatosAtlas nac;

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
        nac = new NatosAtlas(new PlatformBTA());
    }
}
