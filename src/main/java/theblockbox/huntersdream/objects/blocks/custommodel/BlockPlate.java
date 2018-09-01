package theblockbox.huntersdream.objects.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockPlate extends BlockBaseCustomModel {
	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(7 / 32.0D, 0, 7 / 32.0D, 25 / 32.0D,
			sixteenth(1), 25 / 32.0D);

	public BlockPlate(String name) {
		super(name, Material.WOOD, 1.5F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
	}
}
