package pixeleyestudios.huntersdream.potions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.helpers.WerewolfHelper;

/**
 * Applied when you got bitten by a werewolf
 */
public class PotionLupumVocant extends PotionBase {
	public static final String NAME = "lupumvocant";
	public static final String POTION_NAME = "effect." + NAME;

	public PotionLupumVocant() {
		super(true, 14769882, NAME);
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		// tests if the entity is player on server side
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			player.getActivePotionEffects().forEach(e -> {
				if (e.getEffectName().equals(POTION_NAME)) {
					// PotionEffect#getEffectName() always equals Potion#getPotionName
					if (e.getDuration() <= 100) {
						// five seconds before the potion ends
						if (WerewolfHelper.isWerewolfTime(player)) {
							TransformationHelper.changeTransformationWhenPossible(player, Transformations.WEREWOLF);
						} else {
							System.out.println("Did someone change the time until the next full moon?");
							System.out.println("Reapplying Lupum Vocant potion effect...");

							// removes potion effect and reapplies it for another 30 seconds
							player.removeActivePotionEffect(this);
							player.removePotionEffect(this);
							player.addPotionEffect(new PotionEffect(new PotionLupumVocant(), (30 * 20)));
						}
					}
				} else {
					return;
				}
			});
		}
	}

	// TODO: Add curative items
	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> items = new ArrayList<>();

		return items;
	}

}
