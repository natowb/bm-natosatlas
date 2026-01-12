package dev.natowb.natosatlas.core.models;

public class NacWorldInfo {
    public final String name;
    public final boolean isPlayerInOverworld;

    public NacWorldInfo(String name, boolean isPlayerInOverworld) {
        this.name = name;
        this.isPlayerInOverworld = isPlayerInOverworld;
    }
}
