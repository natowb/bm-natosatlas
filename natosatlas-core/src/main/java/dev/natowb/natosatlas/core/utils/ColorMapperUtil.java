package dev.natowb.natosatlas.core.utils;

import java.util.HashMap;

public class ColorMapperUtil {

    private static final HashMap<String, Integer> COLOR_OVERRIDES = new HashMap<String, Integer>();


    static {
        COLOR_OVERRIDES.put("tile.gravel", 0xFF9A9A9A);
        COLOR_OVERRIDES.put("tile.hellrock", 0xFF7A2A2A);
        COLOR_OVERRIDES.put("tile.lightgem", 0xFFFFA500);
    }

    private static final int[] WOOL_COLORS = new int[16];

    static {
        WOOL_COLORS[0] = 0xFFFFFFFF; // White
        WOOL_COLORS[1] = 0xFFFFA500; // Orange
        WOOL_COLORS[2] = 0xFFFF00FF; // Magenta
        WOOL_COLORS[3] = 0xFFADD8E6; // Light Blue
        WOOL_COLORS[4] = 0xFFFFFF00; // Yellow
        WOOL_COLORS[5] = 0xFF00FF00; // Lime
        WOOL_COLORS[6] = 0xFFFFC0CB; // Pink
        WOOL_COLORS[7] = 0xFF808080; // Gray
        WOOL_COLORS[8] = 0xFFD3D3D3; // Light Gray
        WOOL_COLORS[9] = 0xFF00FFFF; // Cyan
        WOOL_COLORS[10] = 0xFF800080; // Purple
        WOOL_COLORS[11] = 0xFF0000FF; // Blue
        WOOL_COLORS[12] = 0xFFA52A2A; // Brown
        WOOL_COLORS[13] = 0xFF008000; // Green
        WOOL_COLORS[14] = 0xFFFF0000; // Red
        WOOL_COLORS[15] = 0xFF000000; // Black
    }

    public static int getOverrideColor(String blockTranslationKey) {
        return COLOR_OVERRIDES.getOrDefault(blockTranslationKey, -1);
    }

    public static int getWoolColor(int meta) {
        if (meta < 0 || meta > WOOL_COLORS.length) {
            return 0xFFFF00FF;
        }

        return WOOL_COLORS[meta];
    }
}
