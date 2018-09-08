package theblockbox.huntersdream.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;

public class BlockCropBase extends BlockCrops implements IHasModel {
	protected Item seed;
	protected Item crop;
	private ItemBlock itemBlock;

	public BlockCropBase(String name, Item crop) {
		setUnlocalizedName(Reference.MODID + "." + name);
		setRegistryName(name);
		setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
		itemBlock = new ItemBlock(this);
		this.seed = this.getItemBlock();
		BlockInit.BLOCKS.add(this);
		ItemInit.ITEMS.add(getItemBlock().setRegistryName(this.getRegistryName()));
		setDefaultState(getDefaultState().withProperty(getAgeProperty(), 0));
	}

	public ItemBlock getItemBlock() {
		return this.itemBlock;
	}

	// what is planted
	@Override
	protected Item getSeed() {
		return this.seed;
	}

	// what is harvested
	@Override
	protected Item getCrop() {
		return this.crop;
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(getSeed());
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(this.getAgeProperty(), meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(this.getAgeProperty());
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return this.isMaxAge(state) ? getCrop() : getSeed();
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && this.canBePlantedOn(worldIn.getBlockState(pos.down()).getBlock());
	}

	public boolean canBePlantedOn(Block block) {
		return (block == Blocks.FARMLAND);
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) {
			final NonNullList<ItemStack> items = NonNullList.create();
			this.getDrops(items, worldIn, pos, state, fortune);
			for (ItemStack item : items) {
				spawnAsEntity(worldIn, pos, item);
			}
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		drops.add(new ItemStack(seed));
		if (getAge(state) >= getMaxAge()) {
			drops.add(new ItemStack(crop));
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, this.getAgeProperty());
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return this.canBePlantedOn(state.getBlock());
	}

	@Override
	protected int getAge(IBlockState state) {
		return state.getValue(getAgeProperty());
	}
}
