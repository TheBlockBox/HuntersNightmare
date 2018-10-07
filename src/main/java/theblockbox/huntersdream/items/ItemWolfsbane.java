package theblockbox.huntersdream.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import theblockbox.huntersdream.init.BlockInit;

public class ItemWolfsbane extends Item implements IPlantable {
	public ItemWolfsbane() {
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		IBlockState state = worldIn.getBlockState(pos);
		if ((facing == EnumFacing.UP) && player.canPlayerEdit(pos.offset(facing), facing, stack)
				&& state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this)
				&& worldIn.isAirBlock(pos)) {
			worldIn.setBlockState(pos.up(), BlockInit.WOLFSBANE.getDefaultState());
			stack.shrink(1);
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return BlockInit.WOLFSBANE.getDefaultState();
	}
}
