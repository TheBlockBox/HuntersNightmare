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

    @GameRegistry.ObjectHolder("huntersdream:silver_ore")
    public static final Block SILVER_ORE = null;

    /**
     * dummy field; always equals null
     */
    public static final Block SILVER_FURNACE = null;

    @GameRegistry.ObjectHolder("huntersdream:aconite_flower")
    public static final Block ACONITE_FLOWER = null;

    @GameRegistry.ObjectHolder("huntersdream:monkshood_flower")
    public static final Block MONKSHOOD_FLOWER = null;

    @GameRegistry.ObjectHolder("huntersdream:tile_tent")
    public static final Block TENT = null;

    @GameRegistry.ObjectHolder("huntersdream:wolfsbane_garland")
    public static final BlockWolfsbaneGarland WOLFSBANE_GARLAND = null;

    @GameRegistry.ObjectHolder("huntersdream:wolfsbane_petals")
    public static final Block WOLFSBANE_PETALS = null;

    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        BlockInit.registerBlockWithItem(new Block(Material.IRON).setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC)
                .setHardness(5.0F), "silver_block", event);
        BlockInit.registerBlockWithItem(
                new BlockOre(Dimensions.OVERWORLD, ConfigHandler.server.ores.silverMinY,
                        ConfigHandler.server.ores.silverMaxY, ConfigHandler.server.ores.silverChance, Blocks.STONE),
                "silver_ore", event);
        BlockInit.registerBlockWithItem(new BlockWolfsbaneFlower(), "aconite_flower", event);
        BlockInit.registerBlockWithItem(new BlockWolfsbaneFlower(), "monkshood_flower", event);
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            BlockInit.registerBlockWithItem(new BlockCampfire(type), type.getName() + "_campfire", 1, event);
        }
        BlockInit.registerBlock(new BlockTent(), "tile_tent", event);
        BlockInit.registerBlockWithItem(new BlockWolfsbaneGarland(), "wolfsbane_garland", event);
        BlockInit.registerBlockWithItem(new BlockWolfsbanePetals(), "wolfsbane_petals", event);
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
