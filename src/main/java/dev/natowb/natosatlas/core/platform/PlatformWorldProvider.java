package dev.natowb.natosatlas.core.platform;

import dev.natowb.natosatlas.core.map.MapBiome;

public interface PlatformWorldProvider {

    String getName();

    boolean isRemote();

    int getDimension();

    boolean isDaytime();

    void generateExistingChunks();

    MapBiome getBiome(int blockX, int blockZ);
}
