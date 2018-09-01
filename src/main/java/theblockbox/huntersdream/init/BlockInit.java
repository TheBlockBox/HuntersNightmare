package theblockbox.huntersdream.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import theblockbox.huntersdream.objects.blocks.BlockBase;
import theblockbox.huntersdream.objects.blocks.BlockOreBase;
import theblockbox.huntersdream.objects.blocks.BlockTableWool;
import theblockbox.huntersdream.objects.blocks.BlockWolfsbane;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockAdvancedCraftingTable;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockBlessingTable;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockCampfire;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockCandle;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockChair;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockCoffin;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockCrate;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockCursingTable;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockGunHolder;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockLantern;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockNatureTable;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockPlack;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockPlate;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockShelf;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockStandardTable;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockSummoningTable;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockTent;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockThrone;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockVampireAltar;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockWerewolfEnchantingStone;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockWitchCauldron;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.world.dimension.Dimensions;

public class BlockInit {
	public static final List<Block> BLOCKS = new ArrayList<>(); // also includes ores
	public static final List<BlockOreBase> ORES = new ArrayList<>();

	public static final Block BLOCK_SILVER = new BlockBase("block_silver", Material.IRON, 3.0F) {
		@Override
		public float getBlockHardness(IBlockState blockState, World worldIn, net.minecraft.util.math.BlockPos pos) {
			return 5F;
		}
	};
	public static final Block ORE_SILVER = new BlockOreBase("silver", Dimensions.OVERWORLD, ConfigHandler.silverMinY,
			ConfigHandler.silverMaxY, ConfigHandler.silverChance, Blocks.STONE);
	public static final Block WOOL_GREEN_NATURE = new BlockTableWool("green_nature");
	public static final Block WOOL_LIGHT_BLUE_BLESSING = new BlockTableWool("light_blue_blessing");
	public static final Block WOOL_PURPLE_CURSING = new BlockTableWool("purple_cursing");
	public static final Block WOOL_RED_SUMMONING = new BlockTableWool("red_summoning");

	public static final Block BLOCK_WEREWOLF_ENCHANTMENT_STONE = new BlockWerewolfEnchantingStone(
			"werewolf_enchanting_stone");
	public static final Block WOLFSBANE = BlockWolfsbane.of("plant_wolfsbane");
	public static final Block BLOCK_CHAIR_0 = new BlockChair("chair_0");
	public static final Block BLOCK_CHAIR_1 = new BlockChair("chair_1");
	public static final Block BLOCK_CHAIR_2 = new BlockChair("chair_2");
	public static final BlockAdvancedCraftingTable ADVANCED_CRAFTING_TABLE = new BlockAdvancedCraftingTable(
			"advanced_crafting_table");
	public static final Block BLESSING_TABLE = new BlockBlessingTable("blessing_table");
	public static final Block CURSING_TABLE = new BlockCursingTable("cursing_table");
	public static final Block NATURE_TABLE = new BlockNatureTable("nature_table");
	public static final Block SUMMONING_TABLE = new BlockSummoningTable("summoning_table");
	public static final Block TABLE = new BlockStandardTable("table");
	public static final Block VAMPIRE_ALTAR = new BlockVampireAltar("vampire_altar");
	public static final Block CRATE = new BlockCrate("crate");
	public static final Block THRONE = new BlockThrone("throne");
	public static final Block SHELF = new BlockShelf("shelf");
	public static final Block PLATE = new BlockPlate("plate");
	public static final Block CANDLE = new BlockCandle("candle");
	public static final Block WITCH_CAULDRON = new BlockWitchCauldron("witch_cauldron");
	public static final Block TENT = new BlockTent("tent");
	public static final Block COFFIN = new BlockCoffin("coffin");
	public static final Block CAMPFIRE = new BlockCampfire("campfire");
	public static final Block GUN_HOLDER = new BlockGunHolder("gun_holder");
	public static final Block LANTERN = new BlockLantern("lantern");
	public static final Block PLACK = new BlockPlack("plack");

	// Ores
	// only give a name, the dimension and the ore tag will be automatically added
	// Example: given name: copper, given dimension: 0
	// Result: overworld_ore_copper

	/*
	 * How to make new blocks: - create new JSON file in blockstates and
	 * models/block - create new texture in textures/blocks - create new JSON file
	 * (for item block) in models/item
	 */
}
