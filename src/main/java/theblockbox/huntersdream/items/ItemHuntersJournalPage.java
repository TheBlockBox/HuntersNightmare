package theblockbox.huntersdream.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class ItemHuntersJournalPage extends ItemBase {

	public ItemHuntersJournalPage(String name) {
		super(name);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!playerIn.world.isRemote) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			ITransformationPlayer cap = TransformationHelper.getCap(playerIn);
			HuntersJournalPage page = cap.getRandomNotUnlockedPage();
			if (page != null) {
				cap.unlockPage(page);
				if (!playerIn.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				PacketHandler.sendTransformationMessage((EntityPlayerMP) playerIn);
				playerIn.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".onPageUnlock",
						new TextComponentTranslation(page.getTitle())));
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			} else {
				playerIn.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".allUnlocked"));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			}
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
