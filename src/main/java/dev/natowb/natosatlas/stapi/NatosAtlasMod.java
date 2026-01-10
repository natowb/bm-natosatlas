package dev.natowb.natosatlas.stapi;

import dev.natowb.natosatlas.core.NatesAtlas;
import dev.natowb.natosatlas.core.glue.INacFileProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.event.tick.GameTickEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.EntrypointManager;
import net.modificationstation.stationapi.api.util.Namespace;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;

public class NatosAtlasMod implements INacFileProvider {
    static {
        EntrypointManager.registerLookup(MethodHandles.lookup());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static final Namespace NAMESPACE = Namespace.resolve();
    public static final Logger LOGGER = NAMESPACE.getLogger();

    public static KeyBinding KEY_BINDING_MAP;
    private boolean inWorld;
    private NatesAtlas NAC;


    public NatosAtlasMod() {
        NAC = new NatesAtlas(this, new NacChunkGeneratorST(), new NacEntityAdapterST(), new NacPainterST());
    }


    @EventListener
    public void registerKeybinds(KeyBindingRegisterEvent event) {
        KEY_BINDING_MAP = new KeyBinding("key.natoworldmap.map_keybind", Keyboard.KEY_M);
        event.keyBindings.add(NatosAtlasMod.KEY_BINDING_MAP);
    }

    @EventListener
    void onGameTick(GameTickEvent.End event) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        if(mc.player == null) {
            return;
        }

        if (mc.world == null && inWorld) {
            inWorld = false;
            NAC.onWorldLeft();
        }

        if (mc.world != null && !inWorld) {
            inWorld = true;
            NAC.onWorldJoin();
        }

        if (inWorld) {
            NAC.onWorldUpdate();
        }
    }


    @EventListener
    public void handle(KeyStateChangedEvent event) {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();

        if (Keyboard.getEventKeyState()) {
            if (Keyboard.isKeyDown(NatosAtlasMod.KEY_BINDING_MAP.code)) {
                if (event.environment == KeyStateChangedEvent.Environment.IN_GAME) {
                    mc.setScreen(new AtlasScreen());
                }

                if (event.environment == KeyStateChangedEvent.Environment.IN_GUI && mc.currentScreen instanceof AtlasScreen) {
                    mc.setScreen(null);
                }
            }
        }
    }


    @Override
    public Path getDataDirectory() {
        Minecraft mc = (Minecraft) FabricLoader.getInstance().getGameInstance();
        Path mcPath = FabricLoader.getInstance().getGameDir();
        Path dataPath;
        if (mc.isWorldRemote()) {
            dataPath = mcPath.resolve("natesatlas/data/" + mc.options.lastServer);
        } else {
            dataPath = mcPath.resolve("natesatlas/data/" + mc.world.getProperties().getName());
        }

        try {
            Files.createDirectories(dataPath);
        } catch (IOException e) {
            throw new RuntimeException("UHOHHHHH FAILED TO CREATE DATA DIRECTORY", e);
        }

        return dataPath;
    }

    @Override
    public Path getRegionDirectory() {
        Path regionDir = getDataDirectory().resolve("regions");

        try {
            Files.createDirectories(regionDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create region directory", e);
        }

        return regionDir;
    }
}
