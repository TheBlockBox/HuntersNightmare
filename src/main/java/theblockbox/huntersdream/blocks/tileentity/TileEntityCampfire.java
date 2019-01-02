package theblockbox.huntersdream.blocks.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theblockbox.huntersdream.blocks.BlockCampfire;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.inventory.SideItemHandler;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class TileEntityCampfire extends TileEntity implements ITickable {
	public static final String KEY_ITEM_HANDLER = "itemHandler";
	public static final String KEY_SMELTING_RECIPE_SINCE = "smeltingRecipeSince";
	public static final String KEY_TICKS = "ticks";
	public static final String KEY_BURN_TIME = "burnTime";
	public static final String KEY_FULL_BURN_TIME = "fullBurnTime";
	public static final String KEY_OUTPUT = "output";
	public static final String KEY_HAS_RECIPE = "hasRecipe";

	// slot 0: input, 1: fuel, 2: output
	private final TileEntityCampfire.CampfireItemHandler itemHandler = new TileEntityCampfire.CampfireItemHandler();
	private final IItemHandler itemHandlerTop = new SideItemHandler(this.itemHandler, i -> i == 0,
			GeneralHelper.FALSE_PREDICATE);
	private final IItemHandler itemHandlerSide = new SideItemHandler(this.itemHandler, i -> i == 1,
			GeneralHelper.FALSE_PREDICATE);
	private final IItemHandler itemHandlerBottom = new SideItemHandler(this.itemHandler, GeneralHelper.FALSE_PREDICATE,
			i -> i == 2);
	private int smeltingRecipeSince = 0;
	private int ticks = 0;
	private int burnTime = 0;
	private int fullBurnTime = 0;
	private ItemStack output = ItemStack.EMPTY;
	private boolean hasRecipe = false;

	@Override
	public void update() {
		if (!this.world.isRemote && this.ticks++ % 9 == 0) {
			boolean wasBurning = this.isBurning();
			this.burnTime -= 9;
			while (this.burnTime < 0) {
				if (this.hasRecipe()) {
					int fuel = TileEntityFurnace.getItemBurnTime(this.getFuel());
					if (fuel > 0 && !this.itemHandler.extractItem(1, 1, false).isEmpty()) {
						this.burnTime += fuel;
						this.fullBurnTime = fuel;
						this.markDirty();
						continue;
					}
				}

				if (this.burnTime < 0)
					this.burnTime = 0;
				this.markDirty();
				break;
			}

			if (this.hasRecipe()) {
				if (this.isBurning()) {
					if (this.ticks > (this.smeltingRecipeSince + 180)) {
						if (this.itemHandler.insertItem(2, this.output, false).isEmpty()) {
							this.itemHandler.extractItem(0, 1, false);
						}
						this.setHasRecipe(false);
					}
				} else {
					this.setHasRecipe(false);
				}
			} else {
				this.checkForRecipe();
			}

			if (wasBurning != this.isBurning())
				this.world.setBlockState(this.pos,
						this.world.getBlockState(this.pos).withProperty(BlockCampfire.BURNING, this.isBurning()));
		}
	}

	private void setHasRecipe(boolean hasRecipe) {
		this.hasRecipe = hasRecipe;
	}

	public boolean checkForRecipe() {
		ItemStack out = FurnaceRecipes.instance().getSmeltingResult(this.itemHandler.getStackInSlot(0)).copy();
		if (out.getItem() instanceof ItemFood) {
			if (this.itemHandler.insertItem(2, out, true).isEmpty()) {
				if (!(ItemStack.areItemStacksEqual(this.output, out) || this.hasRecipe())) {
					this.output = out;
				}
				this.smeltingRecipeSince = this.ticks;
				this.setHasRecipe(true);
				return true;
			}
		}
		this.setHasRecipe(false);
		return false;
	}

	public NonNullList<ItemStack> getInventory() {
		return this.itemHandler.getInventory();
	}

	private ItemStack getFuel() {
		return this.itemHandler.getStackInSlot(1);
	}

	public boolean isBurning() {
		return this.burnTime > 0;
	}

	public boolean hasRecipe() {
		return this.hasRecipe;
	}

	public int getBurnTime() {
		return this.burnTime;
	}

	public int getTicks() {
		return this.ticks;
	}

	public int getFullBurnTime() {
		return this.fullBurnTime;
	}

	public int getSmeltingRecipeSince() {
		return this.smeltingRecipeSince;
	}

	@Override
	public boolean shouldRefresh(World worldIn, BlockPos blockPos, IBlockState oldState, IBlockState newState) {
		return newState.getBlock() != BlockInit.CAMPFIRE;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilitiesInit.CAPABILITY_ITEM_HANDLER)
			return true;
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilitiesInit.CAPABILITY_ITEM_HANDLER) {
			if (facing == null)
				return (T) this.itemHandler;
			else if (facing == EnumFacing.UP)
				return (T) this.itemHandlerTop;
			else if (facing == EnumFacing.DOWN)
				return (T) this.itemHandlerBottom;
			else
				return (T) this.itemHandlerSide;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey(TileEntityCampfire.KEY_ITEM_HANDLER))
			CapabilitiesInit.CAPABILITY_ITEM_HANDLER.readNBT(this.itemHandler, null, compound.getTag(TileEntityCampfire.KEY_ITEM_HANDLER));
		if (compound.hasKey(TileEntityCampfire.KEY_BURN_TIME))
			this.burnTime = compound.getInteger(TileEntityCampfire.KEY_BURN_TIME);
		if (compound.hasKey(TileEntityCampfire.KEY_FULL_BURN_TIME))
			this.fullBurnTime = compound.getInteger(TileEntityCampfire.KEY_FULL_BURN_TIME);
		if (compound.hasKey(TileEntityCampfire.KEY_SMELTING_RECIPE_SINCE))
			this.smeltingRecipeSince = compound.getInteger(TileEntityCampfire.KEY_SMELTING_RECIPE_SINCE);
		if (compound.hasKey(TileEntityCampfire.KEY_TICKS))
			this.ticks = compound.getInteger(TileEntityCampfire.KEY_TICKS);
		if (compound.hasKey(TileEntityCampfire.KEY_HAS_RECIPE))
			this.hasRecipe = compound.getBoolean(TileEntityCampfire.KEY_HAS_RECIPE);
		if (compound.hasKey(TileEntityCampfire.KEY_OUTPUT))
			this.output = new ItemStack(compound.getCompoundTag(TileEntityCampfire.KEY_OUTPUT));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag(TileEntityCampfire.KEY_ITEM_HANDLER, CapabilitiesInit.CAPABILITY_ITEM_HANDLER.writeNBT(this.itemHandler, null));
		compound.setInteger(TileEntityCampfire.KEY_BURN_TIME, this.burnTime);
		compound.setInteger(TileEntityCampfire.KEY_FULL_BURN_TIME, this.fullBurnTime);
		compound.setInteger(TileEntityCampfire.KEY_SMELTING_RECIPE_SINCE, this.smeltingRecipeSince);
		compound.setInteger(TileEntityCampfire.KEY_TICKS, this.ticks);
		compound.setBoolean(TileEntityCampfire.KEY_HAS_RECIPE, this.hasRecipe);
		compound.setTag(TileEntityCampfire.KEY_OUTPUT, this.output.serializeNBT());
		return super.writeToNBT(compound);
	}

	private class CampfireItemHandler extends ItemStackHandler {
		private CampfireItemHandler() {
			super(3);
		}

		public NonNullList<ItemStack> getInventory() {
			return this.stacks;
		}

		@Override
		protected void onContentsChanged(int slot) {
			if (TileEntityCampfire.this.world != null && !TileEntityCampfire.this.world.isRemote) {
				// checks if recipe has changed
				TileEntityCampfire.this.checkForRecipe();
				TileEntityCampfire.this.markDirty();
			}
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return (slot == 0) || ((slot == 1) && TileEntityFurnace.isItemFuel(stack));
		}
	}
}
