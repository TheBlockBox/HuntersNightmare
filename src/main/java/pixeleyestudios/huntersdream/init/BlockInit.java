package pixeleyestudios.huntersdream.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import pixeleyestudios.huntersdream.objects.blocks.BlockBase;
import pixeleyestudios.huntersdream.objects.blocks.BlockOreBase;

public class BlockInit {
	public static final List<Block> BLOCKS = new ArrayList<>(); // also includes ores
	public static final List<BlockOreBase> ORES = new ArrayList<>();

	public static final Block BLOCK_PURE_SILVER = new BlockBase("block_pure_silver", Material.IRON);

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
}