package theblockbox.huntersdream.blocks.custommodel;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import theblockbox.huntersdream.init.CreativeTabInit;

public abstract class BlockBaseCustomModel extends Block {
	public static final AxisAlignedBB DEFAULT_BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	public BlockBaseCustomModel(Material materialIn, float hardness) {
		super(materialIn);
		setHardness(hardness);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_FURNITURE);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public abstract AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos);

	public static double sixteenth(double numerator) {
		return numerator / 16.0D;
	}
}
