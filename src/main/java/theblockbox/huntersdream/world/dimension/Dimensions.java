package theblockbox.huntersdream.world.dimension;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Made for easier ore generation
 */
public enum Dimensions {
	NETHER(Blocks.NETHERRACK, -1), OVERWORLD(Blocks.STONE, 0), END(Blocks.END_STONE, 1);
	public final int ID;
	public final String NAME;
	/**
	 * The block on which ores should spawn in this dimension (if not set)
	 */
	public final Block SPAWN_ON;

	private Dimensions(Block spawnOn, int id) {
		this.NAME = this.toString();
		this.ID = id;
		this.SPAWN_ON = spawnOn;
	}

	public static Dimensions getDimensionFromID(int id) {
		Dimensions dimension = null;
		for (Dimensions d : values()) {
			if (d.ID == id)
				dimension = d;
		}
		return dimension;
	}

	public static int getIDFromDimension(Dimensions dimension) {
		return dimension.ID;
	}

	/**
	 * Same as {@link Dimension#NAME}
	 * 
	 * @return Returns the dimension name
	 */
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
