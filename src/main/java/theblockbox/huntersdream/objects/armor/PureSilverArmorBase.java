package theblockbox.huntersdream.objects.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import theblockbox.huntersdream.util.interfaces.IArmorEffectiveAgainstWerewolf;

public class PureSilverArmorBase extends ArmorBase implements IArmorEffectiveAgainstWerewolf {
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
	public float getEffectiveness() {
		return this.EFFECTIVENESS;
	}

	@Override
	public float getProtection() {
		return PROTECTION;
	}
}
