package theblockbox.huntersdream.potions;

import net.minecraft.entity.EntityLivingBase;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;

public class PotionAconite extends PotionBase {
	public PotionAconite() {
		super(false, 14811307, 0, "aconite");
	}

	@Override
	public void performEffect(EntityLivingBase entityIn, int amplifier) {
		// remove infection
		WerewolfHelper.getIInfectOnNextMoon(entityIn).ifPresent(ionm -> {
			if (entityIn.isPotionActive(PotionInit.POTION_ACONITE) && ionm.isInfected()) {
				ionm.setInfectionStatus(IInfectOnNextMoon.InfectionStatus.NOT_INFECTED);
				ionm.setInfectionTick(-1);
				ionm.setInfectionTransformation(Transformation.HUMAN);
			}
		});
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 60 == 0;
	}
}
