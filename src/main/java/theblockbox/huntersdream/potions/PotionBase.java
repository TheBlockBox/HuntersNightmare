package theblockbox.huntersdream.potions;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.Reference;

public abstract class PotionBase extends Potion {

	protected PotionBase(boolean isBadEffectIn, int liquidColorIn, String name) {
		super(isBadEffectIn, liquidColorIn);

		setRegistryName(new ResourceLocation(Reference.MODID + ":" + name));
		setPotionName("effect." + name);

		PotionInit.POTIONS.add(this);
	}

}
