package theblockbox.huntersdream.objects.blocks.custommodel;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockVampireAltar extends BlockBaseCustomModelWithDirection {

	public BlockVampireAltar(String name) {
		super(name, Material.ROCK, 1.5F);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return DEFAULT_BOUNDING_BOX;
	}
}
