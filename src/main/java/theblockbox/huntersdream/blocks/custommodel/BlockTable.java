package theblockbox.huntersdream.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockTable extends BlockBaseCustomModelWithDirection {

	public BlockTable() {
		super(Material.WOOD, 1.5F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return DEFAULT_BOUNDING_BOX;
	}
}
