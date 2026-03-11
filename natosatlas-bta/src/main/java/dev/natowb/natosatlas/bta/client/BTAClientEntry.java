package dev.natowb.natosatlas.bta.client;

import dev.natowb.natosatlas.bta.BTABlockAccess;
import dev.natowb.natosatlas.client.platform.BlockAccess;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.core.NACore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.lwjgl.input.Keyboard;
import turniplabs.halplibe.util.GameStartEntrypoint;
import turniplabs.halplibe.util.RecipeEntrypoint;

public class BTAClientEntry implements ModInitializer, RecipeEntrypoint, GameStartEntrypoint {

    public static final KeyBinding KEY_MAP = new KeyBinding("natosatlas.key.map")
            .setDefault(InputDevice.keyboard, Keyboard.KEY_M);

    public static final KeyBinding KEY_WAYPOINTS = new KeyBinding("natosatlas.key.waypoints")
            .setDefault(InputDevice.keyboard, Keyboard.KEY_J);

    public static final KeyBinding KEY_ADD_WAYPOINT = new KeyBinding("natosatlas.key.add_waypoint")
            .setDefault(InputDevice.keyboard, Keyboard.KEY_K);


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

        OptionsPages.register(
                new OptionsPage("natosatlas.options.title", new ItemStack(Items.MAP))
        ).withComponent(
                new OptionsCategory("natosatlas.options.category.keybinds")
                        .withComponent(new KeyBindingComponent(KEY_MAP))
                        .withComponent(new KeyBindingComponent(KEY_WAYPOINTS))
                        .withComponent(new KeyBindingComponent(KEY_ADD_WAYPOINT))
        );
    }
}
