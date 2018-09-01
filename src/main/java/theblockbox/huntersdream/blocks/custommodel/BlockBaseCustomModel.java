package theblockbox.huntersdream.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import theblockbox.huntersdream.blocks.BlockBase;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.items.ItemBlockWithMaxStackSize;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public abstract class BlockBaseCustomModel extends BlockBase {
	public static final AxisAlignedBB DEFAULT_BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	public BlockBaseCustomModel(String name, Material materialIn, float hardness) {
		super(name, materialIn, hardness);
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

	@Override
	protected ItemBlock getItemBlock() {
		return new ItemBlockWithMaxStackSize(this, 1);
	}

	public static double sixteenth(double numerator) {
		return GeneralHelper.getSixteenth(numerator);
	}
}
