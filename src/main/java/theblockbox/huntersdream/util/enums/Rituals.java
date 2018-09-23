package theblockbox.huntersdream.util.enums;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public enum Rituals {
	LUPUS_ADVOCABIT("lupus_advocabit", TransformationInit.WEREWOLF),
	WEREWOLF_SECOND_RITE("werewolf_second_rite", TransformationInit.WEREWOLF),
	VAMPIRE_FIRST_RITUAL("vampire_first_ritual", TransformationInit.VAMPIRE),
	VAMPIRE_SECOND_RITUAL("vampire_second_ritual", TransformationInit.VAMPIRE);

	private ResourceLocation resourceLocation;
	private Transformation[] forTransformations;

	private Rituals(ResourceLocation resourceLocation, Transformation... forTransformations) {
		this.resourceLocation = resourceLocation;
		this.forTransformations = forTransformations;
		Helper.RITUALS.add(this);
	}

	private Rituals(String name, Transformation... forTransformations) {
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
	public Transformation[] getForTransformations() {
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
