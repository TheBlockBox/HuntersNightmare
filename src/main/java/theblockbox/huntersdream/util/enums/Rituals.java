package theblockbox.huntersdream.util.enums;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;

public enum Rituals {
	LUPUS_ADVOCABIT("lupusadvocabit", Transformations.WEREWOLF);

	private ResourceLocation resourceLocation;
	private Transformations[] forTransformations;

	private Rituals(ResourceLocation resourceLocation, Transformations... forTransformations) {
		this.resourceLocation = resourceLocation;
		this.forTransformations = forTransformations;
		Helper.RITUALS.add(this);
	}

	private Rituals(String name, Transformations... forTransformations) {
		this(new ResourceLocation(Reference.MODID, name), forTransformations);
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
		for (Rituals ritual : Helper.RITUALS) {
			if (ritual.getResourceLocation().toString().equals(name)) {
				return ritual;
			}
		}
		Main.LOGGER.error("The given string \"" + name
				+ "\" does not have a corresponding ritual. Please report this, NullPointerExceptions may occure\nStacktrace: "
				+ (new ExecutionPath()).getAll());
		return null;
	}

	@Override
	public String toString() {
		return this.getResourceLocation().toString();
	}
}
