package theblockbox.huntersdream.init;

import theblockbox.huntersdream.event.TransformationRegistryEvent;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.Transformation.TransformationEntry;
import theblockbox.huntersdream.util.helpers.VampireHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public class TransformationInit {
	// TODO: Add levelling system for WITCH, CLOCKWORKANDROID, HYBRID and
	// HUNTER
	public static final Transformation HUMAN = newEntry().setSupernatural(false).create("human");
	public static final Transformation WEREWOLF = newEntry().setCalculateDamage(WerewolfHelper::calculateUnarmedDamage)
			.setCalculateProtection(WerewolfHelper::calculateProtection)
			.setCalculateLevel(WerewolfHelper::getWerewolfLevel)
			.setTransformCreature(WerewolfHelper::toWerewolfWhenNight)
			.setTexturesHD("werewolf_beta_black", "werewolf_beta_brown", "werewolf_beta_white").create("werewolf");
	public static final Transformation VAMPIRE = newEntry().setCalculateDamage(VampireHelper::calculateDamage)
			.setCalculateProtection(VampireHelper::calculateProtection).setCalculateLevel(VampireHelper::calculateLevel)
			.create("vampire");
	public static final Transformation WITCH = newEntry().create("witch");
	public static final Transformation CLOCKWORKANDROID = newEntry().create("clockworkandroid");
	public static final Transformation HYBRID = newEntry().create("hybrid");
	public static final Transformation HUNTER = newEntry().create("hunter");

	private static TransformationEntry newEntry() {
		return TransformationEntry.of();
	}

	public static void register(TransformationRegistryEvent event) {
		event.registerTransformations(HUMAN, WEREWOLF, VAMPIRE, WITCH, CLOCKWORKANDROID, HYBRID, HUNTER);
	}
}
