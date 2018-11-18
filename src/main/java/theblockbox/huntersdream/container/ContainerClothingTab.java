package theblockbox.huntersdream.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;
import theblockbox.huntersdream.inventory.ItemHandlerClothingTab;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class ContainerClothingTab extends Container {
	public final IInventory playerInventory;
	public final EntityPlayer player;
	public final ItemHandlerClothingTab clothingTab;

	public ContainerClothingTab(EntityPlayer player) {
		this.player = player;
		this.playerInventory = this.player.inventory;
		this.clothingTab = TransformationHelper.getITransformationPlayer(this.player).getClothingTab();
		this.addSlots(this.playerInventory);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	private void addSlots(IInventory playerInv) {
		// clothes

		int index = 0;
		for (int col = 0; col < 4; col++) {
			// hat/shirt/trousers/shoe
			this.addSlotToContainer(new SlotItemHandler(this.clothingTab, index++, 45, (col * 18) + 7));
			// mask/coat/glove/cloak
			this.addSlotToContainer(new SlotItemHandler(this.clothingTab, index++, 144, (col * 18) + 7));
		}
		// accessories
		this.addSlotToContainer(new SlotItemHandler(this.clothingTab, index++, 132, 43));
		// backpack
		this.addSlotToContainer(new SlotItemHandler(this.clothingTab, index++, 132, 61));

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
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
	}
}
