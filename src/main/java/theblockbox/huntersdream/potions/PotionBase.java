package theblockbox.huntersdream.potions;

import net.minecraft.potion.Potion;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public abstract class PotionBase extends Potion {

	protected PotionBase(boolean isBadEffectIn, int liquidColorIn, String name) {
		super(isBadEffectIn, liquidColorIn);

		setRegistryName(GeneralHelper.newResLoc(name));
		setPotionName("effect." + Reference.MODID + "." + name);

		PotionInit.POTIONS.add(this);
	}

}
