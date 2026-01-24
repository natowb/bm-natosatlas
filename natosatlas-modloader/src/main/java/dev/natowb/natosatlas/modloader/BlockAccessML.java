package dev.natowb.natosatlas.modloader;

import dev.natowb.natosatlas.core.utils.ColorMapperUtil;
import dev.natowb.natosatlas.core.wrapper.BlockAccess;
import net.minecraft.block.Block;

public class BlockAccessML extends BlockAccess {
    @Override
    public int getColor(int blockId, int blockMeta) {

        int overrideColor = ColorMapperUtil.getOverrideColor(Block.BLOCKS[blockId].getTranslationKey());

        if (overrideColor != -1) {
            return overrideColor;
        }

        if (blockId == Block.WOOL.id) {
            return ColorMapperUtil.getWoolColor(blockMeta);
        }

        if (blockId == Block.LEAVES.id) {
            return Block.LEAVES.getColor(blockMeta);
        }

        if (Block.BLOCKS[blockId].material == null) {
            return 0xFF800080;
        }
        return Block.BLOCKS[blockId].material.mapColor.color;
    }

    @Override
    public boolean isGrass(int blockId) {
        return blockId == Block.GRASS_BLOCK.id;
    }


    @Override
    public boolean isFluid(int blockId) {
        Block block = Block.BLOCKS[blockId];
        if (block == null) {
            return false;
        }

        if (block.material == null) {
            return false;
        }

        return block.material.isFluid();
    }

}
