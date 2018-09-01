package theblockbox.huntersdream.objects.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockLantern extends BlockBaseCustomModelWithDirection {
	public static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[] {
			new AxisAlignedBB(sixteenth(3), 0, sixteenth(3), sixteenth(12), 1, sixteenth(12)),
			new AxisAlignedBB(sixteenth(4), 0, sixteenth(3), sixteenth(13), 1, sixteenth(12)),
			new AxisAlignedBB(sixteenth(4), 0, sixteenth(4), sixteenth(13), 1, sixteenth(13)),
			new AxisAlignedBB(sixteenth(3), 0, sixteenth(4), sixteenth(12), 1, sixteenth(13)) };

	public BlockLantern(String name) {
		super(name, Material.IRON, 1.7F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOXES[state.getValue(FACING).getHorizontalIndex()];
	}
}
