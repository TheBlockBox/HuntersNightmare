package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class BlockWolfsbane extends BlockCrops {
	public static final PropertyInteger AGE_PROPERTY = PropertyInteger.create("age", 0, 4);

	private static final AxisAlignedBB[] WOLFSBANE_AABB = {
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D) };

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockWolfsbane.AGE_PROPERTY);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BlockWolfsbane.WOLFSBANE_AABB[state.getValue(this.getAgeProperty())];
	}

	@Override
	protected PropertyInteger getAgeProperty() {
		return BlockWolfsbane.AGE_PROPERTY;
	}

	@Override
	protected int getBonemealAgeIncrease(World worldIn) {
		return super.getBonemealAgeIncrease(worldIn) / 3;
	}

	@Override
	public int getMaxAge() {
		return 4;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		drops.add(new ItemStack(this.getSeed()));
		if (this.getAge(state) >= this.getMaxAge()) {
			drops.add(new ItemStack(this.getSeed()));
		}
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) {
			NonNullList<ItemStack> items = NonNullList.create();
			this.getDrops(items, worldIn, pos, state, fortune);
			for (ItemStack item : items) {
				Block.spawnAsEntity(worldIn, pos, item);
			}
		}
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entityIn;
			if (TransformationHelper.getTransformation(living) == Transformation.WEREWOLF) {
				// TODO: Add effects here
			}
		}
	}

	// what is planted
	@Override
	protected Item getSeed() {
		return Item.getItemFromBlock(this);
	}

	// what is harvested
	@Override
	protected Item getCrop() {
		return ItemInit.WOLFSBANE_FLOWER;
	}
}
