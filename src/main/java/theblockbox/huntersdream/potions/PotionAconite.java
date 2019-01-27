package theblockbox.huntersdream.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;

public class PotionAconite extends PotionBase {
	public PotionAconite() {
		super(false, 16769280, 0, "aconite");
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		// remove infection
		WerewolfHelper.getIInfectOnNextMoon(entityIn).ifPresent(ionm -> {
			if (ionm.isInfected()) {
				ionm.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.NOT_INFECTED);
				ionm.setInfectionTick(-1);
				ionm.setInfectionTransformation(Transformation.HUMAN);
			}
		});
	}
}
