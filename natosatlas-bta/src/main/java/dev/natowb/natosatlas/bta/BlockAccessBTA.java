package dev.natowb.natosatlas.bta;

import dev.natowb.natosatlas.core.wrapper.BlockAccess;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.MaterialColor;

public class BlockAccessBTA extends BlockAccess {
    @Override
    public int getColor(int blockId, int blockMeta) {
        Block<?> block = Blocks.getBlock(blockId);
        if (block == null) {
            return MaterialColor.getColorFromIndex(MaterialColor.none.id);
        }
        if (block.getMaterialColor() == null) return 0xFF00FF00;

        assert Blocks.getBlock(blockId) != null;
        return MaterialColor.getColorFromIndex(Blocks.getBlock(blockId).getMaterialColor().id);
    }

    @Override
    public boolean isGrass(int blockId) {
        return false;
    }

    @Override
    public boolean isFluid(int blockId) {
        Block<?> block = Blocks.getBlock(blockId);
        if (block == null) return false;
        if (block.getMaterial() == null) return false;
        return block.getMaterial().isLiquid();
    }
}
