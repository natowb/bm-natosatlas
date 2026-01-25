package dev.natowb.natosatlas.core.texture;

import dev.natowb.natosatlas.core.NatosAtlasCore;
import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.layers.MapLayer;
import dev.natowb.natosatlas.core.map.MapRegion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

public final class TextureProvider {

    private int iconsTextureId = -1;

    public int getIconTexture() {
        if (iconsTextureId == -1) {
            try {
                BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("assets/natosatlas/textures/ui.png")));
                iconsTextureId = TextureUtils.createTextureFromBufferedImage(img);
            } catch (Exception e) {
                LogUtil.error("Failed to load icons.png texture!, error={}", e);
                return -1;
            }
        }
        return iconsTextureId;
    }

    public int getTexture(NACoord coord) {
        MapLayer layer = NatosAtlasCore.get().layers.getActiveLayer();
        MapRegion region = NatosAtlasCore.get().cache.getRegion(layer.id, coord);
        if (region == null) return -1;

        region.updateTexture();
        return region.getTextureId();
    }
}
