package theblockbox.huntersdream.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCandle extends BlockBaseCustomModel {
	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(sixteenth(6.5F), 0, sixteenth(6.5F),
			sixteenth(9.5F), sixteenth(6), sixteenth(9.5F));

	public BlockCandle(String name) {
		super(name, Material.CIRCUITS, 0.4F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
	}
}
