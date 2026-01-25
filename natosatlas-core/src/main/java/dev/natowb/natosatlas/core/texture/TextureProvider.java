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

    public static final int ICON_SLIME_ACTIVE = 0;
    public static final int ICON_SLIME_DISABLED = 1;
    public static final int ICON_WAYPOINTS = 2;
    public static final int ICON_COG = 3;
    public static final int ICON_CROSS = 4;
    public static final int ICON_DAY = 5;
    public static final int ICON_NIGHT = 6;
    public static final int ICON_CAVE = 7;
    public static final int ICON_AUTO = 8;
    public static final int ICON_EYE_OPEN = 9;
    public static final int ICON_EYE_CLOSED = 10;
    public static final int ICON_HELP = 11;
    public static final int ICON_PLUS = 12;
    public static final int ICON_BACK = 13;
    public static final int ICON_CHECK = 14;
    public static final int ICON_MENU_DOWN = 15;
    public static final int ICON_MENU_UP = 16;


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
