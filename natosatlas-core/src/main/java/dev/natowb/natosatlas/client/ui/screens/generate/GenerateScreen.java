package dev.natowb.natosatlas.client.ui.screens.generate;

import dev.natowb.natosatlas.client.MapUpdater;
import dev.natowb.natosatlas.client.NAClient;
import dev.natowb.natosatlas.client.platform.ClientWorldAccess;
import dev.natowb.natosatlas.client.platform.NAPainter;
import dev.natowb.natosatlas.client.ui.elements.UIScaleInfo;
import dev.natowb.natosatlas.client.ui.elements.UIElementButton;
import dev.natowb.natosatlas.client.ui.elements.UIScreen;
import dev.natowb.natosatlas.client.ui.screens.map.*;
import dev.natowb.natosatlas.client.ui.themes.UITheme;
import dev.natowb.natosatlas.core.NAConstants;
import dev.natowb.natosatlas.core.NAPaths;
import dev.natowb.natosatlas.core.chunk.ChunkWrapper;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.data.NARegionFile;
import dev.natowb.natosatlas.core.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GenerateScreen extends UIScreen {

    private int current = 0;
    private int max = 100;
    private int delayTicks = 0;

    private List<NACoord> pendingChunks = null;
    private NARegionFile currentRegion = null;

    private final MapViewport viewport = new MapViewport();
    private final MapStageRegions regionPainter = new MapStageRegions();

    List<NARegionFile> regions;

    public GenerateScreen(UIScreen parent) {
        super(parent);

        ClientWorldAccess access = ClientWorldAccess.get();
        if (access == null || access.getWorldInfo() == null) {
            LogUtil.warn("No world loaded, cannot scan regions");
            return;
        }

        File worldDir = NAPaths.getWorldSavePath().toFile();
        regions = access.getRegionFiles(ClientWorldAccess.get().getDimDirectory(worldDir));

        if (regions.isEmpty()) {
            LogUtil.info("No region files found for client world");
            return;
        }

        LogUtil.info("Found {} region files", regions.size());
        max = regions.size();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);

        viewport.initViewport(0, 0, width, height);
        viewport.setZoom(MapConfig.MIN_ZOOM);

        UIElementButton cancelButton = new UIElementButton(200, width / 2 - 50, height - 30, 100, 20, "Cancel");

        cancelButton.setHandler(btn -> {
            NAClient.get().getPlatform().screen.openNacScreen(parent);
        });

        addButton(cancelButton);
    }

    @Override
    public void tick() {
        super.tick();

        if (regions == null || regions.isEmpty()) return;

        if (current >= regions.size()) {
            NAClient.get().getPlatform().screen.openNacScreen(new MapScreen(null));
            return;
        }

        if (delayTicks > 0) {
            delayTicks--;
            return;
        }
        delayTicks = 1;

        if (pendingChunks == null) {
            processNextRegion();
        }

        processChunkBatch();
    }

    private void processNextRegion() {
        currentRegion = regions.get(current);

        float centerX = currentRegion.regionCoord.x * NAConstants.PIXELS_PER_CANVAS_REGION
                + (NAConstants.PIXELS_PER_CANVAS_REGION / 2f);
        float centerZ = currentRegion.regionCoord.z * NAConstants.PIXELS_PER_CANVAS_REGION
                + (NAConstants.PIXELS_PER_CANVAS_REGION / 2f);
        viewport.centerOn(centerX, centerZ);

        pendingChunks = new ArrayList<>();
        for (NACoord c : currentRegion.iterateExistingChunks()) {
            pendingChunks.add(c);
        }
    }

    private void processChunkBatch() {
        if (pendingChunks == null || pendingChunks.isEmpty()) {
            current++;
            pendingChunks = null;
            return;
        }

        int batch = Math.min(128, pendingChunks.size());
        for (int i = 0; i < batch; i++) {
            NACoord chunkCoord = pendingChunks.remove(0);

            ChunkWrapper wrapper = ClientWorldAccess.get().getChunkFromDisk(
                    chunkCoord,
                    regions.get(current).file.getParentFile().getParentFile()
            );

            if (wrapper != null) {
                MapUpdater.get().updateChunk(chunkCoord, wrapper);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta, UIScaleInfo scaleInfo) {
        NAPainter p = NAClient.get().getPlatform().painter;
        p.drawRect(0, 0, width, height, UITheme.PANEL_BG);

        viewport.begin(scaleInfo);

        MapContext ctx = viewport.getContext();
        Set<Long> visible = viewport.computeVisibleRegions();

        regionPainter.draw(ctx, visible, 0);

        if (currentRegion != null) {
            int rx = currentRegion.regionCoord.x;
            int rz = currentRegion.regionCoord.z;

            int px = (rx * NAConstants.PIXELS_PER_CANVAS_REGION);
            int pz = (rz * NAConstants.PIXELS_PER_CANVAS_REGION);
            int size = NAConstants.PIXELS_PER_CANVAS_REGION;

            int fillColor = 0x6000AAFF;
            int outlineThickness = 6;
            int outlineColor = 0xFF00AAFF;
            p.drawRect(px + size, pz + size, px, pz, fillColor);

            p.drawRect(px + size, pz + outlineThickness, px, pz, outlineColor);
            p.drawRect(px + size, pz + size, px, pz + size - outlineThickness, outlineColor);
            p.drawRect(px + outlineThickness, pz + size, px, pz, outlineColor);
            p.drawRect(px + size, pz + size, px + size - outlineThickness, pz, outlineColor);
        }

        viewport.end();

        float percent = (float) current / (float) max;
        int barWidth = 200;
        int barHeight = 20;

        int barX = width / 2 - barWidth / 2;
        int barY = height - 70;

        p.drawRect(barX, barY, barX + barWidth, barY + barHeight, 0xAA000000);

        int fill = (int) (barWidth * percent);
        p.drawRect(barX, barY, barX + fill, barY + barHeight, 0xFF00AAFF);

        String text = current + " / " + max;
        p.drawCenteredString(text, width / 2, barY + 6, 0xFFFFFFFF);

        super.render(mouseX, mouseY, delta, scaleInfo);
    }
}
