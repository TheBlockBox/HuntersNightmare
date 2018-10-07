package theblockbox.huntersdream.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySilverFurnace;
import theblockbox.huntersdream.init.CapabilitiesInit;

public class ContainerFurnaceSilver extends Container {
	private final TileEntitySilverFurnace tileEntity;
	private final World world;
	private final BlockPos pos;
	private final IInventory playerInventory;

	public ContainerFurnaceSilver(IInventory playerInv, TileEntitySilverFurnace tileEntity) {
		this.tileEntity = tileEntity;
		this.world = tileEntity.getWorld();
		this.pos = tileEntity.getPos();
		this.playerInventory = playerInv;
		this.addSlots(playerInv);
	}

	public static boolean isSlotFurnaceSlot(int slotIndex) {
		return slotIndex <= 4;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.world.getTileEntity(this.pos) != this.tileEntity ? false
				: playerIn.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
						this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public IInventory getPlayerInventory() {
		return this.playerInventory;
	}

	private void addSlots(IInventory playerInv) {
		// furnace
		IItemHandler itemHandler = this.tileEntity.getCapability(CapabilitiesInit.CAPABILITY_ITEM_HANDLER, null);
		int index = 0;
		this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 38, 53));
		this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 20, 17));
		this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 56, 17));
		this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 116, 19));
		this.addSlotToContainer(new SlotItemHandler(itemHandler, index++, 116, 51));

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
		ItemStack result = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			result = stack.copy();

			if (slotIndex == 2) {
				if (!this.mergeItemStack(stack, 3, 39, true)) {
					return ItemStack.EMPTY;
				}
				slot.onSlotChange(stack, result);
			} else if (slotIndex != 1 && slotIndex != 0) {
				if (!FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty()) {
					if (!this.mergeItemStack(stack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (TileEntityFurnace.isItemFuel(stack)) {
					if (!this.mergeItemStack(stack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotIndex >= 3 && slotIndex < 30) {
					if (!this.mergeItemStack(stack, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotIndex >= 30 && slotIndex < 39 && !this.mergeItemStack(stack, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(stack, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (stack.getCount() == result.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, stack);
		}

		return result;
	}
}
