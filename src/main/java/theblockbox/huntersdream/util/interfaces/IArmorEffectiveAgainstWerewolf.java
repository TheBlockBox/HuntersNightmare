package theblockbox.huntersdream.util.interfaces;

public interface IArmorEffectiveAgainstWerewolf extends IEffectiveAgainstWerewolf {
	public static final float DEFAULT_PROTECTION = 1.5F;

	/**
	 * when werewolf attacks player with armor implementing
	 * IArmorEffectiveAgainstWerewolf: damage = damage / armor.getProtection()
	 */
	default public float getProtection() {
		return DEFAULT_PROTECTION;
	}
}
