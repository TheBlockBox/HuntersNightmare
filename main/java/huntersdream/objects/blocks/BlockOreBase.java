package huntersdream.objects.blocks;

import huntersdream.init.BlockInit;
import huntersdream.world.dimension.Dimensions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.gen.feature.WorldGenMinable;

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
		super(dimensionName + "_ore_" + name, Material.ROCK);
		this.setHardness(3.0F);
		this.setResistance(15.0F);
		this.DIMENSION = dimensionID;
		this.CHANCE = chance;
		this.MIN_HEIGHT = minHeight;
		this.MAX_HEIGHT = maxHeight;
		this.SPAWN_ON = spawnOn;

		BlockInit.ORES.add(this);
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
