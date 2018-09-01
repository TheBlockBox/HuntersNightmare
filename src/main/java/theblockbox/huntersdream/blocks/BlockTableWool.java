package theblockbox.huntersdream.blocks;

import net.minecraft.block.material.Material;

public class BlockTableWool extends BlockBase {

	public BlockTableWool(String name) {
		super("wool_colored_" + name, Material.CLOTH, 0.8F);
	}
}