package theblockbox.huntersdream.potions;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;

public class PotionWolfsbane extends PotionBase {
	public static final HashMap<EntityLivingBase, Integer> APPLIED_AT_TIME = new HashMap<>();

	public PotionWolfsbane() {
		super(false, 14811307, "wolfsbane");
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return Lists.newArrayList(new ItemStack(Items.MILK_BUCKET));
	}

	@Override
	public void performEffect(EntityLivingBase entityIn, int amplifier) {
		if (entityIn.ticksExisted % 20 == 0) {
			if (entityIn.hasCapability(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON, null)) {
				IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entityIn);
				if (ionm.isInfected()) {
					ionm.setInfectionStatus(InfectionStatus.NOT_INFECTED);
					ionm.setInfectionTick(-1);
					ionm.setInfectionTransformation(Transformations.HUMAN);
				}
			}
			if (WerewolfHelper.transformedWerewolf(entityIn)) {
				entityIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 2));
				entityIn.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 2));
			}

			if (!APPLIED_AT_TIME.containsKey(entityIn)) {
				APPLIED_AT_TIME.put(entityIn, entityIn.ticksExisted);
				if (WerewolfHelper.transformedWerewolf(entityIn)) {
					entityIn.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
				} else {
					entityIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100));
				}
			}

			if (entityIn.ticksExisted % 10000 == 0)
				APPLIED_AT_TIME.entrySet().stream().filter(e -> !e.getKey().isPotionActive(this))
						.forEach(e -> APPLIED_AT_TIME.remove(e.getKey()));
		}
	}
}
