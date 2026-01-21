package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.NatosAtlas;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class NatoAtlasBTA implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {
	public static final String MOD_ID = "natoatlas";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static boolean inWorld;
	public static NatosAtlas nac;

	@Override
	public void onInitialize() {
		LOGGER.info("ExampleMod initialized.");
	}

	@Override
	public void onRecipesReady() {}

	@Override
	public void initNamespaces() {}

	@Override
	public void beforeGameStart() {}

	@Override
	public void afterGameStart() {
		nac = new NatosAtlas(new PlatformBTA());
	}
}
