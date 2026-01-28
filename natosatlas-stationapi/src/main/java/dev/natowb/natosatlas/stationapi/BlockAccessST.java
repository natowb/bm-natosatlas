package dev.natowb.natosatlas.stationapi;

import dev.natowb.natosatlas.core.mapper.ColorMapper;
import dev.natowb.natosatlas.client.access.ClientBlockAccess;
import net.minecraft.block.Block;

public class BlockAccessST extends ClientBlockAccess {

    public BlockAccessST() {
        registerBlockIdentifier(BlockIdentifier.GLASS, Block.GLASS.id);
        registerBlockIdentifier(BlockIdentifier.GRASS, Block.GRASS_BLOCK.id);
        registerBlockIdentifier(BlockIdentifier.SNOW, Block.SNOW.id);
    }

    @Override
    public int getColor(int blockId, int blockMeta) {

        int overrideColor = ColorMapper.getOverrideColor(Block.BLOCKS[blockId].getTranslationKey());

        if (overrideColor != -1) {
            return overrideColor;
        }

        if (blockId == Block.WOOL.id) {
            return ColorMapper.getWoolColor(blockMeta);
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

    @Override
    public boolean blocksMovement(int blockId) {
        Block block = Block.BLOCKS[blockId];
        if (block == null) {
            return false;
        }

        if (block.material == null) {
            return false;
        }

        return block.material.blocksMovement();
    }

}
