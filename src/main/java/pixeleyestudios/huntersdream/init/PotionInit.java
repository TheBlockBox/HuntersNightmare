package pixeleyestudios.huntersdream.init;

import java.util.ArrayList;

import net.minecraft.potion.Potion;
import pixeleyestudios.huntersdream.potions.PotionFear;
import pixeleyestudios.huntersdream.potions.PotionLupumVocant;

public class PotionInit {
	public static final ArrayList<Potion> POTIONS = new ArrayList<>();

	public static final Potion POTION_LUPUM_VOCANT = new PotionLupumVocant();
	public static final Potion POTION_FEAR = new PotionFear();
}
