package theblockbox.huntersdream.blocks;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.blocks.tileentity.TileEntityCampfire;
import theblockbox.huntersdream.init.CreativeTabInit;

public class BlockCampfire extends BlockContainer {
	public static final PropertyBool BURNING = PropertyBool.create("burning");
	public static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 3 / 8.0D, 1);

	public BlockCampfire() {
		super(Material.WOOD);
		this.setHardness(2.0F);
		this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
		this.setHarvestLevel("axe", 0);
		this.setDefaultState(this.getDefaultState().withProperty(BlockCampfire.BURNING, false));
	}

	@Override
	public TileEntityCampfire createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCampfire();
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(BlockCampfire.BURNING) ? 13 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockCampfire.BURNING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockCampfire.BURNING, meta != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockCampfire.BURNING) ? 1 : 0;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityCampfire) {
				playerIn.openGui(Main.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityCampfire)
			((TileEntityCampfire) te).getInventory().forEach(stack -> InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BlockCampfire.BOUNDING_BOX;
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random random) {
		if (stateIn.getValue(BlockCampfire.BURNING)) {
			Blocks.FIRE.randomDisplayTick(stateIn, worldIn, pos, random);
		}
	}
}
