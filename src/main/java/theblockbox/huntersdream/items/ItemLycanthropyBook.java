package theblockbox.huntersdream.items;

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
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;

import java.util.List;

public class ItemLycanthropyBook extends Item {
    public ItemLycanthropyBook() {
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack heldStack = playerIn.getHeldItem(handIn);
        if (TransformationHelper.getTransformation(playerIn) == Transformation.WEREWOLF) {
            Main.proxy.openSkillTab(playerIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
        } else {
            playerIn.sendStatusMessage(new TextComponentTranslation(this.getTranslationKey() + ".onClick.wrong_transformation"), true);
        }
        return new ActionResult<>(EnumActionResult.FAIL, heldStack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(this.getTranslationKey() + ".tooltip"));
    }
}
