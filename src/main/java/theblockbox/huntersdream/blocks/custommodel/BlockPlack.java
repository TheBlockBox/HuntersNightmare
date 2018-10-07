package theblockbox.huntersdream.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockPlack extends BlockBaseCustomModelWithDirection {
	public static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[] {
			new AxisAlignedBB(0, 0, 0, 1, 1, sixteenth(1)), new AxisAlignedBB(sixteenth(15), 0, 0, 1, 1, 1),
			new AxisAlignedBB(0, 0, sixteenth(15), 1, 1, 1), new AxisAlignedBB(0, 0, 0, sixteenth(1), 1, 1) };

	public BlockPlack() {
		super(Material.WOOD, 0.8F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOXES[state.getValue(FACING).getHorizontalIndex()];
	}
}
