package dev.natowb.natosatlas.core.colors;

import dev.natowb.natosatlas.core.glue.NacPlatformAPI;
import dev.natowb.natosatlas.core.models.NacChunk;

import java.util.HashMap;
import java.util.Map;

public class NacColorMapper {

    public static final Map<Integer, Integer> COLORS = new HashMap<>();

    static {
        COLORS.put(0, 0x000000); // Air
        COLORS.put(1, 0x707070); // StoneBlock
        COLORS.put(2, 0x7FB238); // GrassBlock
        COLORS.put(3, 0xB76A2F); // DirtBlock
        COLORS.put(4, 0x707070); // Block
        COLORS.put(5, 0x685332); // Block
        COLORS.put(6, 0x007C00); // SaplingBlock
        COLORS.put(7, 0x707070); // Block
        COLORS.put(8, 0x4040FF); // FlowingLiquidBlock
        COLORS.put(9, 0x4040FF); // StillLiquidBlock
        COLORS.put(10, 0xFF0000); // FlowingLiquidBlock
        COLORS.put(11, 0xFF0000); // StillLiquidBlock
        COLORS.put(12, 0xF7E9A3); // SandBlock
        COLORS.put(13, 0xF7E9A3); // GravelBlock
        COLORS.put(14, 0x707070); // OreBlock
        COLORS.put(15, 0x707070); // OreBlock
        COLORS.put(16, 0x707070); // OreBlock
        COLORS.put(17, 0x685332); // LogBlock
        COLORS.put(18, 0x007C00); // LeavesBlock
        COLORS.put(19, 0xA7A7A7); // SpongeBlock
        COLORS.put(20, 0x000000); // GlassBlock
        COLORS.put(21, 0x707070); // OreBlock
        COLORS.put(22, 0x707070); // Block
        COLORS.put(23, 0x707070); // DispenserBlock
        COLORS.put(24, 0x707070); // SandstoneBlock
        COLORS.put(25, 0x685332); // NoteBlock
        COLORS.put(26, 0xA7A7A7); // BedBlock
        COLORS.put(27, 0x000000); // RailBlock
        COLORS.put(28, 0x000000); // DetectorRailBlock
        COLORS.put(29, 0x707070); // PistonBlock
        COLORS.put(30, 0xA7A7A7); // CobwebBlock
        COLORS.put(31, 0x007C00); // TallPlantBlock
        COLORS.put(32, 0x007C00); // DeadBushBlock
        COLORS.put(33, 0x707070); // PistonBlock
        COLORS.put(34, 0x707070); // PistonHeadBlock
        COLORS.put(35, 0xA7A7A7); // WoolBlock
        COLORS.put(36, 0x707070); // PistonExtensionBlock
        COLORS.put(37, 0x007C00); // PlantBlock
        COLORS.put(38, 0x007C00); // PlantBlock
        COLORS.put(39, 0x007C00); // MushroomPlantBlock
        COLORS.put(40, 0x007C00); // MushroomPlantBlock
        COLORS.put(41, 0xA7A7A7); // OreStorageBlock
        COLORS.put(42, 0xA7A7A7); // OreStorageBlock
        COLORS.put(43, 0x707070); // SlabBlock
        COLORS.put(44, 0x707070); // SlabBlock
        COLORS.put(45, 0x707070); // Block
        COLORS.put(46, 0xFF0000); // TntBlock
        COLORS.put(47, 0x685332); // BookshelfBlock
        COLORS.put(48, 0x707070); // Block
        COLORS.put(49, 0x707070); // ObsidianBlock
        COLORS.put(50, 0x000000); // TorchBlock
        COLORS.put(51, 0x000000); // FireBlock
        COLORS.put(52, 0x707070); // SpawnerBlock
        COLORS.put(53, 0x685332); // StairsBlock
        COLORS.put(54, 0x685332); // ChestBlock
        COLORS.put(55, 0x000000); // RedstoneWireBlock
        COLORS.put(56, 0x707070); // OreBlock
        COLORS.put(57, 0xA7A7A7); // OreStorageBlock
        COLORS.put(58, 0x685332); // WorkbenchBlock
        COLORS.put(59, 0x007C00); // CropBlock
        COLORS.put(60, 0xB76A2F); // FarmlandBlock
        COLORS.put(61, 0x707070); // FurnaceBlock
        COLORS.put(62, 0x707070); // FurnaceBlock
        COLORS.put(63, 0x685332); // SignBlock
        COLORS.put(64, 0x685332); // DoorBlock
        COLORS.put(65, 0x000000); // LadderBlock
        COLORS.put(66, 0x000000); // RailBlock
        COLORS.put(67, 0x707070); // StairsBlock
        COLORS.put(68, 0x685332); // SignBlock
        COLORS.put(69, 0x000000); // LeverBlock
        COLORS.put(70, 0x707070); // PressurePlateBlock
        COLORS.put(71, 0xA7A7A7); // DoorBlock
        COLORS.put(72, 0x685332); // PressurePlateBlock
        COLORS.put(73, 0x707070); // RedstoneOreBlock
        COLORS.put(74, 0x707070); // RedstoneOreBlock
        COLORS.put(75, 0x000000); // RedstoneTorchBlock
        COLORS.put(76, 0x000000); // RedstoneTorchBlock
        COLORS.put(77, 0x000000); // ButtonBlock
        COLORS.put(78, 0xFFFFFF); // SnowyBlock
        COLORS.put(79, 0xA0A0FF); // IceBlock
        COLORS.put(80, 0xFFFFFF); // SnowBlock
        COLORS.put(81, 0x007C00); // CactusBlock
        COLORS.put(82, 0xA4A8B8); // ClayBlock
        COLORS.put(83, 0x007C00); // SugarCaneBlock
        COLORS.put(84, 0x685332); // JukeboxBlock
        COLORS.put(85, 0x685332); // FenceBlock
        COLORS.put(86, 0x007C00); // PumpkinBlock
        COLORS.put(87, 0x707070); // NetherrackBlock
        COLORS.put(88, 0xF7E9A3); // SoulSandBlock
        COLORS.put(89, 0x707070); // GlowstoneBlock
        COLORS.put(90, 0x000000); // NetherPortalBlock
        COLORS.put(91, 0x007C00); // PumpkinBlock
        COLORS.put(92, 0x000000); // CakeBlock
        COLORS.put(93, 0x000000); // RepeaterBlock
        COLORS.put(94, 0x000000); // RepeaterBlock
        COLORS.put(95, 0x685332); // LockedChestBlock
        COLORS.put(96, 0x685332); // TrapdoorBlock
    }


    public static int get(int blockId) {
        return COLORS.getOrDefault(blockId, 0xFFFF0000);
    }


}
