package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.gen.feature.WorldGenMinable;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.world.dimension.Dimensions;

public class BlockOreBase extends BlockBase {
	public final int DIMENSION;
	public final Block SPAWN_ON;
	public final int CHANCE;
	public final int MIN_HEIGHT;
	public final int MAX_HEIGHT;
	private WorldGenMinable worldGenMinable;
	private boolean modified = false;

	public BlockOreBase(String name, int dimensionID, String dimensionName, int minHeight, int maxHeight, int chance,
			Block spawnOn) {
		super(dimensionName + "_ore_" + name, Material.ROCK, 3.0F);
		this.setResistance(5.0F);
		this.DIMENSION = dimensionID;
		this.CHANCE = chance;
		this.MIN_HEIGHT = minHeight;
		this.MAX_HEIGHT = maxHeight;
		this.SPAWN_ON = spawnOn;

		BlockInit.ORES.add(this);
		setHarvestLevel("pickaxe", 2);
	}

	public BlockOreBase(String name, Dimensions dimension, int minHeight, int maxHeight, int chance, Block spawnOn) {
		this(name, dimension.ID, dimension.NAME, minHeight, maxHeight, chance, spawnOn);
	}

	public BlockOreBase(String name, Dimensions dimension, int minHeight, int maxHeight, int chance) {
		this(name, dimension, minHeight, maxHeight, chance, dimension.SPAWN_ON);
	}

	public WorldGenMinable getWorldGenMinable() {
		return worldGenMinable;
	}

	public void setWorldGenMinable(WorldGenMinable worldGenMinable) {
		if (!modified)
			this.worldGenMinable = worldGenMinable;
	}
}
