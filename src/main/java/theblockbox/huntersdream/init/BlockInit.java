package theblockbox.huntersdream.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import theblockbox.huntersdream.blocks.BlockCampfire;
import theblockbox.huntersdream.blocks.BlockOre;
import theblockbox.huntersdream.blocks.BlockSilverFurnace;
import theblockbox.huntersdream.blocks.BlockWolfsbane;
import theblockbox.huntersdream.items.ItemBlockWithMaxStackSize;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.world.dimension.Dimensions;

public class BlockInit {
	public static final List<BlockOre> ORES = new ArrayList<>();

	@ObjectHolder("huntersdream:block_silver")
	public static final Block BLOCK_SILVER = null;

	@ObjectHolder("huntersdream:furnace_silver")
	public static final Block FURNACE_SILVER = null;

	@ObjectHolder("huntersdream:campfire")
	public static final Block CAMPFIRE = null;

	@ObjectHolder("huntersdream:overworld_ore_silver")
	public static final Block ORE_SILVER = null;

	@ObjectHolder("huntersdream:plant_wolfsbane")
	public static final Block WOLFSBANE = null;

	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		registerBlock(new Block(Material.IRON).setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC).setHardness(5.0F),
				"block_silver", event);
		registerBlock(
				new BlockOre(Dimensions.OVERWORLD, ConfigHandler.server.ores.silverMinY,
						ConfigHandler.server.ores.silverMaxY, ConfigHandler.server.ores.silverChance, Blocks.STONE),
				"overworld_ore_silver", event);
		registerBlock(new BlockWolfsbane(), "plant_wolfsbane", event);

		registerBlock(new BlockSilverFurnace(), "furnace_silver", event);
		registerBlock(new BlockCampfire(), "campfire", CreativeTabInit.HUNTERSDREAM_MISC, 1, event);
	}

	private static void registerBlock(Block block, String name, RegistryEvent.Register<Block> event) {
		registerBlock(block, name,
				block.getCreativeTab() == null ? CreativeTabInit.HUNTERSDREAM_MISC : block.getCreativeTab(), event);
	}

	private static void registerBlock(Block block, String name, CreativeTabs tab, RegistryEvent.Register<Block> event) {
		event.getRegistry().register(block.setTranslationKey(Reference.MODID + "." + name).setCreativeTab(tab)
				.setRegistryName(GeneralHelper.newResLoc(name)));
		ItemInit.ITEMS.add(new ItemBlock(block).setRegistryName(GeneralHelper.newResLoc(name)));
	}

	private static void registerBlock(Block block, String name, CreativeTabs tab, int maxStackSize,
			RegistryEvent.Register<Block> event) {
		event.getRegistry().register(block.setTranslationKey(Reference.MODID + "." + name).setCreativeTab(tab)
				.setRegistryName(GeneralHelper.newResLoc(name)));
		ItemInit.ITEMS
				.add(new ItemBlockWithMaxStackSize(block, maxStackSize).setRegistryName(GeneralHelper.newResLoc(name)));
	}
}
