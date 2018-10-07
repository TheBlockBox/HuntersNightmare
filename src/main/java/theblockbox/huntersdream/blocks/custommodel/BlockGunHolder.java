package theblockbox.huntersdream.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockGunHolder extends BlockBaseCustomModelWithDirection {
	public static final AxisAlignedBB[] BOUNDING_BOX = new AxisAlignedBB[] {
			new AxisAlignedBB(0, sixteenth(4), 0, 1, sixteenth(12), sixteenth(5)),
			new AxisAlignedBB(sixteenth(11), sixteenth(4), 0, 1, sixteenth(12), 1),
			new AxisAlignedBB(0, sixteenth(4), sixteenth(11), 1, sixteenth(12), 1),
			new AxisAlignedBB(0, sixteenth(4), 0, sixteenth(5), sixteenth(12), 1) };

	public BlockGunHolder() {
		super(Material.IRON, 1.7F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX[state.getValue(FACING).getHorizontalIndex()];
	}
}
