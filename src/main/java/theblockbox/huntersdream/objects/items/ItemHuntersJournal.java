package theblockbox.huntersdream.objects.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ItemHuntersJournal extends ItemBase {

	public ItemHuntersJournal(String name) {
		super(name);
		setMaxStackSize(1);
	}

	public void openBook(EntityPlayer player, ItemStack stack, EnumHand hand) {
		Item item = stack.getItem();
		if (item == this) {
			if (player.world.isRemote) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiScreenBook(player, stack, false));
			}
		}
	}
}
