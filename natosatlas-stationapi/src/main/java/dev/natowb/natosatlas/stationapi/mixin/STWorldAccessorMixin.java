package dev.natowb.natosatlas.stationapi.mixin;

import net.minecraft.world.World;
import net.minecraft.world.storage.WorldStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface STWorldAccessorMixin {
    @Accessor("storage")
    WorldStorage getStorage();
}
