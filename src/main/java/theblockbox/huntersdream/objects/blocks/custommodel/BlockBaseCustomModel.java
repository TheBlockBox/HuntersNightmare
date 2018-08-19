package theblockbox.huntersdream.objects.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import theblockbox.huntersdream.objects.blocks.BlockBase;

public abstract class BlockBaseCustomModel extends BlockBase {

	public BlockBaseCustomModel(String name, Material materialIn, float hardness, boolean silver) {
		super(name, materialIn, hardness, silver);
	}

	public BlockBaseCustomModel(String name, Material materialIn, float hardness) {
		this(name, materialIn, hardness, false);
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
}
