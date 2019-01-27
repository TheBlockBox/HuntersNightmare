package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.gen.feature.WorldGenMinable;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.world.dimension.Dimensions;

public class BlockOre extends Block {
	public final int dimension;
	public final Block spawnOn;
	public final int chance;
	public final int minHeight;
	public final int maxHeight;
	private WorldGenMinable worldGenMinable;
	private boolean modified = false;

	public BlockOre(int dimensionID, int minHeight, int maxHeight, int chance, Block spawnOn) {
		super(Material.ROCK);
		this.setHardness(3.0F);
		this.dimension = dimensionID;
		this.chance = chance;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.spawnOn = spawnOn;
		this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);

		BlockInit.ORES.add(this);
		this.setHarvestLevel("pickaxe", 2);
	}

	public BlockOre(Dimensions dimension, int minHeight, int maxHeight, int chance, Block spawnOn) {
		this(dimension.id, minHeight, maxHeight, chance, spawnOn);
	}

	public BlockOre(Dimensions dimension, int minHeight, int maxHeight, int chance) {
		this(dimension, minHeight, maxHeight, chance, dimension.spawnOn);
	}

	public WorldGenMinable getWorldGenMinable() {
		return this.worldGenMinable;
	}

	public void setWorldGenMinable(WorldGenMinable worldGenMinable) {
		if (!this.modified) {
			this.worldGenMinable = worldGenMinable;
			this.modified = true;
		}
	}
}
