package theblockbox.huntersdream.blocks.tileentity;

import java.util.Optional;
import java.util.function.IntPredicate;

import org.apache.commons.lang3.tuple.MutablePair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.blocks.BlockSilverFurnace;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.SideItemStackHandler;
import theblockbox.huntersdream.util.SilverFurnaceRecipe;

public class TileEntitySilverFurnace extends TileEntity implements ITickable {
	private static final IntPredicate FALSE_PREDICATE = i -> false;
	private int ticks = 0;
	/** The tick on which the current recipe has started smelting */
	private int smeltingRecipeSince;
	/** The time that is needed to completely smelt the current recipe */
	private int fullNeededSmeltingTime;
	private int burnTime = 0;
	/**
	 * The amount that should be subtracted from the first input stack when the
	 * furnace successfully smelted a recipe
	 */
	private int amount1;
	/**
	 * The amount that should be subtracted from the second input stack when the
	 * furnace successfully smelted a recipe
	 */
	private int amount2;
	private MutablePair<ItemStack, ItemStack> outputs = new MutablePair<>(ItemStack.EMPTY, ItemStack.EMPTY);
	private float experience;
	private boolean hasRecipe;
	private IItemHandler itemHandler = new ItemStackHandler(5) {
		@Override
		protected void onContentsChanged(int slot) {
			// checks if recipe has changed
			setHasRecipe(checkForRecipe());
			markDirty();
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot == 0) {
				return TileEntityFurnace.isItemFuel(stack);
			} else if (slot == 3 || slot == 4) {
				return false;
			} else {
				return true;
			}
		}
	};
	private IItemHandler itemHandlerTop = new SideItemStackHandler(this.itemHandler, i -> i == 1 || i == 2,
			FALSE_PREDICATE);
	private IItemHandler itemHandlerSide = new SideItemStackHandler(this.itemHandler, i -> i == 0, FALSE_PREDICATE);
	private IItemHandler itemHandlerBottom = new SideItemStackHandler(this.itemHandler, FALSE_PREDICATE,
			i -> i == 3 || i == 4);

	@Override
	public void update() {
		if (this.ticks++ % 10 == 0 && !this.world.isRemote) {
			boolean wasBurning = this.isBurning();
			this.updateBurnTime(10);

			if (this.hasRecipe()) {
				if (this.isBurning()) {
					if (this.ticks >= (this.smeltingRecipeSince + this.fullNeededSmeltingTime)) {
						boolean flag = !(this.itemHandler.extractItem(1, this.amount1, false).isEmpty()
								|| this.itemHandler.extractItem(2, this.amount2, false).isEmpty())
								&& this.addItemsToOutputIfPossible(this.outputs.getLeft(), this.outputs.getRight());
						if (!flag) {
							Main.getLogger().error("Couldn't extract items into ");
						}
						this.setHasRecipe(false);
					}
				} else {
					this.setHasRecipe(false);
				}
			} else if (this.isBurning()) {
				this.checkForRecipe();
			}

			// reset blockstate if burning state changed
			if (wasBurning != this.isBurning()) {
				this.world.setBlockState(this.pos,
						this.world.getBlockState(this.pos).withProperty(BlockSilverFurnace.BURNING, this.isBurning()));
			}
		}
	}

	/**
	 * Checks for a fitting recipe first in the vanilla recipes, then in the silver
	 * furnace recipes. If there a fitting one is found, the values are set and true
	 * is returned, otherwise false is returned. This method can also be used to
	 * test if a recipe still applies and set another one if the old one doesn't
	 * apply
	 */
	private boolean checkForRecipe() {
		ItemStack input = this.getInput1();
		ItemStack stack = FurnaceRecipes.instance().getSmeltingResult(input);
		if (!stack.isEmpty() && this.setRecipe(1, 0, stack, ItemStack.EMPTY, 200,
				FurnaceRecipes.instance().getSmeltingExperience(stack))) {
			return true;
		}
		Optional<SilverFurnaceRecipe> sfr = SilverFurnaceRecipe.getFromInput(input, this.getInput2());
		if (sfr.isPresent()) {
			SilverFurnaceRecipe recipe = sfr.get();
			return this.setRecipe(recipe.getAmount1(), recipe.getAmount2(), recipe.getOutput1(), recipe.getOutput2(),
					recipe.getSmeltingTime(), recipe.getExperience());
		}
		return false;
	}

	/**
	 * Tries to set the current recipe. Returns false if it wasn't successful
	 * (meaning that the output items couldn't be added to the output)
	 */
	private boolean setRecipe(int amount1, int amount2, ItemStack output1, ItemStack output2,
			int fullNeededSmeltingTime, float experience) {
		if (this.canAddItemsToOutput(output1, output2)) {
			// if everything is still the same, just return true and do nothing
			if ((this.hasRecipe() && (this.amount1 == amount1) && (this.amount2 == amount2)
					&& ItemStack.areItemStacksEqual(this.outputs.getLeft(), output1)
					&& ItemStack.areItemStacksEqual(this.outputs.getRight(), output2)
					&& this.fullNeededSmeltingTime == fullNeededSmeltingTime && this.experience == experience)) {
				return true;
			}

			this.amount1 = amount1;
			this.amount2 = amount2;
			this.outputs.setLeft(output1);
			this.outputs.setRight(output2);
			this.fullNeededSmeltingTime = fullNeededSmeltingTime;
			this.experience = experience;
			this.hasRecipe = true;
			this.smeltingRecipeSince = this.ticks;
			this.markDirty();
			return true;
		}
		return false;
	}

	private void updateBurnTime(int toDecrease) {
		this.burnTime -= toDecrease;
		while (this.burnTime < 0) {
			// if there is the furnace has a recipe,
			if (this.hasRecipe()) {
				// get fuel in fuel slot
				int fuel = TileEntityFurnace.getItemBurnTime(this.getFuel());
				// if there is fuel,
				if (fuel > 0) {
					// try to extract and if successful, add fuel to burnTime
					if (this.itemHandler.extractItem(0, 1, false).isEmpty()) {
						this.burnTime += fuel;
						this.markDirty();
						continue;
					}
				}
			}
			// if no fuel was found, the item couldn't be extracted or there was no recipe,
			// set fuel to 0 and go out
			// of the loop
			this.burnTime = 0;
			break;
		}
		this.markDirty();
	}

	public boolean isBurning() {
		return this.burnTime > 0;
	}

	/**
	 * Returns true if the furnace is currently smelting something. Only use this
	 * method to test if the furnace is smelting
	 */
	public boolean hasRecipe() {
		return this.hasRecipe;
	}

	private void setHasRecipe(boolean hasRecipe) {
		this.hasRecipe = hasRecipe;
		this.markDirty();
	}

	private ItemStack getInput1() {
		return this.itemHandler.getStackInSlot(1);
	}

	private ItemStack getInput2() {
		return this.itemHandler.getStackInSlot(2);
	}

	private ItemStack getFuel() {
		return this.itemHandler.getStackInSlot(0);
	}

	@Override
	public boolean shouldRefresh(World worldIn, BlockPos blockPos, IBlockState oldState, IBlockState newState) {
		return !(newState.getBlock() == BlockInit.FURNACE_SILVER);
	}

	/**
	 * Tries to add the given items to the output. Returns false if they couldn't be
	 * added
	 */
	public boolean addItemsToOutputIfPossible(ItemStack stack1, ItemStack stack2) {
		if (this.itemHandler.insertItem(3, stack1, true).isEmpty()
				&& this.itemHandler.insertItem(4, stack2, true).isEmpty()) {
			this.itemHandler.insertItem(3, stack1, false);
			this.itemHandler.insertItem(4, stack2, false);
			return true;
		} else if (this.itemHandler.insertItem(3, stack2, true).isEmpty()
				&& this.itemHandler.insertItem(4, stack1, true).isEmpty()) {
			this.itemHandler.insertItem(3, stack2, false);
			this.itemHandler.insertItem(4, stack1, false);
			return true;
		} else {
			return false;
		}
	}

	public boolean canAddItemsToOutput(ItemStack stack1, ItemStack stack2) {
		return (this.itemHandler.insertItem(3, stack1, true).isEmpty()
				&& this.itemHandler.insertItem(4, stack2, true).isEmpty())
				|| (this.itemHandler.insertItem(3, stack2, true).isEmpty()
						&& this.itemHandler.insertItem(4, stack1, true).isEmpty());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("itemHandler"))
			CapabilitiesInit.CAPABILITY_ITEM_HANDLER.readNBT(this.itemHandler, null, compound.getTag("itemHandler"));
		this.burnTime = compound.getInteger("fuel");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("itemHandler", CapabilitiesInit.CAPABILITY_ITEM_HANDLER.writeNBT(this.itemHandler, null));
		compound.setInteger("fuel", this.burnTime);
		return super.writeToNBT(compound);
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
}
