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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.event.TransformationEvent.TransformationEventReason;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class ItemBestiary extends Item {
	public ItemBestiary() {
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack heldStack = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote) {
			if (playerIn.isCreative()) {
				Transformation transformation = TransformationHelper.getTransformation(playerIn).cycle();
				TransformationHelper.changeTransformation(playerIn, transformation, TransformationEventReason.BESTIARY);
				playerIn.sendMessage(new TextComponentTranslation(this.getTranslationKey() + ".onClick",
						transformation.getTranslation()));
				return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
			} else {
				return new ActionResult<>(EnumActionResult.FAIL, heldStack);
			}
		} else {
			return new ActionResult<>(EnumActionResult.PASS, heldStack);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format(this.getTranslationKey() + ".tooltip"));
	}
}
