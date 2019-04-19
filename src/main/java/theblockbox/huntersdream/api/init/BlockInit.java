package theblockbox.huntersdream.api.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.blocks.*;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.world.dimension.Dimensions;

import java.util.ArrayList;
import java.util.List;

public class BlockInit {
    public static final List<BlockOre> ORES = new ArrayList<>();

    @GameRegistry.ObjectHolder("huntersdream:furnace_silver")
    public static final Block FURNACE_SILVER = null;

    @GameRegistry.ObjectHolder("huntersdream:overworld_ore_silver")
    public static final Block ORE_SILVER = null;

    @GameRegistry.ObjectHolder("huntersdream:aconite_flower")
    public static final Block ACONITE_FLOWER = null;

    @GameRegistry.ObjectHolder("huntersdream:tile_tent")
    public static final Block TENT = null;

    @GameRegistry.ObjectHolder("huntersdream:aconite_garland")
    public static final BlockAconiteGarland ACONITE_GARLAND = null;

    @GameRegistry.ObjectHolder("huntersdream:mountain_ash")
    public static final Block MOUNTAIN_ASH = null;

    @GameRegistry.ObjectHolder("huntersdream:mountain_ash_barrier")
    public static final Block MOUNTAIN_ASH_BARRIER = null;

    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        BlockInit.registerBlockWithItem(new Block(Material.IRON).setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC)
                .setHardness(5.0F), "block_silver", event);
        BlockInit.registerBlockWithItem(
                new BlockOre(Dimensions.OVERWORLD, ConfigHandler.server.ores.silverMinY,
                        ConfigHandler.server.ores.silverMaxY, ConfigHandler.server.ores.silverChance, Blocks.STONE),
                "overworld_ore_silver", event);
        BlockInit.registerBlockWithItem(new BlockAconiteFlower(), "aconite_flower", event);
        BlockInit.registerBlockWithItem(new BlockSilverFurnace(), "furnace_silver", event);
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            BlockInit.registerBlockWithItem(new BlockCampfire(type), "campfire_" + type.getName(), 1, event);
        }
        BlockInit.registerBlock(new BlockTent(), "tile_tent", event);
        BlockInit.registerBlockWithItem(new BlockAconiteGarland(), "aconite_garland", event);
        BlockInit.registerBlockWithItem(new BlockMountainAsh(), "mountain_ash", event);
        BlockInit.registerBlock(new BlockMountainAshBarrier(), "mountain_ash_barrier", event);
    }

    private static void registerBlock(Block block, String name, RegistryEvent.Register<Block> event) {
        event.getRegistry().register(block.setTranslationKey(Reference.MODID + "." + name)
                .setRegistryName(GeneralHelper.newResLoc(name)));
    }

    private static void registerBlockWithItem(Block block, String name, RegistryEvent.Register<Block> event) {
        BlockInit.registerBlock(block, name, event);
        ItemInit.ITEMS.add(new ItemBlock(block).setRegistryName(GeneralHelper.newResLoc(name)));
    }

    private static void registerBlockWithItem(Block block, String name, int maxStackSize,
                                              RegistryEvent.Register<Block> event) {
        BlockInit.registerBlock(block, name, event);
        ItemInit.ITEMS.add(new ItemBlock(block).setMaxStackSize(maxStackSize).setRegistryName(GeneralHelper.newResLoc(name)));
    }
}