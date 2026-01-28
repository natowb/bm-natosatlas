package dev.natowb.natosatlas.core.data;

public class NAWorldInfo {

    private final int worldHeight;
    private final String name;
    private final long time;
    private final long seed;
    private final int dimensionId;
    private final boolean hasCeiling;
    private final boolean multiplayer;

    public NAWorldInfo(
            int worldHeight,
            String name,
            long time,
            long seed,
            int dimensionId,
            boolean hasCeiling,
            boolean multiplayer
    ) {
        this.worldHeight = worldHeight;
        this.name = name;
        this.time = time;
        this.seed = seed;
        this.dimensionId = dimensionId;
        this.hasCeiling = hasCeiling;
        this.multiplayer = multiplayer;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public long getSeed() {
        return seed;
    }

    public int getDimensionId() {
        return dimensionId;
    }

    public boolean hasCeiling() {
        return hasCeiling;
    }

    public boolean isMultiplayer() {
        return multiplayer;
    }
}
