package theblockbox.huntersdream.util.effectiveagainsttransformation;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import theblockbox.huntersdream.util.enums.Transformations;

public class ArmorEffectiveAgainstTransformation implements IEffective {
	public static final ArrayList<ArmorEffectiveAgainstTransformation> ARMOR_PARTS = new ArrayList<>();
	private Item armorPart;
	private Transformations[] effectiveAgainst;
	private float thorns;
	private float protection;
	public static final float DEFAULT_PROTECTION = 1.5F;
	public static final float DEFAULT_EFFECTIVENESS = 2;

	/**
	 * Registers an armor part as effective against specific transformations
	 * 
	 * @param armorPart        The armor that should be effective
	 * @param thorns           The multiplier of the damage sent back (for example
	 *                         if a werewolf hits you and deals 6 hearts damage but
	 *                         your thorns multiplier is 0.5, the werewolf will get
	 *                         3 hearts damage.) (Also remember that some
	 *                         transformations like the werewolf have extra
	 *                         protection, so a werewolf would only receive the
	 *                         thorns damage by its protection)
	 * @param protection       The protection you should get on attack (for example
	 *                         if a werewolf attacks you and deals 6 hearts damage
	 *                         and your protection is 2, you will receive 3 hearts
	 *                         damage)
	 * @param effectiveAgainst The transformations against which the armor should be
	 *                         effective
	 */
	public ArmorEffectiveAgainstTransformation(Item armorPart, float thorns, float protection,
			@Nonnull Transformations... effectiveAgainst) {
		this.armorPart = armorPart;
		this.effectiveAgainst = effectiveAgainst;
		this.thorns = thorns;
		this.protection = protection;

		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS)
			if (aeat.getArmor() == armorPart)
				throw new IllegalArgumentException("Object already registered");
		ARMOR_PARTS.add(this);
	}

	public float getArmorEffectiveness() {
		return this.thorns;
	}

	public float getProtection() {
		return this.protection;
	}

	/**
	 * Returns the armor part that is effective against the transformations returned
	 * in {@link #transformations()}
	 */
	public Item getArmor() {
		return this.armorPart;
	}

	@Override
	public Transformations[] transformations() {
		return this.effectiveAgainst;
	}

	public static ArmorEffectiveAgainstTransformation getFromArmor(Item armor) {
		for (ArmorEffectiveAgainstTransformation aeat : ARMOR_PARTS)
			if (aeat.getArmor().equals(armor))
				return aeat;

		return null;
	}
}
