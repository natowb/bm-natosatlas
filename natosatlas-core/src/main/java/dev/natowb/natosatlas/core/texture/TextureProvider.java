package dev.natowb.natosatlas.core.texture;

import dev.natowb.natosatlas.core.data.NACoord;
import dev.natowb.natosatlas.core.io.LogUtil;
import dev.natowb.natosatlas.core.layers.MapLayer;
import dev.natowb.natosatlas.core.layers.MapLayerHandler;
import dev.natowb.natosatlas.core.map.NARegionCache;
import dev.natowb.natosatlas.core.map.NARegionPixelData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

public final class TextureProvider {

    public static final int ICON_SLIME_ENABLED = 0;
    public static final int ICON_SLIME_DISABLED = 1;

    public static final int ICON_GRID_ENABLED = 2;
    public static final int ICON_GRID_DISABLED = 3;

    public static final int ICON_DAY = 4;
    public static final int ICON_NIGHT = 5;
    public static final int ICON_CAVE = 6;
    public static final int ICON_AUTO = 7;

    public static final int ICON_COG = 8;
    public static final int ICON_HELP = 9;
    public static final int ICON_WAYPOINTS = 10;

    public static final int ICON_BACK = 11;
    public static final int ICON_FORWARD = 12;

    public static final int ICON_CROSS = 13;
    public static final int ICON_CHECK = 14;
    public static final int ICON_PLUS = 15;
    public static final int ICON_MINUS = 16;

    public static final int ICON_EYE_OPEN = 17;
    public static final int ICON_EYE_CLOSED = 18;

    public static final int ICON_ENTITY_PLAYER = 19;
    public static final int ICON_ENTITY_ALL = 20;
    public static final int ICON_ENTITY_NONE = 21;


    private TextureProvider() {
    }


    private static int iconsTextureId = -1;

    public static int getIconTexture() {
        if (iconsTextureId == -1) {
            try {
                BufferedImage img = ImageIO.read(Objects.requireNonNull(TextureProvider.class.getClassLoader().getResourceAsStream("assets/natosatlas/textures/ui.png")));
                iconsTextureId = TextureUtils.createTextureFromBufferedImage(img);
            } catch (Exception e) {
                LogUtil.error("Failed to load icons.png texture!, error={}", e);
                return -1;
            }
        }
        return iconsTextureId;
    }

    public static int getTexture(NACoord coord) {
        MapLayer layer = MapLayerHandler.get().getActiveLayer();
        NARegionPixelData region = NARegionCache.get().getRegion(layer.id, coord);
        if (region == null) return -1;
        region.updateTexture();
        return region.getTextureId();
    }
}
