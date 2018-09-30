package theblockbox.huntersdream.potions;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class PotionTypeBase extends PotionType {
	public PotionTypeBase(String name, PotionEffect... potionEffects) {
		super(name, potionEffects);
		this.setRegistryName(GeneralHelper.newResLoc(name));
		PotionInit.POTION_TYPES.add(this);
	}
}
