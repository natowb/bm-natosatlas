package dev.natowb.natosatlas.core.data;

import java.io.File;

public class NAWorldInfo {
    public final String worldName;
    public final boolean isServer;
    public final long worldTime;
    public final int worldDimension;
    public final File worldDirectory;


    public NAWorldInfo(String worldName, boolean isServer, long worldTime, int worldDimension, File worldDirectory) {
        this.worldName = worldName;
        this.isServer = isServer;
        this.worldTime = worldTime;
        this.worldDimension = worldDimension;
        this.worldDirectory = worldDirectory;
    }
}
