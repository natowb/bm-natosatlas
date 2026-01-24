package dev.natowb.natosatlas.core.wrapper;

public interface WorldWrapper {
    String getName();
    String getSaveName();
    long getTime();
    long getSeed();
    int getDimensionId();
    boolean isServer();
}