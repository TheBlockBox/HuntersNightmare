package theblockbox.huntersdream.blocks.custommodel;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.entity.EntityChair;
import theblockbox.huntersdream.util.Reference;

public class BlockChair extends BlockBaseCustomModelWithDirection {
	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	public BlockChair(String name) {
		super(name, Material.WOOD, 1F);
		setTranslationKey(Reference.MODID + ".chair");
	}

	protected BlockChair(String name, Material materialIn, float hardness) {
		super(name, materialIn, hardness);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!playerIn.isSneaking()) {
			if (sitOnBlock(worldIn, pos.getX(), pos.getY(), pos.getZ(), playerIn, 7 * 0.0625)) {
				worldIn.updateComparatorOutputLevel(pos, this);
				return true;
			}
		}
		return false;
	}

	public static boolean sitOnBlock(World par1World, double x, double y, double z, EntityPlayer par5EntityPlayer,
			double par6) {
		if (!checkForExistingEntity(par1World, x, y, z, par5EntityPlayer)) {
			EntityChair nemb = new EntityChair(par1World, x, y, z, par6);
			par1World.spawnEntity(nemb);
			par5EntityPlayer.startRiding(nemb);
		}
		return true;
	}

	public static boolean checkForExistingEntity(World par1World, double x, double y, double z,
			EntityPlayer par5EntityPlayer) {
		List<EntityChair> listEMB = par1World.getEntitiesWithinAABB(EntityChair.class,
				new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D).expand(1D, 1D, 1D));
		for (EntityChair mount : listEMB) {
			if (mount.blockPosX == x && mount.blockPosY == y && mount.blockPosZ == z) {
				if (!mount.isBeingRidden()) {
					par5EntityPlayer.startRiding(mount);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
	}
}
