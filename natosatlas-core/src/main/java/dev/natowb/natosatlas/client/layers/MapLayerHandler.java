package dev.natowb.natosatlas.client.layers;

import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.chunk.ChunkCaveRenderer;
import dev.natowb.natosatlas.core.chunk.ChunkSurfaceRenderer;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.client.settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class MapLayerHandler {


    private static MapLayerHandler instance;

    public static MapLayerHandler get() {
        if (instance == null) {
            instance = new MapLayerHandler();
        }
        return instance;
    }

    private final List<MapLayer> layers = new ArrayList<>();
    private int activeLayer = 0;

    private MapLayerHandler() {
        ChunkSurfaceRenderer surface = new ChunkSurfaceRenderer();
        layers.add(new MapLayer(0, "Day", surface, false));
        layers.add(new MapLayer(1, "Night", surface, true));
        layers.add(new MapLayer(2, "Cave", new ChunkCaveRenderer(), true));
    }

    public void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            activeLayer = index;
        }
    }

    public MapLayer getActiveLayer() {
        return layers.get(activeLayer);
    }

    public List<MapLayer> getLayers() {
        return layers;
    }

    public void tick() {

        if (NACore.getClient().getPlatform().world.hasCeiling()) {
            setActiveLayer(2);
            return;
        }

        switch (Settings.mapRenderMode) {
            case Day:
                setActiveLayer(0);
                break;
            case Night:
                setActiveLayer(1);
                break;
            case Cave:
                setActiveLayer(2);
                break;
            case Auto:
                autoSelectLayer();
                break;
        }
    }

    private void autoSelectLayer() {
        long time = NACore.getClient().getPlatform().world.getTime() % 24000L;
        boolean day = time < 12000L;

        NAEntity player = NACore.getClient().getPlatform().world.getPlayer();
        ChunkWrapper playerChunk = NACore.getClient().getPlatform().world.getChunk(NACoord.from(player.chunkX, player.chunkZ));
        int skyLight = playerChunk.getSkyLight(player.localX, ((int) Math.floor(player.y)), player.localZ);

        if (skyLight == 0) {
            setActiveLayer(2);
        } else {
            setActiveLayer(day ? 0 : 1);
        }
    }
}
