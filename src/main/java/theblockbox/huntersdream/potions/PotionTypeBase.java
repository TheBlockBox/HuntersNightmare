package theblockbox.huntersdream.potions;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import theblockbox.huntersdream.init.PotionInit;

public class PotionTypeBase extends PotionType {
	public PotionTypeBase(String baseName, PotionEffect... potionEffects) {
		super(baseName, potionEffects);
		PotionInit.POTION_TYPES.add(this);
	}
}
