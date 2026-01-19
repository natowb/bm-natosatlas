package dev.natowb.natosatlas.core.utils;

public class Constants {
    public static final int BLOCKS_PER_MINECRAFT_CHUNK = 16;
    public static final int CHUNKS_PER_MINECRAFT_REGION = 32;
    public static final int PIXELS_PER_CANVAS_UNIT = 8;
    public static final int PIXELS_PER_CANVAS_CHUNK = BLOCKS_PER_MINECRAFT_CHUNK * PIXELS_PER_CANVAS_UNIT;
    public static final int BLOCKS_PER_CANVAS_REGION = CHUNKS_PER_MINECRAFT_REGION * BLOCKS_PER_MINECRAFT_CHUNK;
    public static final int PIXELS_PER_CANVAS_REGION = BLOCKS_PER_CANVAS_REGION * PIXELS_PER_CANVAS_UNIT;
}
