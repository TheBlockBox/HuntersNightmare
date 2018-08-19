package theblockbox.huntersdream.objects.blocks;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class BlockWolfsbane extends BlockCropBase {
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 4);

	public static final AxisAlignedBB[] WOLFSBANE_AABB = {
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, GeneralHelper.getSixteenth(2), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, GeneralHelper.getSixteenth(6), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, GeneralHelper.getSixteenth(9), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, GeneralHelper.getSixteenth(13), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, GeneralHelper.getSixteenth(15), 1.0D) };

	public BlockWolfsbane(String name) {
		super(name, Item.getItemFromBlock(BlockInit.WOLFSBANE));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AGE);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return WOLFSBANE_AABB[state.getValue(this.getAgeProperty()).intValue()];
	}

	@Override
	protected PropertyInteger getAgeProperty() {
		return AGE;
	}

	@Override
	protected Item getCrop() {
		return ItemInit.WOLFSBANE;
	}

	@Override
	public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world,
			BlockPos pos, IBlockState state, int fortune) {
		super.getDrops(drops, world, pos, state, 0);
		if (getAge(state) >= getMaxAge()) {
			drops.add(new ItemStack(this.getSeed(), 1, 0));
		}
	}

	@Override
	protected int getBonemealAgeIncrease(World worldIn) {
		return super.getBonemealAgeIncrease(worldIn) / 3;
	}

	@Override
	public int getMaxAge() {
		return 4;
	}
}
