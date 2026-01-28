package dev.natowb.natosatlas.client.map;

import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.settings.Settings;
import dev.natowb.natosatlas.core.LayerRegistry;
import dev.natowb.natosatlas.core.NACore;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NAEntity;
import dev.natowb.natosatlas.core.data.NALayer;

public class MapLayerController {

    private int activeLayer = 0;

    public int getActiveLayer() {
        return activeLayer;
    }

    public NALayer getLayer() {
        return LayerRegistry.get(activeLayer);
    }

    public void setActiveLayer(int index) {
        if (index >= 0 && index < LayerRegistry.getLayers().size()) {
            activeLayer = index;
        }
    }

    public void tick() {
        if (NAClient.get().getPlatform().world.hasCeiling()) {
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
            default:
                break;
        }
    }

    private void autoSelectLayer() {
        long time = NAClient.get().getPlatform().world.getTime() % 24000L;
        boolean day = time < 12000L;

        NAEntity player = NAClient.get().getPlatform().world.getPlayer();
        ChunkWrapper chunk = NAClient.get().getPlatform().world.getChunk(
                NACoord.from(player.chunkX, player.chunkZ)
        );

        int skyLight = chunk.getSkyLight(
                player.localX,
                (int) Math.floor(player.y),
                player.localZ
        );

        if (skyLight == 0) {
            setActiveLayer(2);
        } else {
            setActiveLayer(day ? 0 : 1);
        }
    }
}
