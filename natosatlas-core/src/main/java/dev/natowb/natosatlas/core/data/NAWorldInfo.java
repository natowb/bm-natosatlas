package dev.natowb.natosatlas.core.data;

public class NAWorldInfo {
    public final String worldName;
    public final boolean isServer;
    public final long worldTime;
    public final int worldDimension;
    public final long worldSeed;


    public NAWorldInfo(String worldName, boolean isServer, long worldTime, int worldDimension, long seed) {
        this.worldName = worldName;
        this.isServer = isServer;
        this.worldTime = worldTime;
        this.worldDimension = worldDimension;
        this.worldSeed = seed;
    }
}
