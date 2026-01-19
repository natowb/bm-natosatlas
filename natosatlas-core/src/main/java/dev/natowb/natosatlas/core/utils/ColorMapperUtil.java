package dev.natowb.natosatlas.core.utils;

public class ColorMapperUtil {

    public static final int BLOCK_AIR_ID = 0;
    public static final int BLOCK_STONE_ID = 1;
    public static final int BLOCK_GRASS_ID = 2;
    public static final int BLOCK_DIRT_ID = 3;
    public static final int BLOCK_COBBLESTONE_ID = 4;
    public static final int BLOCK_PLANKS_ID = 5;
    public static final int BLOCK_SAPLING_ID = 6;
    public static final int BLOCK_BEDROCK_ID = 7;
    public static final int BLOCK_WATER_MOVING_ID = 8;
    public static final int BLOCK_WATER_STILL_ID = 9;
    public static final int BLOCK_LAVA_MOVING_ID = 10;
    public static final int BLOCK_LAVA_STILL_ID = 11;
    public static final int BLOCK_SAND_ID = 12;
    public static final int BLOCK_GRAVEL_ID = 13;
    public static final int BLOCK_ORE_GOLD_ID = 14;
    public static final int BLOCK_ORE_IRON_ID = 15;
    public static final int BLOCK_ORE_COAL_ID = 16;
    public static final int BLOCK_WOOD_ID = 17;
    public static final int BLOCK_LEAVES_ID = 18;
    public static final int BLOCK_SPONGE_ID = 19;
    public static final int BLOCK_GLASS_ID = 20;
    public static final int BLOCK_ORE_LAPIS_ID = 21;
    public static final int BLOCK_BLOCK_LAPIS_ID = 22;
    public static final int BLOCK_DISPENSER_ID = 23;
    public static final int BLOCK_SANDSTONE_ID = 24;
    public static final int BLOCK_MUSIC_BLOCK_ID = 25;
    public static final int BLOCK_BED_ID = 26;
    public static final int BLOCK_RAIL_POWERED_ID = 27;
    public static final int BLOCK_RAIL_DETECTOR_ID = 28;
    public static final int BLOCK_PISTON_STICKY_BASE_ID = 29;
    public static final int BLOCK_WEB_ID = 30;
    public static final int BLOCK_TALL_GRASS_ID = 31;
    public static final int BLOCK_DEAD_BUSH_ID = 32;
    public static final int BLOCK_PISTON_BASE_ID = 33;
    public static final int BLOCK_PISTON_EXTENSION_ID = 34;
    public static final int BLOCK_WOOL_ID = 35;
    public static final int BLOCK_PISTON_MOVING_ID = 36;
    public static final int BLOCK_FLOWER_YELLOW_ID = 37;
    public static final int BLOCK_FLOWER_RED_ID = 38;
    public static final int BLOCK_MUSHROOM_BROWN_ID = 39;
    public static final int BLOCK_MUSHROOM_RED_ID = 40;
    public static final int BLOCK_BLOCK_GOLD_ID = 41;
    public static final int BLOCK_BLOCK_IRON_ID = 42;
    public static final int BLOCK_STONE_SLAB_DOUBLE_ID = 43;
    public static final int BLOCK_STONE_SLAB_SINGLE_ID = 44;
    public static final int BLOCK_BRICK_ID = 45;
    public static final int BLOCK_TNT_ID = 46;
    public static final int BLOCK_BOOKSHELF_ID = 47;
    public static final int BLOCK_COBBLESTONE_MOSSY_ID = 48;
    public static final int BLOCK_OBSIDIAN_ID = 49;
    public static final int BLOCK_TORCH_ID = 50;
    public static final int BLOCK_FIRE_ID = 51;
    public static final int BLOCK_MOB_SPAWNER_ID = 52;
    public static final int BLOCK_STAIRS_WOOD_ID = 53;
    public static final int BLOCK_CHEST_ID = 54;
    public static final int BLOCK_REDSTONE_WIRE_ID = 55;
    public static final int BLOCK_ORE_DIAMOND_ID = 56;
    public static final int BLOCK_BLOCK_DIAMOND_ID = 57;
    public static final int BLOCK_WORKBENCH_ID = 58;
    public static final int BLOCK_CROPS_ID = 59;
    public static final int BLOCK_FARMLAND_ID = 60;
    public static final int BLOCK_FURNACE_IDLE_ID = 61;
    public static final int BLOCK_FURNACE_ACTIVE_ID = 62;
    public static final int BLOCK_SIGN_POST_ID = 63;
    public static final int BLOCK_DOOR_WOOD_ID = 64;
    public static final int BLOCK_LADDER_ID = 65;
    public static final int BLOCK_RAIL_ID = 66;
    public static final int BLOCK_STAIRS_STONE_ID = 67;
    public static final int BLOCK_SIGN_WALL_ID = 68;
    public static final int BLOCK_LEVER_ID = 69;
    public static final int BLOCK_PRESSURE_PLATE_STONE_ID = 70;
    public static final int BLOCK_DOOR_IRON_ID = 71;
    public static final int BLOCK_PRESSURE_PLATE_WOOD_ID = 72;
    public static final int BLOCK_ORE_REDSTONE_ID = 73;
    public static final int BLOCK_ORE_REDSTONE_GLOWING_ID = 74;
    public static final int BLOCK_REDSTONE_TORCH_IDLE_ID = 75;
    public static final int BLOCK_REDSTONE_TORCH_ACTIVE_ID = 76;
    public static final int BLOCK_BUTTON_ID = 77;
    public static final int BLOCK_SNOW_ID = 78;
    public static final int BLOCK_ICE_ID = 79;
    public static final int BLOCK_SNOW_BLOCK_ID = 80;
    public static final int BLOCK_CACTUS_ID = 81;
    public static final int BLOCK_CLAY_ID = 82;
    public static final int BLOCK_REED_ID = 83;
    public static final int BLOCK_JUKEBOX_ID = 84;
    public static final int BLOCK_FENCE_ID = 85;
    public static final int BLOCK_PUMPKIN_ID = 86;
    public static final int BLOCK_NETHERRACK_ID = 87;
    public static final int BLOCK_SOUL_SAND_ID = 88;
    public static final int BLOCK_GLOWSTONE_ID = 89;
    public static final int BLOCK_PORTAL_ID = 90;
    public static final int BLOCK_PUMPKIN_LANTERN_ID = 91;
    public static final int BLOCK_CAKE_ID = 92;
    public static final int BLOCK_REDSTONE_REPEATER_IDLE_ID = 93;
    public static final int BLOCK_REDSTONE_REPEATER_ACTIVE_ID = 94;
    public static final int BLOCK_LOCKED_CHEST_ID = 95;
    public static final int BLOCK_TRAPDOOR_ID = 96;

    private static final int[] COLORS = new int[128];

    static {

        COLORS[BLOCK_AIR_ID] = 0x00000000;
        COLORS[BLOCK_STONE_ID] = 0x7A7A7A;
        COLORS[BLOCK_GRASS_ID] = 0x4CAF50;
        COLORS[BLOCK_DIRT_ID] = 0x8B5A2B;
        COLORS[BLOCK_COBBLESTONE_ID] = 0x6E6E6E;
        COLORS[BLOCK_PLANKS_ID] = 0xA07850;
        COLORS[BLOCK_SAPLING_ID] = 0x228B22;
        COLORS[BLOCK_BEDROCK_ID] = 0x4A4A4A;
        COLORS[BLOCK_WATER_MOVING_ID] = 0x3F76E4;
        COLORS[BLOCK_WATER_STILL_ID] = 0x3F76E4;
        COLORS[BLOCK_LAVA_MOVING_ID] = 0xFF4500;
        COLORS[BLOCK_LAVA_STILL_ID] = 0xFF4500;
        COLORS[BLOCK_SAND_ID] = 0xE8D8A0;
        COLORS[BLOCK_GRAVEL_ID] = 0x9A9A9A;
        COLORS[BLOCK_ORE_GOLD_ID] = 0xD4AF37;
        COLORS[BLOCK_ORE_IRON_ID] = 0xC0C0C0;
        COLORS[BLOCK_ORE_COAL_ID] = 0x2B2B2B;
        COLORS[BLOCK_WOOD_ID] = 0x8B6C42;
        COLORS[BLOCK_LEAVES_ID] = 0x3A8E3A;
        COLORS[BLOCK_SPONGE_ID] = 0xE2E27B;
        COLORS[BLOCK_GLASS_ID] = 0x80FFFFFF;
        COLORS[BLOCK_ORE_LAPIS_ID] = 0x1D3FA3;
        COLORS[BLOCK_BLOCK_LAPIS_ID] = 0x1E4ED8;
        COLORS[BLOCK_DISPENSER_ID] = 0x8A8A8A;
        COLORS[BLOCK_SANDSTONE_ID] = 0xE3D7A3;
        COLORS[BLOCK_MUSIC_BLOCK_ID] = 0x8B4513;
        COLORS[BLOCK_BED_ID] = 0xC04040;
        COLORS[BLOCK_RAIL_POWERED_ID] = 0xFFD700;
        COLORS[BLOCK_RAIL_DETECTOR_ID] = 0xAAAAAA;
        COLORS[BLOCK_PISTON_STICKY_BASE_ID] = 0x6E8B3D;
        COLORS[BLOCK_WEB_ID] = 0xFFFFFF;
        COLORS[BLOCK_TALL_GRASS_ID] = 0x4CAF50;
        COLORS[BLOCK_DEAD_BUSH_ID] = 0xA0522D;
        COLORS[BLOCK_PISTON_BASE_ID] = 0x7A7A7A;
        COLORS[BLOCK_PISTON_EXTENSION_ID] = 0x7A7A7A;
        COLORS[BLOCK_PISTON_MOVING_ID] = 0x7A7A7A;
        COLORS[BLOCK_FLOWER_YELLOW_ID] = 0xFFFF00;
        COLORS[BLOCK_FLOWER_RED_ID] = 0xFF0000;
        COLORS[BLOCK_MUSHROOM_BROWN_ID] = 0x8B4513;
        COLORS[BLOCK_MUSHROOM_RED_ID] = 0xFF4444;
        COLORS[BLOCK_BLOCK_GOLD_ID] = 0xFFD700;
        COLORS[BLOCK_BLOCK_IRON_ID] = 0xD8D8D8;
        COLORS[BLOCK_STONE_SLAB_DOUBLE_ID] = 0xB0B0B0;
        COLORS[BLOCK_STONE_SLAB_SINGLE_ID] = 0xB0B0B0;
        COLORS[BLOCK_BRICK_ID] = 0xB03030;
        COLORS[BLOCK_TNT_ID] = 0xFF0000;
        COLORS[BLOCK_BOOKSHELF_ID] = 0xA07850;
        COLORS[BLOCK_COBBLESTONE_MOSSY_ID] = 0x6E8E6E;
        COLORS[BLOCK_OBSIDIAN_ID] = 0x1A0F2A;
        COLORS[BLOCK_TORCH_ID] = 0xFFD37F;
        COLORS[BLOCK_FIRE_ID] = 0xFFA500;
        COLORS[BLOCK_MOB_SPAWNER_ID] = 0x3A3A6A;
        COLORS[BLOCK_STAIRS_WOOD_ID] = 0xA07850;
        COLORS[BLOCK_CHEST_ID] = 0xB8860B;
        COLORS[BLOCK_REDSTONE_WIRE_ID] = 0xAA0000;
        COLORS[BLOCK_ORE_DIAMOND_ID] = 0x4ED6D6;
        COLORS[BLOCK_BLOCK_DIAMOND_ID] = 0x55FFFF;
        COLORS[BLOCK_WORKBENCH_ID] = 0x8B6C42;
        COLORS[BLOCK_CROPS_ID] = 0x4CAF50;
        COLORS[BLOCK_FARMLAND_ID] = 0x6B4423;
        COLORS[BLOCK_FURNACE_IDLE_ID] = 0x5A5A5A;
        COLORS[BLOCK_FURNACE_ACTIVE_ID] = 0xFF6A00;
        COLORS[BLOCK_SIGN_POST_ID] = 0xC2A878;
        COLORS[BLOCK_DOOR_WOOD_ID] = 0xA07850;
        COLORS[BLOCK_LADDER_ID] = 0xC2A878;
        COLORS[BLOCK_RAIL_ID] = 0xAAAAAA;
        COLORS[BLOCK_STAIRS_STONE_ID] = 0x7A7A7A;
        COLORS[BLOCK_SIGN_WALL_ID] = 0xC2A878;
        COLORS[BLOCK_LEVER_ID] = 0xAAAAAA;
        COLORS[BLOCK_PRESSURE_PLATE_STONE_ID] = 0x7A7A7A;
        COLORS[BLOCK_DOOR_IRON_ID] = 0xD8D8D8;
        COLORS[BLOCK_PRESSURE_PLATE_WOOD_ID] = 0xA07850;
        COLORS[BLOCK_ORE_REDSTONE_ID] = 0x8B0000;
        COLORS[BLOCK_ORE_REDSTONE_GLOWING_ID] = 0xFF0000;
        COLORS[BLOCK_REDSTONE_TORCH_IDLE_ID] = 0xAA0000;
        COLORS[BLOCK_REDSTONE_TORCH_ACTIVE_ID] = 0xFF0000;
        COLORS[BLOCK_BUTTON_ID] = 0xAAAAAA;
        COLORS[BLOCK_SNOW_ID] = 0xFFFFFF;
        COLORS[BLOCK_ICE_ID] = 0xA0D8FF;
        COLORS[BLOCK_SNOW_BLOCK_ID] = 0xFFFFFF;
        COLORS[BLOCK_CACTUS_ID] = 0x2E8B57;
        COLORS[BLOCK_CLAY_ID] = 0xA0B8C8;
        COLORS[BLOCK_REED_ID] = 0x4CAF50;
        COLORS[BLOCK_JUKEBOX_ID] = 0x8B4513;
        COLORS[BLOCK_FENCE_ID] = 0xA07850;
        COLORS[BLOCK_PUMPKIN_ID] = 0xFF8C00;
        COLORS[BLOCK_NETHERRACK_ID] = 0x7A2A2A;
        COLORS[BLOCK_SOUL_SAND_ID] = 0xA08060;
        COLORS[BLOCK_GLOWSTONE_ID] = 0xFFEB8C;
        COLORS[BLOCK_PORTAL_ID] = 0x551A8B;
        COLORS[BLOCK_PUMPKIN_LANTERN_ID] = 0xFFA500;
        COLORS[BLOCK_CAKE_ID] = 0xFFF0F0;
        COLORS[BLOCK_REDSTONE_REPEATER_IDLE_ID] = 0xAA0000;
        COLORS[BLOCK_REDSTONE_REPEATER_ACTIVE_ID] = 0xFF0000;
        COLORS[BLOCK_LOCKED_CHEST_ID] = 0xB8860B;
        COLORS[BLOCK_TRAPDOOR_ID] = 0xA07850;
        COLORS[BLOCK_WOOL_ID] = 0xFFFF0000;
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

    public static int getWoolColor(int meta) {
        if (meta < 0 || meta > WOOL_COLORS.length) {
            return 0xFFFF00FF;
        }

        return WOOL_COLORS[meta];
    }


    public static int get(int blockId, int meta) {
        if (blockId < 0 || blockId >= COLORS.length)
            return 0xFFFF00FF;

        if (blockId == BLOCK_WOOL_ID) {
            return getWoolColor(meta);
        }

        return COLORS[blockId];
    }
}
