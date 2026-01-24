package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.wrapper.WorldWrapper;
import net.minecraft.core.world.World;

public class WorldWrapperBTA implements WorldWrapper {
    private final World world;
    private final String worldSaveName;

    public WorldWrapperBTA(World world, String worldSaveName) {
        this.world = world;
        this.worldSaveName = worldSaveName;
    }

    @Override
    public String getName() {
        return world.getLevelData().getWorldName();
    }

    @Override
    public String getSaveName() {
        return worldSaveName;
    }

    @Override
    public long getTime() {
        return world.getWorldTime();
    }

    @Override
    public long getSeed() {
        return world.getRandomSeed();
    }

    @Override
    public int getDimensionId() {
        return world.dimension.id;
    }

    @Override
    public boolean isServer() {
        return world.isClientSide;
    }
}