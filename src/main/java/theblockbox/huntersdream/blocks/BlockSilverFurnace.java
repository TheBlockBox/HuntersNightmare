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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySilverFurnace;
import theblockbox.huntersdream.init.CreativeTabInit;

public class BlockSilverFurnace extends BlockContainer {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool BURNING = PropertyBool.create("burning");

	public BlockSilverFurnace() {
		super(Material.ROCK);
		this.setHardness(3.5F);
		this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
		setHarvestLevel("pickaxe", 2);
		this.setDefaultState(
				this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(BURNING, false));
	}

	@Override
	public TileEntitySilverFurnace createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySilverFurnace();
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(BURNING) ? 13 : 0;
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, BURNING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta / 2)).withProperty(BURNING,
				meta % 2 == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() * 2 + (state.getValue(BURNING) ? 1 : 0);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntitySilverFurnace) {
				playerIn.openGui(Main.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
