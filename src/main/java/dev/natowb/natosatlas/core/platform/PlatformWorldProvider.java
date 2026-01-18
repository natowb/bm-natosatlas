package dev.natowb.natosatlas.core.platform;

public interface PlatformWorldProvider {

    String getName();

    boolean isRemote();

    int getDimension();

    boolean isDaytime();
}
