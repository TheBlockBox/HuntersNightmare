package theblockbox.huntersdream.objects.blocks.custommodel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.handlers.PacketHandler.Packets;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class BlockWerewolfEnchantingStone extends BlockBaseCustomModel {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final Material WEREWOLF_ENCHANTING_STONE = new Material(MapColor.STONE) {
		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public boolean blocksMovement() {
			return false;
		}

		@Override
		public boolean getCanBurn() {
			return false;
		}

		@Override
		public boolean isReplaceable() {
			return false;
		}

		@Override
		public boolean isOpaque() {
			return false;
		}

		@Override
		public boolean isToolNotRequired() {
			return false;
		};
	};

	public BlockWerewolfEnchantingStone(String name) {
		super(name, WEREWOLF_ENCHANTING_STONE, 3.5F);
		setHarvestLevel("pickaxe", 2);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(getSixteenth(1), 0, getSixteenth(1), getSixteenth(15), getSixteenth(10),
				getSixteenth(15));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (TransformationHelper.getTransformation(playerIn) == Transformations.WEREWOLF) {
				ITransformationPlayer cap = TransformationHelper.getCap(playerIn);
				if (!cap.hasRitual(Rituals.LUPUS_ADVOCABIT)) {
					cap.addRitual(Rituals.LUPUS_ADVOCABIT);
					Packets.TRANSFORMATION.sync(playerIn);
					playerIn.sendMessage(new TextComponentTranslation("werewolf_enchanting_stone.onClick.werewolf"));
				} else {
					return false;
				}
			} else {
				playerIn.sendMessage(new TextComponentTranslation("werewolf_enchanting_stone.onClick.notWerewolf"));
			}
		}
		return true;
	}

	@Override
	protected ItemBlock getItemBlock() {
		return new ItemBlockWerewolfEnchantingStone(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return canBlockStay(worldIn, pos);
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return canPlaceBlockAt(worldIn, pos);
	}

	private boolean canBlockStay(World worldIn, BlockPos pos) {
		boolean flag = worldIn.getBlockState(pos.down()).getMaterial().isSolid();
		return flag;
	}

	private static class ItemBlockWerewolfEnchantingStone extends ItemBlock
			implements ISilverEffectiveAgainstTransformation {
		public ItemBlockWerewolfEnchantingStone(Block block) {
			super(block);
			setMaxStackSize(1);
		}
	}
}
