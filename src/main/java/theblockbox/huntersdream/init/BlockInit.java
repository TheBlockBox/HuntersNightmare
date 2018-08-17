package theblockbox.huntersdream.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import theblockbox.huntersdream.objects.blocks.BlockBase;
import theblockbox.huntersdream.objects.blocks.BlockOreBase;
import theblockbox.huntersdream.objects.blocks.custommodel.BlockWerewolfEnchantingStone;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.world.dimension.Dimensions;

public class BlockInit {
	public static final List<Block> BLOCKS = new ArrayList<>(); // also includes ores
	public static final List<BlockOreBase> ORES = new ArrayList<>();

	public static final Block BLOCK_SILVER = new BlockBase("block_silver", Material.IRON, 3.0F, true) {
		@Override
		public float getBlockHardness(IBlockState blockState, World worldIn, net.minecraft.util.math.BlockPos pos) {
			return 5F;
		}
	};
	public static final Block ORE_SILVER = new BlockOreBase("silver", Dimensions.OVERWORLD, ConfigHandler.silverMinY,
			ConfigHandler.silverMaxY, ConfigHandler.silverChance, Blocks.STONE, true);
	public static final Block BLOCK_WEREWOLF_ENCHANTMENT_STONE = new BlockWerewolfEnchantingStone(
			"werewolf_enchanting_stone");
	// public static final Block BLOCK_CHAIR = new BlockChair("chair");

	// Ores
	// only give a name, the dimension and the ore tag will be automatically added
	// Example: given name: copper, given dimension: 0
	// Result: overworld_ore_copper

	// public static final Block OVERWORLD_ORE_SILVER = new BlockOreBase("silver",
	// Dimensions.OVERWORLD, 0, 100, 90);

	/*
	 * How to make new blocks: - create new JSON file in blockstates and
	 * models/block - create new texture in textures/blocks - create new JSON file
	 * (for item block) in models/item
	 */

	static {
		FurnaceRecipes.instance().addSmeltingRecipeForBlock(ORE_SILVER, new ItemStack(ItemInit.INGOT_SILVER), 0.9F);
	}
}
