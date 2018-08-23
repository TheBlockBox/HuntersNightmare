package theblockbox.huntersdream.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public abstract class PotionBase extends Potion {

	public PotionBase(boolean isBadEffectIn, int liquidColorIn, int iconIndex, String name) {
		super(isBadEffectIn, liquidColorIn);

		setRegistryName(GeneralHelper.newResLoc(name));
		setPotionName("effect." + Reference.MODID + "." + name);
		setIconIndex(iconIndex % 6, MathHelper.floor(iconIndex / 6.0D));

		PotionInit.POTIONS.add(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasStatusIcon() {
		Minecraft.getMinecraft().renderEngine.bindTexture(GeneralHelper.newResLoc("textures/gui/potion_icons.png"));
		return true;
	}
}
