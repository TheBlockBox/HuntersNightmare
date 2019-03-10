package theblockbox.huntersdream.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;

public class PotionAconite extends PotionBase {
	public PotionAconite() {
		super(false, 16769280, 0, "aconite");
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		// remove infection
		WerewolfHelper.cureLycanthropy(entityIn, true);
	}
}
