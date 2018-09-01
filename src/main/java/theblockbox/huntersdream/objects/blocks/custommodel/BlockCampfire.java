package theblockbox.huntersdream.objects.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCampfire extends BlockBaseCustomModelWithDirection {

	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(sixteenth(1), 0, sixteenth(1), sixteenth(15),
			sixteenth(12), sixteenth(15));

	public BlockCampfire(String name) {
		super(name, Material.ROCK, 1.7F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
	}
}
