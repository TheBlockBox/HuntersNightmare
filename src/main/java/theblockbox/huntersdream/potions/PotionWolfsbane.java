package theblockbox.huntersdream.potions;

import java.util.Optional;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;

public class PotionWolfsbane extends PotionBase {
	public PotionWolfsbane() {
		super(false, 14811307, 0, "wolfsbane");
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn,
			AbstractAttributeMap attributeMapIn, int amplifier) {
		// not actually applying attribute modifiers
		entityLivingBaseIn.addPotionEffect(
				new PotionEffect((TransformationHelper.getTransformation(entityLivingBaseIn) == Transformation.WEREWOLF)
						? MobEffects.HUNGER
						: MobEffects.POISON, 100, 2, false, false));
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
	}

	@Override
	public void performEffect(EntityLivingBase entityIn, int amplifier) {
		Optional<ITransformation> transformation = TransformationHelper.getITransformation(entityIn);
		if (transformation.isPresent() && transformation.get().getTransformation() == Transformation.WEREWOLF
				&& !WerewolfHelper.isTransformed(entityIn)) {
			if (entityIn.isPotionActive(PotionInit.POTION_WOLFSBANE)) {
				entityIn.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 110));
				entityIn.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100));
				if (entityIn instanceof EntityPlayerMP) {
					// reset transformation
					EntityPlayerMP player = (EntityPlayerMP) entityIn;
					WerewolfHelper.setTransformationStage(player, 0);
					WerewolfHelper.setTimeSinceTransformation(player, -1);
				}
			}
		}

		// remove infection
		WerewolfHelper.getIInfectOnNextMoon(entityIn).ifPresent(ionm -> {
			if (entityIn.isPotionActive(PotionInit.POTION_WOLFSBANE) && ionm.isInfected()) {
				ionm.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.NOT_INFECTED);
				ionm.setInfectionTick(-1);
				ionm.setInfectionTransformation(Transformation.HUMAN);
			}
		});
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 0;
	}
}
