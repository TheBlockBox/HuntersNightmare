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
import theblockbox.huntersdream.blocks.custommodel.BlockAdvancedCraftingTable;
import theblockbox.huntersdream.blocks.custommodel.BlockBlessingTable;
import theblockbox.huntersdream.blocks.custommodel.BlockCandle;
import theblockbox.huntersdream.blocks.custommodel.BlockChair;
import theblockbox.huntersdream.blocks.custommodel.BlockCoffin;
import theblockbox.huntersdream.blocks.custommodel.BlockCrate;
import theblockbox.huntersdream.blocks.custommodel.BlockCursingTable;
import theblockbox.huntersdream.blocks.custommodel.BlockGunHolder;
import theblockbox.huntersdream.blocks.custommodel.BlockLantern;
import theblockbox.huntersdream.blocks.custommodel.BlockNatureTable;
import theblockbox.huntersdream.blocks.custommodel.BlockPlack;
import theblockbox.huntersdream.blocks.custommodel.BlockPlate;
import theblockbox.huntersdream.blocks.custommodel.BlockShelf;
import theblockbox.huntersdream.blocks.custommodel.BlockStandardTable;
import theblockbox.huntersdream.blocks.custommodel.BlockSummoningTable;
import theblockbox.huntersdream.blocks.custommodel.BlockTent;
import theblockbox.huntersdream.blocks.custommodel.BlockThrone;
import theblockbox.huntersdream.blocks.custommodel.BlockVampireAltar;
import theblockbox.huntersdream.blocks.custommodel.BlockWerewolfEnchantingStone;
import theblockbox.huntersdream.blocks.custommodel.BlockWitchCauldron;
import theblockbox.huntersdream.items.ItemBlockWithMaxStackSize;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.world.dimension.Dimensions;

public class BlockInit {
	public static final List<Block> BLOCKS = new ArrayList<>(); // also includes ores
	public static final List<BlockOre> ORES = new ArrayList<>();

	@ObjectHolder("huntersdream:block_silver")
	public static final Block BLOCK_SILVER = null;

	@ObjectHolder("huntersdream:furnace_silver")
	public static final Block FURNACE_SILVER = null;

	@ObjectHolder("huntersdream:campfire")
	public static final Block CAMPFIRE = null;

	@ObjectHolder("huntersdream:lantern")
	public static final Block LANTERN = null;

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
		registerWool(event, "green_nature", "light_blue_blessing", "purple_cursing", "red_summoning");
		registerBlock(new BlockWerewolfEnchantingStone(), "werewolf_enchanting_stone", event);
		registerBlock(new BlockWolfsbane(), "plant_wolfsbane", event);

		registerBlock(new BlockSilverFurnace(), "furnace_silver", event);
		registerBlock(new BlockPlack(), "plack", event);
		registerBlock(new BlockLantern(), "lantern", event);
		registerBlock(new BlockGunHolder(), "gun_holder", event);
		registerBlock(new BlockCoffin(), "coffin", event);
		registerBlock(new BlockTent(), "tent", event);
		registerBlock(new BlockWitchCauldron(), "witch_cauldron", event);
		registerBlock(new BlockCandle(), "candle", event);
		registerBlock(new BlockPlate(), "plate", event);
		registerBlock(new BlockShelf(), "shelf", event);
		registerBlock(new BlockThrone(), "throne", event);
		registerBlock(new BlockCrate(), "crate", event);
		registerBlock(new BlockVampireAltar(), "vampire_altar", event);
		registerBlock(new BlockAdvancedCraftingTable(), "advanced_crafting_table", event);
		registerChairs(event, 3);
		registerBlock(new BlockStandardTable(), "table", event);
		registerBlock(new BlockBlessingTable(), "blessing_table", event);
		registerBlock(new BlockCursingTable(), "cursing_table", event);
		registerBlock(new BlockNatureTable(), "nature_table", event);
		registerBlock(new BlockSummoningTable(), "summoning_table", event);
		registerBlock(new BlockCampfire(), "campfire", CreativeTabInit.HUNTERSDREAM_MISC, 1, event);
	}

	private static void registerBlock(Block block, String name, RegistryEvent.Register<Block> event) {
		registerBlock(block, name,
				block.getCreativeTab() == null ? CreativeTabInit.HUNTERSDREAM_MISC : block.getCreativeTab(), event);
	}

	private static void registerBlock(Block block, String name, CreativeTabs tab, RegistryEvent.Register<Block> event) {
		event.getRegistry().register(block.setTranslationKey(Reference.MODID + "." + name).setCreativeTab(tab)
				.setRegistryName(GeneralHelper.newResLoc(name)));
		BLOCKS.add(block);
		ItemInit.ITEMS.add(new ItemBlock(block).setRegistryName(GeneralHelper.newResLoc(name)));
	}

	private static void registerBlock(Block block, String name, CreativeTabs tab, int maxStackSize,
			RegistryEvent.Register<Block> event) {
		event.getRegistry().register(block.setTranslationKey(Reference.MODID + "." + name).setCreativeTab(tab)
				.setRegistryName(GeneralHelper.newResLoc(name)));
		BLOCKS.add(block);
		ItemInit.ITEMS
				.add(new ItemBlockWithMaxStackSize(block, maxStackSize).setRegistryName(GeneralHelper.newResLoc(name)));
	}

	private static void registerWool(RegistryEvent.Register<Block> event, String... names) {
		for (String name : names)
			registerBlock(new Block(Material.CLOTH).setHardness(0.8F), "wool_colored_" + name,
					CreativeTabInit.HUNTERSDREAM_MISC, event);
	}

	private static void registerChairs(RegistryEvent.Register<Block> event, int count) {
		for (int i = 0; i < count; i++) {
			Block block = new BlockChair();
			event.getRegistry().register(block.setTranslationKey(Reference.MODID + ".chair")
					.setRegistryName(GeneralHelper.newResLoc("chair_" + i)));
			BLOCKS.add(block);
			ItemInit.ITEMS.add(new ItemBlock(block).setRegistryName(GeneralHelper.newResLoc("chair_" + i)));
		}
	}
}
