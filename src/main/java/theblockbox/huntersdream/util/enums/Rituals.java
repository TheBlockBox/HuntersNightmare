package theblockbox.huntersdream.util.enums;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public enum Rituals {
	LUPUS_ADVOCABIT("lupus_advocabit", Transformations.WEREWOLF),
	WEREWOLF_SECOND_RITE("werewolf_second_rite", Transformations.WEREWOLF),
	VAMPIRE_FIRST_RITUAL("vampire_first_ritual", Transformations.VAMPIRE),
	VAMPIRE_SECOND_RITUAL("vampire_second_ritual", Transformations.VAMPIRE);

	private ResourceLocation resourceLocation;
	private Transformations[] forTransformations;

	private Rituals(ResourceLocation resourceLocation, Transformations... forTransformations) {
		this.resourceLocation = resourceLocation;
		this.forTransformations = forTransformations;
		Helper.RITUALS.add(this);
	}

	private Rituals(String name, Transformations... forTransformations) {
		this(GeneralHelper.newResLoc(name), forTransformations);
	}

	private static class Helper {
		private static final ArrayList<Rituals> RITUALS = new ArrayList<>();
	}

	public ResourceLocation getResourceLocation() {
		return this.resourceLocation;
	}

	/**
	 * Returns for what transformations the ritual is for (others can't get the
	 * ritual)
	 */
	public Transformations[] getForTransformations() {
		return this.forTransformations;
	}

	public static Rituals fromName(String name) {
		for (Rituals ritual : Helper.RITUALS)
			if (ritual.toString().equals(name))
				return ritual;
		Main.getLogger().error("The given string \"" + name
				+ "\" does not have a corresponding ritual. Please report this, NullPointerExceptions may occure\nStacktrace: "
				+ (new ExecutionPath()).getAll());
		return null;
	}

	public static Rituals fromNameWithException(String name) {
		for (Rituals ritual : Helper.RITUALS)
			if (ritual.toString().equals(name))
				return ritual;
		throw new IllegalArgumentException("The given string \"" + name + "\" does not have a corresponding ritual");
	}

	@Override
	public String toString() {
		return this.getResourceLocation().toString();
	}
}
