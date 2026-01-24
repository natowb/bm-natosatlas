package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.wrapper.WorldWrapper;
import net.minecraft.world.World;

public class WorldWrapperML implements WorldWrapper {

    private final World world;
    private final String worldSaveName;

    public WorldWrapperML(World world, String worldSaveName) {
        this.world = world;
        this.worldSaveName = worldSaveName;
    }

    @Override
    public String getName() {
        return world.getProperties().getName();
    }

    @Override
    public String getSaveName() {
        return worldSaveName;
    }

    @Override
    public long getTime() {
        return world.getTime();
    }

    @Override
    public long getSeed() {
        return world.getSeed();
    }

    @Override
    public int getDimensionId() {
        return world.dimension.id;
    }

    @Override
    public boolean isServer() {
        return world.isRemote;
    }
}
