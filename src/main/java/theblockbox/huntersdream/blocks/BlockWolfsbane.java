package theblockbox.huntersdream.blocks;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.ItemInit;

public class BlockWolfsbane extends BlockCropBase {
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 4);

	public static final AxisAlignedBB[] WOLFSBANE_AABB = {
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, getSixteenth(2), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, getSixteenth(6), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, getSixteenth(9), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, getSixteenth(13), 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, getSixteenth(15), 1.0D) };

	private BlockWolfsbane(String name) {
		super(name, ItemInit.WOLFSBANE_FLOWER);
	}

	public static BlockWolfsbane of(String name) {
		BlockWolfsbane wolfsbane = new BlockWolfsbane(name);
		wolfsbane.crop = ItemInit.WOLFSBANE_FLOWER;
		return wolfsbane;
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
	protected int getBonemealAgeIncrease(World worldIn) {
		return super.getBonemealAgeIncrease(worldIn) / 3;
	}

	@Override
	public int getMaxAge() {
		return 4;
	}

	private static double getSixteenth(double numerator) {
		return numerator / 16.0D;
	}
}
