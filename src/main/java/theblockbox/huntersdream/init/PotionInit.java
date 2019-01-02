package theblockbox.huntersdream.init;

import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import theblockbox.huntersdream.potions.PotionFear;
import theblockbox.huntersdream.potions.PotionSunscreen;
import theblockbox.huntersdream.potions.PotionWolfsbane;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class PotionInit {

	public static final Potion POTION_FEAR = new PotionFear();
	public static final Potion POTION_WOLFSBANE = new PotionWolfsbane();
	public static final Potion POTION_SUNSCREEN = new PotionSunscreen();

	public static final PotionType WOLFSBANE = new PotionType("wolfsbane", new PotionEffect(PotionInit.POTION_WOLFSBANE, 12000))
			.setRegistryName(GeneralHelper.newResLoc("wolfsbane"));

	public static void registerPotions(RegistryEvent.Register<Potion> event) {
		event.getRegistry().registerAll(PotionInit.POTION_FEAR, PotionInit.POTION_WOLFSBANE, PotionInit.POTION_SUNSCREEN);
	}

	public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
		event.getRegistry().registerAll(PotionInit.WOLFSBANE);
		PotionHelper.addMix(PotionTypes.AWKWARD, ItemInit.WOLFSBANE_FLOWER, PotionInit.WOLFSBANE);
	}
}
