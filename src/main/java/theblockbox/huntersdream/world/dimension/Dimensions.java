package theblockbox.huntersdream.world.dimension;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Made for easier ore generation
 */
public enum Dimensions {
	NETHER(Blocks.NETHERRACK, 1), OVERWORLD(Blocks.STONE, 0), END(Blocks.END_STONE, -1);
	public final int id;
	public final String name;
	/**
	 * The block on which ores should spawn in this dimension (if not set)
	 */
	public final Block spawnOn;

	Dimensions(Block spawnOn, int id) {
		this.name = this.toString();
		this.id = id;
		this.spawnOn = spawnOn;
	}

	public static Dimensions getDimensionFromID(int id) {
		Dimensions dimension = null;
		for (Dimensions d : Dimensions.values()) {
			if (d.id == id)
				dimension = d;
		}
		return dimension;
	}

	public static int getIDFromDimension(Dimensions dimension) {
		return dimension.id;
	}

	/**
	 * Same as {@link Dimensions#name}
	 * 
	 * @return Returns the dimension name
	 */
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
