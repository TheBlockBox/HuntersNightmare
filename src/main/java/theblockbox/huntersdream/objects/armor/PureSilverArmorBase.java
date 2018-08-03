package theblockbox.huntersdream.objects.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.interfaces.effective.IArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.ISilverEffectiveAgainstTransformation;

public class PureSilverArmorBase extends ArmorBase
		implements IArmorEffectiveAgainstTransformation, ISilverEffectiveAgainstTransformation {
	/**
	 * The damage that the werewolf should get when attacking the entity wearing the
	 * armour
	 */
	private final float EFFECTIVENESS;
	private final float PROTECTION;

	public PureSilverArmorBase(String name, ArmorMaterial materialIn, int renderIndexIn,
			EntityEquipmentSlot equipmentSlotIn, float damageAgainstWerewolf, float protection) {
		super(name, materialIn, renderIndexIn, equipmentSlotIn);
		this.EFFECTIVENESS = damageAgainstWerewolf;
		this.PROTECTION = protection;
	}

	@Override
	public float getArmorEffectiveness() {
		return this.EFFECTIVENESS;
	}

	@Override
	public float getProtection() {
		return PROTECTION;
	}

	@Override
	public Transformations[] transformations() {
		return ISilverEffectiveAgainstTransformation.super.transformations();
	}

	@Override
	public boolean effectiveAgainst(Transformations transformation) {
		return IArmorEffectiveAgainstTransformation.super.effectiveAgainst(transformation);
	}
}
