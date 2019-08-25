package theblockbox.huntersdream.api.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.blocks.*;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.world.dimension.Dimensions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BlockInit {
    public static final List<BlockOre> ORES = new ArrayList<>();
    // this block is used as a placeholder for uninitialized blocks
    // it's annotated with non null to make ides not complain about final fields that equal null
    @Nonnull
    private static final Block NULL_BLOCK = null;

    @GameRegistry.ObjectHolder("huntersdream:silver_ore")
    public static final Block SILVER_ORE = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:aconite_flower")
    public static final Block ACONITE_FLOWER = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:monkshood_flower")
    public static final Block MONKSHOOD_FLOWER = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:glow_fern")
    public static final Block GLOW_FERN = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:magma_flower")
    public static final Block MAGMA_FLOWER = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:poison_ivy")
    public static final Block POISON_IVY = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:wither_moss")
    public static final Block WITHER_MOSS = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:tile_tent")
    public static final Block TENT = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:cotton")
    public static final Block COTTON = BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:wolfsbane_garland")
    public static final BlockWolfsbaneGarland WOLFSBANE_GARLAND = (BlockWolfsbaneGarland) BlockInit.NULL_BLOCK;

    @GameRegistry.ObjectHolder("huntersdream:wolfsbane_petals")
    public static final Block WOLFSBANE_PETALS = BlockInit.NULL_BLOCK;

    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        BlockInit.registerBlockWithItem(new Block(Material.IRON).setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC)
                .setHardness(5.0F), "silver_block", event);
        BlockInit.registerBlockWithItem(
                new BlockOre(Dimensions.OVERWORLD, ConfigHandler.server.ores.silverMinY,
                        ConfigHandler.server.ores.silverMaxY, ConfigHandler.server.ores.silverChance, Blocks.STONE),
                "silver_ore", event);
        BlockInit.registerBlockWithItem(new BlockWolfsbaneFlower(), "aconite_flower", event);
        BlockInit.registerBlockWithItem(new BlockWolfsbaneFlower(), "monkshood_flower", event);
        BlockInit.registerBlockWithItem(new BlockGlowFern(), "glow_fern", event);
        BlockInit.registerBlockWithItem(new BlockMagmaFlower(), "magma_flower", event);
        BlockInit.registerBlockWithItem(new BlockEffectVine(new PotionEffect(MobEffects.POISON, 240))
                .setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC), "poison_ivy", event);
        BlockInit.registerBlockWithItem(new BlockEffectVine(new PotionEffect(MobEffects.WITHER, 240))
                .setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC), "wither_moss", event);
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            BlockInit.registerBlockWithItem(new BlockCampfire(type), type.getName() + "_campfire", 1, event);
        }
        BlockInit.registerBlock(new BlockTent(), "tile_tent", event);
        BlockInit.registerBlockWithItem(new BlockCotton(), "cotton", event);
        BlockInit.registerBlockWithItem(new BlockSpinningWheel(), "spinning_wheel", event);
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
