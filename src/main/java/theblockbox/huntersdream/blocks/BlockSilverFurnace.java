package theblockbox.huntersdream.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.init.CreativeTabInit;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySilverFurnace;

import java.util.Random;

public class BlockSilverFurnace extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool BURNING = PropertyBool.create("burning");

	public BlockSilverFurnace() {
		super(Material.ROCK);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
        this.setHarvestLevel("pickaxe", 2);
		this.setDefaultState(
				this.getDefaultState().withProperty(BlockSilverFurnace.FACING, EnumFacing.NORTH).withProperty(BlockSilverFurnace.BURNING, false));
	}

	@Override
	public TileEntitySilverFurnace createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySilverFurnace();
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(BlockSilverFurnace.BURNING) ? 13 : 0;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(BlockSilverFurnace.FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockSilverFurnace.FACING, BlockSilverFurnace.BURNING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockSilverFurnace.FACING, EnumFacing.byHorizontalIndex(meta & 0b011))
				.withProperty(BlockSilverFurnace.BURNING, (meta & 0b100) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockSilverFurnace.FACING).getHorizontalIndex() | (state.getValue(BlockSilverFurnace.BURNING) ? 0b100 : 0b000);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (worldIn.getTileEntity(pos) instanceof TileEntitySilverFurnace) {
				playerIn.openGui(Main.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntitySilverFurnace)
			((TileEntitySilverFurnace) te).getInventory().forEach(stack -> InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack));
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random random) {
		if (stateIn.getValue(BlockSilverFurnace.BURNING)) {
			Blocks.LIT_FURNACE.randomDisplayTick(stateIn, worldIn, pos, random);
		}
	}
}
