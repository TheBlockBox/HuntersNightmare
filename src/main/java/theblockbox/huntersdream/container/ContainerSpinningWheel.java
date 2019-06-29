package theblockbox.huntersdream.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySpinningWheel;

public class ContainerSpinningWheel extends Container {
    public final TileEntitySpinningWheel tileEntity;
    public final World world;
    public final BlockPos pos;
    public final IInventory playerInventory;
    private int workingSince;
    private int ticks;

    public ContainerSpinningWheel(IInventory playerInv, TileEntitySpinningWheel tileEntity) {
        this.tileEntity = tileEntity;
        this.world = tileEntity.getWorld();
        this.pos = tileEntity.getPos();
        this.playerInventory = playerInv;
        this.addSlots(playerInv);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.world.getTileEntity(this.pos) == this.tileEntity && playerIn.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
                this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public IInventory getPlayerInventory() {
        return this.playerInventory;
    }

    private void addSlots(IInventory playerInv) {
        // spinning wheel
        IItemHandler itemHandler = this.tileEntity.getCapability(CapabilitiesInit.CAPABILITY_ITEM_HANDLER, null);
        int index = 0;
        this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 38, 35));
        this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 56, 35));
        this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 116, 35));

        // player
        // inventory
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; ++col)
                this.addSlotToContainer(new Slot(playerInv, row * 9 + col + 9, col * 18 + 8, row * 18 + 84));

        // hotbar
        for (int row = 0; row < 9; ++row)
            this.addSlotToContainer(new Slot(playerInv, row, row * 18 + 8, 142));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotIndex) {
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();

            // if it's a furnace slot,
            if (slotIndex < 3) {
                // try to move to inventory or hotbar
                if (!this.tryMove(stack, 3, 30))
                    this.tryMove(stack, 30, 39);
            } else {
                // try to move it into the cotton or shear slots
                if (!((GeneralHelper.itemStackHasOreDict(stack, "cotton") && this.tryMove(stack, 0, 1))
                        || ((stack.getItem() instanceof ItemShears) && this.tryMove(stack, 1, 2)))) {
                    // otherwise move it into the inventory
                    boolean inventory = slotIndex < 30;
                    this.tryMove(stack, inventory ? 30 : 3, inventory ? 39 : 30);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private boolean tryMove(ItemStack stack, int beginInclusive, int endExclusive) {
        if (this.mergeItemStack(stack, beginInclusive, endExclusive, false)) {
            return true;
        } else {
            for (int i = beginInclusive; i < endExclusive; i++) {
                Slot slot = this.inventorySlots.get(i);
                if (slot != null && !slot.getHasStack()) {
                    slot.putStack(stack);
                    slot.onSlotChanged();
                    return true;
                }
            }
        }
        return false;
    }

    public int getWorkingSince() {
        return this.workingSince;
    }

    public int getTicks() {
        return this.ticks;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            int fullBurn = this.tileEntity.getWorkingSince();
            if (this.workingSince != fullBurn) {
                this.workingSince = fullBurn;
                listener.sendWindowProperty(this, 0, fullBurn);
            }

            int teTicks = this.tileEntity.getTicks();
            if (this.ticks != teTicks) {
                this.ticks = teTicks;
                listener.sendWindowProperty(this, 1, teTicks);
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        switch (id) {
            case 0:
                this.workingSince = data;
                break;
            case 1:
                this.ticks = data;
                break;
            default:
                Main.getLogger().error("Received data with unknown id " + id + " and value " + data);
                break;
        }
    }
}
