package theblockbox.huntersdream.container;

import org.apache.commons.lang3.BooleanUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySilverFurnace;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.inventory.SlotOutput;

public class ContainerSilverFurnace extends Container {
	public final TileEntitySilverFurnace tileEntity;
	public final World world;
	public final BlockPos pos;
	public final IInventory playerInventory;
	private int fullBurnTime;
	private int burnTime;
	private int ticks;
	private int smeltingRecipeSince;
	private int fullNeededSmeltingTime;
	private boolean hasRecipe;

	public ContainerSilverFurnace(IInventory playerInv, TileEntitySilverFurnace tileEntity) {
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
		return this.world.getTileEntity(this.pos) == this.tileEntity && playerIn.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D,
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
		this.addSlotToContainer(new SlotOutput(itemHandler, index++, 116, 19, this.tileEntity));
		this.addSlotToContainer(new SlotOutput(itemHandler, index++, 116, 51, this.tileEntity));

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
			if (slotIndex < 5) {
				// try to move to inventory or hotbar
				if (!this.tryMove(stack, 5, 32))
					this.tryMove(stack, 32, 41);
				// make sure the te is not thinking it's still cooking something
				this.tileEntity.checkForRecipe();
			} else {
				boolean inventory = slotIndex < 31;
				// if there's fuel try to move it there
				if (!(TileEntityFurnace.isItemFuel(stack) && this.tryMove(stack, 0, 1)))
					// else if there's no recipe try to move the stack into the input slots
					if (!(!this.hasRecipe() && this.tryMove(stack, 1, 3)))
						this.tryMove(stack, inventory ? 32 : 5, inventory ? 41 : 32);
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

	public int getBurnTime() {
		return this.burnTime;
	}

	public int getFullBurnTime() {
		return this.fullBurnTime;
	}

	public int getSmeltingRecipeSince() {
		return this.smeltingRecipeSince;
	}

	public int getTicks() {
		return this.ticks;
	}

	public int getFullNeededSmeltingTime() {
		return this.fullNeededSmeltingTime;
	}

	public boolean hasRecipe() {
		return this.hasRecipe;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : this.listeners) {
			int burn = this.tileEntity.getBurnTime();
			if (this.burnTime != burn) {
				this.burnTime = burn;
				listener.sendWindowProperty(this, 0, burn);
			}

			int fullBurn = this.tileEntity.getFullBurnTime();
			if (this.fullBurnTime != fullBurn) {
				this.fullBurnTime = fullBurn;
				listener.sendWindowProperty(this, 1, fullBurn);
			}

			int fullSmeltingTime = this.tileEntity.getFullNeededSmeltingTime();
			if (this.fullNeededSmeltingTime != fullSmeltingTime) {
				this.fullNeededSmeltingTime = fullSmeltingTime;
				listener.sendWindowProperty(this, 2, fullSmeltingTime);
			}

			int teTicks = this.tileEntity.getTicks();
			if (this.ticks != teTicks) {
				this.ticks = teTicks;
				listener.sendWindowProperty(this, 3, teTicks);
			}

			int smeltingSince = this.tileEntity.getSmeltingRecipeSince();
			if (this.smeltingRecipeSince != smeltingSince) {
				this.smeltingRecipeSince = smeltingSince;
				listener.sendWindowProperty(this, 4, smeltingSince);
			}

			boolean teHasRecipe = this.tileEntity.hasRecipe();
			if (this.hasRecipe != teHasRecipe) {
				this.hasRecipe = teHasRecipe;
				listener.sendWindowProperty(this, 5, BooleanUtils.toInteger(teHasRecipe));
			}
		}
	}

	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		switch (id) {
		case 0:
			this.burnTime = data;
			break;
		case 1:
			this.fullBurnTime = data;
			break;
		case 2:
			this.fullNeededSmeltingTime = data;
			break;
		case 3:
			this.ticks = data;
			break;
		case 4:
			this.smeltingRecipeSince = data;
			break;
		case 5:
			this.hasRecipe = BooleanUtils.toBoolean(data);
			break;
		default:
			Main.getLogger().error("Received data with unknown id " + id + " and value " + data);
			break;
		}
	}
}
