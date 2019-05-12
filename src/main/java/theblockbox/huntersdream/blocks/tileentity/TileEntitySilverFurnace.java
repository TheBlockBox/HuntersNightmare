package theblockbox.huntersdream.blocks.tileentity;

import net.minecraft.block.state.IBlockState;
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
import org.apache.commons.lang3.tuple.MutablePair;
import theblockbox.huntersdream.api.SilverFurnaceRecipe;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.BlockInit;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.blocks.BlockSilverFurnace;
import theblockbox.huntersdream.inventory.SideItemHandler;

import java.util.Optional;

public class TileEntitySilverFurnace extends TileEntity implements ITickable {
    public static final String KEY_ITEM_HANDLER = "itemHandler";
    public static final String KEY_FUEL = "fuel";
    public static final String KEY_TICKS = "ticks";
    public static final String KEY_AMOUNT1 = "amount1";
    public static final String KEY_AMOUNT2 = "amount2";
    public static final String KEY_FULL_BURN_TIME = "fullBurnTime";
    public static final String KEY_FULL_NEEDED_SMELTING_TIME = "fullNeededSmeltingTime";
    public static final String KEY_HAS_RECIPE = "hasRecipe";
    public static final String KEY_OUTPUT1 = "output1";
    public static final String KEY_OUTPUT2 = "output2";
    public static final String KEY_SMELTING_RECIPE_SINCE = "smeltingRecipeSince";

    private int ticks = 0;
    /**
     * The tick on which the current recipe has started smelting
     */
    private int smeltingRecipeSince;
    /**
     * The time that is needed to completely smelt the current recipe
     */
    private int fullNeededSmeltingTime;
    private int burnTime = 0;
    /**
     * The full burn time of the used fuel item (used for rendering)
     */
    private int fullBurnTime = 0;
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
    private final MutablePair<ItemStack, ItemStack> outputs = new MutablePair<>(ItemStack.EMPTY, ItemStack.EMPTY);
    private boolean hasRecipe = false;
    private final TileEntitySilverFurnace.SilverFurnaceItemHandler itemHandler = new TileEntitySilverFurnace.SilverFurnaceItemHandler();
    private final IItemHandler itemHandlerTop = new SideItemHandler(this.itemHandler, i -> i == 1 || i == 2,
            GeneralHelper.FALSE_PREDICATE);
    private final IItemHandler itemHandlerSide = new SideItemHandler(this.itemHandler, i -> i == 0,
            GeneralHelper.FALSE_PREDICATE);
    private final IItemHandler itemHandlerBottom = new SideItemHandler(this.itemHandler, GeneralHelper.FALSE_PREDICATE,
            i -> i == 3 || i == 4);

    @Override
    public void update() {
        // using 9 to make fuels with 10 ticks burn time work properly
        if (!this.world.isRemote && (this.ticks++ % 9 == 0)) {
            boolean wasBurning = this.isBurning();
            this.updateBurnTime(9);

            this.checkForRecipe();
            if (this.hasRecipe()) {
                if (this.isBurning()) {
                    if (this.ticks >= (this.smeltingRecipeSince + this.fullNeededSmeltingTime)) {
                        if (this.addItemsToOutputIfPossible(this.outputs.getLeft(), this.outputs.getRight())) {
                            this.itemHandler.extractItem(1, this.amount1, false);
                            this.itemHandler.extractItem(2, this.amount2, false);
                        }
                        this.setHasRecipe(false);
                        // the smelting process should be done by now
                    }
                } else {
                    this.setHasRecipe(false);
                }
            }
            this.checkForRecipe();

            // reset blockstate if burning state changed
            if (wasBurning != this.isBurning()) {
                this.world.setBlockState(this.pos,
                        this.world.getBlockState(this.pos).withProperty(BlockSilverFurnace.BURNING, this.isBurning()));
            }
        }
    }

    private void updateBurnTime(int toDecrease) {
        this.burnTime -= toDecrease;
        while (this.burnTime < 0) {
            // if there is the furnace has a recipe,
            if (this.hasRecipe()) {
                // get fuel in fuel slot
                int fuel = TileEntityFurnace.getItemBurnTime(this.getFuel());
                // if there is fuel and it could be extracted,
                if (fuel > 0 && !this.itemHandler.extractItem(0, 1, false).isEmpty()) {
                    // add fuel to burnTime
                    this.burnTime += fuel;
                    this.fullBurnTime = fuel;
                    continue;
                }
            }
            // if no fuel was found, the item couldn't be extracted or there was no recipe,
            // set fuel to 0 and go out
            // of the loop
            if (this.burnTime < 0)
                this.burnTime = 0;
            break;
        }
        this.markDirty();
    }

    /**
     * Checks for a fitting recipe first in the silver furnace, then in the
     * vanilla recipes. If a fitting one is found, the values are set and true
     * is returned, otherwise false is returned. This method can also be used to
     * test if a recipe still applies and set another one if the old one doesn't
     * apply
     */
    public boolean checkForRecipe() {
        ItemStack input = this.getInput1();

        Optional<SilverFurnaceRecipe> sfr = SilverFurnaceRecipe.getFromInput(input, this.getInput2());
        if (sfr.isPresent()) {
            SilverFurnaceRecipe recipe = sfr.get();
            return this.setRecipe(recipe.getAmount1(), recipe.getAmount2(), recipe.getOutput1(this.world.rand),
                    recipe.getOutput2(this.world.rand), recipe.getSmeltingTime());
        }

        ItemStack stack = FurnaceRecipes.instance().getSmeltingResult(input);
        if (!stack.isEmpty() && this.getInput2().isEmpty() && this.setRecipe(1, 0, stack.copy(), ItemStack.EMPTY, 200)) {
            return true;
        }

        this.setHasRecipe(false);
        return false;
    }

    /**
     * Tries to set the current recipe. Returns false if it wasn't successful
     * (meaning that the output items couldn't be added to the output)
     */
    private boolean setRecipe(int itemAmount1, int itemAmount2, ItemStack output1, ItemStack output2,
                              int neededSmeltingTime) {
        // make recipes burn faster because furnace smelts a bit slower
        int fullSmeltingTime = neededSmeltingTime - 12;
        if (this.canAddItemsToOutput(output1, output2)) {
            // if everything is still the same, just return true and do nothing
            if (this.hasRecipe() && (this.amount1 == itemAmount1) && (this.amount2 == itemAmount2)
                    && ItemStack.areItemStacksEqual(this.outputs.getLeft(), output1)
                    && ItemStack.areItemStacksEqual(this.outputs.getRight(), output2)
                    && this.fullNeededSmeltingTime == fullSmeltingTime) {
                return true;
            }

            this.amount1 = itemAmount1;
            this.amount2 = itemAmount2;
            this.outputs.setLeft(output1);
            this.outputs.setRight(output2);
            this.fullNeededSmeltingTime = fullSmeltingTime;
            this.smeltingRecipeSince = this.ticks;
            this.setHasRecipe(true);
            return true;
        }
        this.setHasRecipe(false);
        return false;
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
        return newState.getBlock() != BlockInit.SILVER_FURNACE;
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

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getFullBurnTime() {
        return this.fullBurnTime;
    }

    public int getSmeltingRecipeSince() {
        return this.smeltingRecipeSince;
    }

    public int getFullNeededSmeltingTime() {
        return this.fullNeededSmeltingTime;
    }

    public int getTicks() {
        return this.ticks;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey(TileEntitySilverFurnace.KEY_ITEM_HANDLER))
            CapabilitiesInit.CAPABILITY_ITEM_HANDLER.readNBT(this.itemHandler, null, compound.getTag(TileEntitySilverFurnace.KEY_ITEM_HANDLER));
        if (compound.hasKey(TileEntitySilverFurnace.KEY_FUEL))
            this.burnTime = compound.getInteger(TileEntitySilverFurnace.KEY_FUEL);
        if (compound.hasKey(TileEntitySilverFurnace.KEY_TICKS))
            this.ticks = compound.getInteger(TileEntitySilverFurnace.KEY_TICKS);
        if (compound.hasKey(TileEntitySilverFurnace.KEY_AMOUNT1))
            this.amount1 = compound.getInteger(TileEntitySilverFurnace.KEY_AMOUNT1);
        if (compound.hasKey(TileEntitySilverFurnace.KEY_AMOUNT2))
            this.amount2 = compound.getInteger(TileEntitySilverFurnace.KEY_AMOUNT2);
        if (compound.hasKey(TileEntitySilverFurnace.KEY_FULL_BURN_TIME))
            this.fullBurnTime = compound.getInteger(TileEntitySilverFurnace.KEY_FULL_BURN_TIME);
        if (compound.hasKey(TileEntitySilverFurnace.KEY_FULL_NEEDED_SMELTING_TIME))
            this.fullNeededSmeltingTime = compound.getInteger(TileEntitySilverFurnace.KEY_FULL_NEEDED_SMELTING_TIME);
        if (compound.hasKey(TileEntitySilverFurnace.KEY_HAS_RECIPE))
            this.hasRecipe = compound.getBoolean(TileEntitySilverFurnace.KEY_HAS_RECIPE);
        if (compound.hasKey(TileEntitySilverFurnace.KEY_OUTPUT1))
            this.outputs.setLeft(new ItemStack(compound.getCompoundTag(TileEntitySilverFurnace.KEY_OUTPUT1)));
        if (compound.hasKey(TileEntitySilverFurnace.KEY_OUTPUT2))
            this.outputs.setRight(new ItemStack(compound.getCompoundTag(TileEntitySilverFurnace.KEY_OUTPUT2)));
        if (compound.hasKey(TileEntitySilverFurnace.KEY_SMELTING_RECIPE_SINCE))
            this.smeltingRecipeSince = compound.getInteger(TileEntitySilverFurnace.KEY_SMELTING_RECIPE_SINCE);
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag(TileEntitySilverFurnace.KEY_ITEM_HANDLER, CapabilitiesInit.CAPABILITY_ITEM_HANDLER.writeNBT(this.itemHandler, null));
        compound.setInteger(TileEntitySilverFurnace.KEY_FUEL, this.burnTime);
        compound.setInteger(TileEntitySilverFurnace.KEY_TICKS, this.ticks);
        compound.setInteger(TileEntitySilverFurnace.KEY_AMOUNT1, this.amount1);
        compound.setInteger(TileEntitySilverFurnace.KEY_AMOUNT2, this.amount2);
        compound.setInteger(TileEntitySilverFurnace.KEY_FULL_BURN_TIME, this.fullBurnTime);
        compound.setInteger(TileEntitySilverFurnace.KEY_FULL_NEEDED_SMELTING_TIME, this.fullNeededSmeltingTime);
        compound.setBoolean(TileEntitySilverFurnace.KEY_HAS_RECIPE, this.hasRecipe);
        compound.setTag(TileEntitySilverFurnace.KEY_OUTPUT1, this.outputs.getLeft().serializeNBT());
        compound.setTag(TileEntitySilverFurnace.KEY_OUTPUT2, this.outputs.getRight().serializeNBT());
        compound.setInteger(TileEntitySilverFurnace.KEY_SMELTING_RECIPE_SINCE, this.smeltingRecipeSince);
        return super.writeToNBT(compound);
    }

    public NonNullList<ItemStack> getInventory() {
        return this.itemHandler.getInventory();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilitiesInit.CAPABILITY_ITEM_HANDLER) || super.hasCapability(capability, facing);
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

    private class SilverFurnaceItemHandler extends ItemStackHandler {

        private SilverFurnaceItemHandler() {
            super(5);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (TileEntitySilverFurnace.this.world != null && !TileEntitySilverFurnace.this.world.isRemote) {
                TileEntitySilverFurnace.this.markDirty();
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return TileEntityFurnace.isItemFuel(stack);
            } else return slot != 3 && slot != 4;
        }

        private NonNullList<ItemStack> getInventory() {
            return this.stacks;
        }
    }
}
