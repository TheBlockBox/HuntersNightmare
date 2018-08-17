package theblockbox.huntersdream.objects.blocks.custommodel;

import java.util.List;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.entity.EntityChair;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.objects.items.ItemBlockWithMaxStackSize;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class BlockChair extends BlockBaseCustomModel {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 2);
	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	public BlockChair(String name) {
		super(name, Material.WOOD, 1F);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_FURNITURE);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

		ItemInit.ITEMS.add(getItemBlock().setRegistryName(this.getRegistryName() + "1")
				.setUnlocalizedName(this.getUnlocalizedName()));
		ItemInit.ITEMS.add(getItemBlock().setRegistryName(this.getRegistryName() + "2")
				.setUnlocalizedName(this.getUnlocalizedName()));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VARIANT);
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
	protected ItemBlock getItemBlock() {
		return new ItemBlockWithMaxStackSize(this, 1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	// TODO: Add getMetaFromState and getStateFromMeta methods

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int i = 0; i <= 3; i++)
			for (int j = 0; j <= 4; j++)
				items.add(new ItemStack(this, GeneralHelper.convertFromBaseToBase(i + "" + j, 4, 10)));
	}
}
