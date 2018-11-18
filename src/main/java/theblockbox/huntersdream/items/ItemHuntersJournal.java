package theblockbox.huntersdream.items;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class ItemHuntersJournal extends Item {

	public ItemHuntersJournal() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		HuntersJournalPage[] pages = TransformationHelper.getITransformationPlayer(playerIn).getUnlockedPages();
		if (pages.length <= 0) {
			if (!worldIn.isRemote) {
				playerIn.sendMessage(new TextComponentTranslation(this.getTranslationKey() + ".noPage"));
			}
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		} else {
			Main.proxy.openHuntersJournal(playerIn, pages);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.GRAY + I18n.format("book.byAuthor", "Scarlet"));
	}
}