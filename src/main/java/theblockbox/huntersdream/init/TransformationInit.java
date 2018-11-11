package theblockbox.huntersdream.init;

import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.Transformation.TransformationEntry;
import theblockbox.huntersdream.util.helpers.VampireHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public class TransformationInit {
	// TODO: Add levelling system for WITCH, CLOCKWORKANDROID, HYBRID and
	// HUNTER
	/** Used to indicate that no transformation is present and it won't change */
	public static final Transformation NONE = newEntry().setSupernatural(false).create("none");
	/**
	 * Used to indicate that no transformation is currently present but it is
	 * possible that it will change
	 */
	public static final Transformation HUMAN = newEntry().setSupernatural(false).create("human");

	public static final Transformation WEREWOLF = newEntry().setCalculateDamage(WerewolfHelper::calculateUnarmedDamage)
			.setCalculateReducedDamage(WerewolfHelper::calculateReducedDamage).setTexturesHD("lycantrophe")
			.create("werewolf");
	public static final Transformation VAMPIRE = newEntry().setCalculateDamage(VampireHelper::calculateDamage)
			.setCalculateReducedDamage(VampireHelper::calculateReducedDamage).create("vampire");
	public static final Transformation WITCH = newEntry().create("witch");
	public static final Transformation CLOCKWORKANDROID = newEntry().create("clockworkandroid");
	public static final Transformation HYBRID = newEntry().create("hybrid");
	public static final Transformation HUNTER = newEntry().create("hunter");

	private static TransformationEntry newEntry() {
		return TransformationEntry.of();
	}
}
