package theblockbox.huntersdream.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;

public class PotionWolfsbane extends PotionBase {
    public PotionWolfsbane() {
        super(false, 16769280, 0, "wolfsbane");
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        // remove infection
        WerewolfHelper.cureLycanthropy(entityIn, true);
    }
}
