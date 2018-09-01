package theblockbox.huntersdream.objects.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockShelf extends BlockBaseCustomModelWithDirection {
	// bounding boxes in order SOUTH - WEST - NORTH - EAST
	public static final AxisAlignedBB[] BOUNDING_BOX = { new AxisAlignedBB(0, sixteenth(8), sixteenth(10), 1, 1, 0),
			new AxisAlignedBB(1, sixteenth(8), 0, sixteenth(6), 1, 1),
			new AxisAlignedBB(1, sixteenth(8), sixteenth(6), 0, 1, 1),
			new AxisAlignedBB(0, sixteenth(8), 0, sixteenth(10), 1, 1) };

	public BlockShelf(String name) {
		super(name, Material.WOOD, 1.5F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()];
	}
}
