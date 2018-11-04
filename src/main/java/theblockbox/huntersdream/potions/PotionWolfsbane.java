package theblockbox.huntersdream.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf;

public class PotionWolfsbane extends PotionBase {
	public PotionWolfsbane() {
		super(false, 14811307, 0, "wolfsbane");
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn,
			AbstractAttributeMap attributeMapIn, int amplifier) {
		// not actually applying attribute modifiers
		entityLivingBaseIn.addPotionEffect(new PotionEffect(
				(TransformationHelper.getTransformation(entityLivingBaseIn) == TransformationInit.WEREWOLF)
						? MobEffects.HUNGER
						: MobEffects.POISON,
				100, 2, false, false));
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void performEffect(EntityLivingBase entityIn, int amplifier) {
		ITransformation transformation = TransformationHelper.getITransformation(entityIn);
		if (transformation != null && transformation.getTransformation() == TransformationInit.WEREWOLF
				&& !WerewolfHelper.isTransformed(entityIn)) {
			if (entityIn.isPotionActive(PotionInit.POTION_WOLFSBANE)) {
				entityIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 110));
				entityIn.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100));
				if (entityIn instanceof EntityPlayer) {
					// reset transformation
					IWerewolf were = WerewolfHelper.getIWerewolf((EntityPlayer) entityIn);
					were.setTransformationStage(0);
					were.setTimeSinceTransformation(-1);
				}
			}
		}

		// remove infection
		IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entityIn);
		if (entityIn.isPotionActive(PotionInit.POTION_WOLFSBANE) && ionm != null && ionm.isInfected()) {
			ionm.setInfectionStatus(InfectionStatus.NOT_INFECTED);
			ionm.setInfectionTick(-1);
			ionm.setInfectionTransformation(TransformationInit.HUMAN);
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 0;
	}
}
