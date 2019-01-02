package theblockbox.huntersdream.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class ItemHuntersJournalPage extends Item {

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!playerIn.world.isRemote) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(playerIn);
			HuntersJournalPage page = cap.getRandomNotUnlockedPage(playerIn.getRNG());
			if (page != null) {
				if (!playerIn.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				cap.unlockPage(page);
				PacketHandler.sendTransformationMessage((EntityPlayerMP) playerIn);
				playerIn.sendMessage(new TextComponentTranslation(this.getTranslationKey() + ".onPageUnlock",
						new TextComponentTranslation(page.getTitle())));
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			} else {
				playerIn.sendMessage(new TextComponentTranslation(this.getTranslationKey() + ".allUnlocked"));
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
