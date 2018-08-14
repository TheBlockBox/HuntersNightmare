package theblockbox.huntersdream.util.interfaces.effective;

import net.minecraft.client.resources.I18n;
import theblockbox.huntersdream.util.helpers.TranslationHelper;

public interface IArmorEffectiveAgainstTransformation extends IEffective {
	public static final float DEFAULT_PROTECTION = 1.5F;
	public static final float DEFAULT_EFFECTIVENESS = 2;

	/**
	 * when werewolf attacks player with armor implementing
	 * IArmorEffectiveAgainstWerewolf: damage = damage / armor.getProtection()
	 */
	default public float getProtection() {
		return DEFAULT_PROTECTION;
	}

	/** The damage multiplier when used against the specified creature */
	default public float getArmorEffectiveness() {
		return DEFAULT_EFFECTIVENESS;
	}

	default public String getTooltipArmorEffective() {
		return I18n.format("huntersdream.armorEffectiveAgainst.tooltip", TranslationHelper.getAsList(transformations()),
				TranslationHelper.translateNumber(getArmorEffectiveness()),
				TranslationHelper.translateNumber(getProtection()));
	}
}