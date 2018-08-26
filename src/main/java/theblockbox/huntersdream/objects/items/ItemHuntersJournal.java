package theblockbox.huntersdream.objects.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import theblockbox.huntersdream.gui.GuiHuntersJournal;

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

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiHuntersJournal(playerIn));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}
}
