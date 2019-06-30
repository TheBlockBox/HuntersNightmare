package theblockbox.huntersdream.blocks.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.blocks.BlockSpinningWheel;

public class TileEntitySpinningWheel extends TileEntity implements ITickable {
    public static final String KEY_ITEM_HANDLER = "itemHandler";
    public static final String KEY_TICKS = "ticks";
    public static final String KEY_WORKING_SINCE = "workingSince";

    private final TileEntitySpinningWheel.SpinningWheelItemHandler itemHandler = new TileEntitySpinningWheel.SpinningWheelItemHandler();
    private int ticks = 0;
    private int workingSince = Integer.MAX_VALUE;

    @Override
    public void update() {
        if (!this.world.isRemote && (this.ticks++ % 20 == 0)) {
            ItemStack shears = this.itemHandler.getStackInSlot(1);
            ItemStack output = this.itemHandler.getStackInSlot(2);
            // if the output isn't full and there are items to be processed,
            if ((output.getMaxStackSize() > output.getCount()) && !this.itemHandler.getStackInSlot(0).isEmpty()
                    && !shears.isEmpty()) {
                // test if the work is done
                if ((this.ticks - this.getNeededProcessingTime()) > this.workingSince) {
                    this.workingSince = Integer.MAX_VALUE;
                    this.itemHandler.extractItem(0, 1, false);
                    ItemStack newShears = shears.copy();
                    if (newShears.attemptDamageItem(1, this.world.rand, null)) {
                        this.world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                                SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
                        newShears = ItemStack.EMPTY;
                    }
                    this.itemHandler.setStackInSlot(1, newShears);
                    this.world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                            SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.itemHandler.insertItem(2, new ItemStack(ItemInit.FABRIC), false);
                } else if (!this.isWorking()) {
                    this.workingSince = this.ticks;
                }
            } else {
                this.workingSince = Integer.MAX_VALUE;
            }
        }
    }

    @Override
    public boolean shouldRefresh(World worldIn, BlockPos blockPos, IBlockState oldState, IBlockState newState) {
        return !(newState.getBlock() instanceof BlockSpinningWheel);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilitiesInit.CAPABILITY_ITEM_HANDLER) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return (capability == CapabilitiesInit.CAPABILITY_ITEM_HANDLER) ? (T) this.itemHandler : super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey(TileEntitySpinningWheel.KEY_ITEM_HANDLER))
            CapabilitiesInit.CAPABILITY_ITEM_HANDLER.readNBT(this.itemHandler, null, compound.getTag(TileEntitySpinningWheel.KEY_ITEM_HANDLER));
        if (compound.hasKey(TileEntitySpinningWheel.KEY_TICKS))
            this.ticks = compound.getInteger(TileEntitySpinningWheel.KEY_TICKS);
        if (compound.hasKey(TileEntitySpinningWheel.KEY_WORKING_SINCE))
            this.workingSince = compound.getInteger(TileEntitySpinningWheel.KEY_WORKING_SINCE);
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag(TileEntitySpinningWheel.KEY_ITEM_HANDLER, CapabilitiesInit.CAPABILITY_ITEM_HANDLER.writeNBT(this.itemHandler, null));
        compound.setInteger(TileEntitySpinningWheel.KEY_TICKS, this.ticks);
        compound.setInteger(TileEntitySpinningWheel.KEY_WORKING_SINCE, this.workingSince);
        return super.writeToNBT(compound);
    }

    public int getTicks() {
        return this.ticks;
    }

    public int getWorkingSince() {
        return this.workingSince;
    }

    public boolean isWorking() {
        return this.workingSince != Integer.MAX_VALUE;
    }

    public int getNeededProcessingTime() {
        return 200;
    }

    public NonNullList<ItemStack> getInventory() {
        return this.itemHandler.getInventory();
    }

    private class SpinningWheelItemHandler extends ItemStackHandler {
        private SpinningWheelItemHandler() {
            super(3);
        }

        public NonNullList<ItemStack> getInventory() {
            return this.stacks;
        }

        @Override
        protected void onContentsChanged(int slot) {
            if ((TileEntitySpinningWheel.this.world != null) && !TileEntitySpinningWheel.this.world.isRemote) {
                TileEntitySpinningWheel.this.markDirty();
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return ((slot == 0) && GeneralHelper.isCotton(stack)) || ((slot == 1) && (stack.getItem() instanceof ItemShears));
        }
    }
}
