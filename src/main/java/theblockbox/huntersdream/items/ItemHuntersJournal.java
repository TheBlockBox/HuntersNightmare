package theblockbox.huntersdream.items;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.gui.GuiHuntersJournal;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class ItemHuntersJournal extends ItemBase {

	public ItemHuntersJournal(String name) {
		super(name);
		setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote) {
			ITransformationPlayer cap = TransformationHelper.getCap(playerIn);
			if (cap.getUnlockedPages().length == 0) {
				playerIn.sendMessage(new TextComponentTranslation(this.getUnlocalizedName() + ".noPage"));
				return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
			} else {
				Minecraft.getMinecraft().displayGuiScreen(new GuiHuntersJournal(playerIn, cap.getUnlockedPages()));
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}
}