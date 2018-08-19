package theblockbox.huntersdream.init;

import java.util.ArrayList;

import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import theblockbox.huntersdream.potions.PotionFear;
import theblockbox.huntersdream.potions.PotionTypeBase;
import theblockbox.huntersdream.potions.PotionWolfsbane;

public class PotionInit {
	public static final ArrayList<Potion> POTIONS = new ArrayList<>();
	public static final ArrayList<PotionType> POTION_TYPES = new ArrayList<>();

	public static final Potion POTION_FEAR = new PotionFear();
	public static final Potion POTION_WOLFSBANE = new PotionWolfsbane();

	public static final PotionType WOLFSBANE = new PotionTypeBase("wolfsbane",
			new PotionEffect(POTION_WOLFSBANE, 12000));

	public static void registerPotionTypes() {
		WOLFSBANE.setRegistryName(POTION_WOLFSBANE.getRegistryName());
		PotionHelper.addMix(PotionTypes.AWKWARD, ItemInit.WOLFSBANE, WOLFSBANE);
	}
}
