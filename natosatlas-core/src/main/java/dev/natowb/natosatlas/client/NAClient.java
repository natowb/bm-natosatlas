package dev.natowb.natosatlas.client;

import dev.natowb.natosatlas.client.cache.NARegionPixelCache;
import dev.natowb.natosatlas.client.platform.ClientPlatform;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.client.cache.NARegionTextureCache;
import dev.natowb.natosatlas.client.io.MapSaver;
import dev.natowb.natosatlas.client.platform.NAPainter;
import dev.natowb.natosatlas.client.ui.elements.UIScaleInfo;
import dev.natowb.natosatlas.client.ui.hud.MinimapRenderer;
import dev.natowb.natosatlas.client.ui.screens.map.*;
import dev.natowb.natosatlas.client.ui.screens.settings.Settings;
import dev.natowb.natosatlas.client.ui.themes.UITheme;
import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NAClientSession;
import dev.natowb.natosatlas.core.NAConstants;
import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NALayer;
import dev.natowb.natosatlas.core.data.NAWorldInfo;
import dev.natowb.natosatlas.core.util.LogUtil;
import dev.natowb.natosatlas.client.ui.screens.waypoint.Waypoint;
import dev.natowb.natosatlas.client.ui.screens.waypoint.Waypoints;
import org.lwjgl.opengl.GL11;

import java.util.Set;

public class NAClient implements NAClientSession {

    private static NAClient instance;

    private final MapUpdater mapUpdater = MapUpdater.get();
    private final MapSaver mapSaver = MapSaver.get();

    public static NAClient get() {
        return instance;
    }

    private boolean inWorld;
    private String offlineSaveName;
    private String serverName;
    private int dim;
    private final ClientPlatform platform;

    public NAClient(ClientPlatform platform) {
        if (instance != null) {
            LogUtil.error("tried to create NAClient when one already exists");
            throw new RuntimeException();
        }

        NAClient.instance = this;
        this.platform = platform;

        LayerRegistry.getLayers().add(new NALayer(2, "Cave", new NAChunkBuilderCave(), true, false));
        Settings.load();

        LogUtil.setLoggingLevel(LogUtil.LogLevel.INFO);
    }

    public ClientPlatform getPlatform() {
        return platform;
    }

    @Override
    public void tick() {
        NAWorldInfo worldInfo = ClientWorldAccess.get().getWorldInfo();
        boolean worldExists = worldInfo != null;

        if (!worldExists && inWorld) {
            inWorld = false;
            onWorldLeft();
            return;
        }


        if (worldExists && !inWorld) {
            if (worldInfo.isMultiplayer()) {
                if (serverName != null) {
                    inWorld = true;
                    dim = ClientWorldAccess.get().getWorldInfo().getDimensionId();
                    onWorldJoined(serverName, dim, true);
                }
            } else {
                offlineSaveName = ClientWorldAccess.get().getOfflineSaveName();
                if (offlineSaveName != null) {
                    inWorld = true;
                    dim = ClientWorldAccess.get().getWorldInfo().getDimensionId();
                    onWorldJoined(offlineSaveName, dim, false);
                }
            }
            return;
        }

        if (!inWorld) return;

        int currentDim = ClientWorldAccess.get().getWorldInfo().getDimensionId();
        if (dim != currentDim) {
            dim = currentDim;
            onDimensionChange(dim);
            return;
        }
        onWorldTick();
    }

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    private void onWorldJoined(String worldSaveName, int dim, boolean isMultiplayerWorld) {
        LogUtil.info("[Client] Joined world (world={} dim={})", worldSaveName, dim);

        NAPaths.setWorldPaths(worldSaveName, isMultiplayerWorld);

        Waypoints.load();
        mapSaver.start();
    }

    private void onWorldLeft() {
        LogUtil.info("[Client] Left world (world={})", offlineSaveName);

        mapSaver.stop();
        NARegionPixelCache.get().clear();
    }

    private void onDimensionChange(int newDim) {
        LogUtil.info("[Client] Changed dimension (dim={newDim})", newDim);
        NARegionPixelCache.get().clear();
    }

    private void onWorldTick() {
        mapUpdater.tick();
        mapSaver.saveNextBatch();
        if (Settings.minimapEnabled)
            minimapRenderer.tick();
    }

    private final MinimapRenderer minimapRenderer = new MinimapRenderer();

    public void renderGui(UIScaleInfo scaleInfo) {
        if (Settings.minimapEnabled)
            minimapRenderer.render(scaleInfo);
    }
}
