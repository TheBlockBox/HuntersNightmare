package theblockbox.huntersdream.util.enums;

import java.util.ArrayList;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.functional.ICalculateLevel;
import theblockbox.huntersdream.util.interfaces.functional.IInfect;
import theblockbox.huntersdream.util.interfaces.functional.ITransformCreature;

public enum Transformations {

	// TODO: Add levelling system for VAMPIRE, WITCH, CLOCKWORKANDROID and HUNTER
	HUMAN(TransformationEntry.create("human").setSupernatural(false)),
	WEREWOLF(TransformationEntry.create("werewolf").setGeneralDamage(12).setProtection(20F)
			.setCalculateLevel(WerewolfHelper::getWerewolfLevel)
			.setTransformCreature(WerewolfHelper::toWerewolfWhenNight)
			.setTexturesHD("werewolf_beta_black", "werewolf_beta_brown", "werewolf_beta_white")),
	VAMPIRE(TransformationEntry.create("vampire")), WITCH(TransformationEntry.create("witch")),
	CLOCKWORKANDROID(TransformationEntry.create("clockworkandroid")), HYBRID(TransformationEntry.create("hybrid")),
	HUNTER(TransformationEntry.create("hunter").setSupernatural(false));

	// when an entity has no transformation, use null
	// (no transformation meaning not infectable)

	private static class Helper {
		private static final ArrayList<Transformations> TRANSFORMATIONS = new ArrayList<>();
	}

	private final TransformationEntry ENTRY;

	private Transformations(TransformationEntry entry) {
		this.ENTRY = entry;
		Helper.TRANSFORMATIONS.add(this);
	}

	/**
	 * Returns the transformation that has the same name. If no transformation has
	 * that name, null will be returned
	 * 
	 * @param name The name of the resourcelocation (obtained through
	 *             {@link ResourceLocation#toString()}, something like
	 *             "huntersdream:werewolf"
	 */
	public static Transformations fromName(String name) {
		for (Transformations transformations : Helper.TRANSFORMATIONS) {
			if (transformations.getResourceLocation().toString().equals(name)) {
				return transformations;
			}
		}
		Main.LOGGER.error("The given string \"" + name
				+ "\" does not have a corresponding transformation. Please report this, NullPointerExceptions may occure\nStacktrace: "
				+ (new ExecutionPath()).getAll());
		return null;
	}

	public static Transformations fromResourceLocation(ResourceLocation resourceLocation) {
		return fromName(resourceLocation.toString());
	}

	/**
	 * @deprecated Use {@link #fromName(String)} or
	 *             {@link #fromResourceLocation(ResourceLocation)} instead. This is
	 *             only here because of pre-0.2.0 support
	 * @param id The id of the transformation that you want to get
	 * @return Returns the transformation that has the corresponding id
	 */
	@Deprecated
	public static Transformations fromID(int id) {
		if (id == 0) {
			return Transformations.HUMAN;
		} else if (id == 1) {
			return Transformations.WEREWOLF;
		} else {
			throw new UnexpectedBehaviorException("The id " + id + " couldn't be found");
		}
	}

	public ResourceLocation getResourceLocation() {
		return this.ENTRY.resourceLocation;
	}

	public double getLevel(EntityPlayerMP player) {
		if (!player.world.isRemote) {
			return this.getCalculateLevel().getLevel(player);
		} else {
			throw new WrongSideException("Can only obtain level on server side. Please use cap#getLevel instead",
					player.world);
		}
	}

	public boolean isSupernatural() {
		return this.ENTRY.supernatural;
	}

	public ResourceLocation getXPBarTexture() {
		return new ResourceLocation(getResourceLocation().getResourceDomain(),
				"textures/gui/transformation_xp_bar_" + getResourceLocation().getResourcePath() + ".png");
	}

	/**
	 * This method returns the resource location obtained through
	 * {@link #getResourceLocation()} in a string representation. So for example for
	 * werewolf with a resource location of new ResourceLocation("huntersdream",
	 * "werewolf") this method here would return "huntersdream:werewolf"
	 */
	@Override
	public String toString() {
		return this.getResourceLocation().toString();
	}

	public void transformCreatureWhenPossible(EntityCreature creature) {
		if (this.getTransformCreature() != null) {
			this.getTransformCreature().transformCreature(creature);
		}
	}

	private ITransformCreature getTransformCreature() {
		return this.ENTRY.transformCreature;
	}

	public ICalculateLevel getCalculateLevel() {
		return this.ENTRY.calculateLevel;
	}

	public int getGeneralDamage() {
		return this.ENTRY.generalDamage;
	}

	public ResourceLocation[] getTextures() {
		return this.ENTRY.textures;
	}

	public float getProtection() {
		return this.ENTRY.protection;
	}

	public int getRandomTextureIndex() {
		return ChanceHelper.randomInt(this.getTextures().length);
	}

	public IInfect getInfect() {
		return this.ENTRY.infect;
	}

	/** Used to make new transformations */
	public static class TransformationEntry {
		private boolean supernatural = true;
		/**
		 * the damage that the entites should deal in half hearts or for player's the
		 * damage multiplier
		 */
		private int generalDamage = 1;
		private ResourceLocation[] textures = new ResourceLocation[0];
		private ICalculateLevel calculateLevel = player -> {
			return TransformationHelper.getCap(player).getXP() / 500;
		};
		private float protection = 1F;
		private ITransformCreature transformCreature;
		private IInfect infect;
		private ResourceLocation resourceLocation;

		public TransformationEntry(ResourceLocation resourceLocation) {
			this.resourceLocation = resourceLocation;
			if (resourceLocation.getResourceDomain().equals("") || resourceLocation.getResourcePath().equals("")
					|| resourceLocation == null
					|| resourceLocation.getResourceDomain() == null | resourceLocation.getResourcePath() == null)
				throw new NullPointerException(
						"A value or the resource location itself is either null or an empty string");
		}

		/** returns a new instance of type TransformationEntry */
		public static TransformationEntry create(ResourceLocation resourceLocation) {
			return new TransformationEntry(resourceLocation);
		}

		/**
		 * Caution! Domain defaults to huntersdream. If you're making an addon, use
		 * {@link #create(ResourceLocation)}
		 */
		public static TransformationEntry create(String resourceLocation) {
			return new TransformationEntry(new ResourceLocation(Reference.MODID, resourceLocation));
		}

		public TransformationEntry setSupernatural(boolean supernatural) {
			this.supernatural = supernatural;
			return this;
		}

		public TransformationEntry setGeneralDamage(int damage) {
			this.generalDamage = damage;
			return this;
		}

		public TransformationEntry setCalculateLevel(ICalculateLevel calculateLevel) {
			this.calculateLevel = calculateLevel;
			return this;
		}

		public TransformationEntry setProtection(float protection) {
			this.protection = protection;
			return this;
		}

		public TransformationEntry setTransformCreature(ITransformCreature transformCreature) {
			this.transformCreature = transformCreature;
			return this;
		}

		public TransformationEntry setTextures(ResourceLocation... textures) {
			this.textures = textures;
			return this;
		}

		/**
		 * Don't use this method when creating an addon (use
		 * {@link #setTextures(ResourceLocation...)}
		 */
		public TransformationEntry setTexturesHD(String... textures) {
			ResourceLocation[] resourceLocations = new ResourceLocation[textures.length];
			for (int i = 0; i < textures.length; i++) {
				resourceLocations[i] = new ResourceLocation(Reference.ENTITY_TEXTURE_PATH + textures[i] + ".png");
			}
			this.textures = resourceLocations;
			return this;
		}

		public TransformationEntry setInfect(IInfect infect) {
			this.infect = infect;
			return this;
		}

		public Transformations getTransformations(String name) {
			return EnumHelper.addEnum(Transformations.class, name, new Class<?>[] { TransformationEntry.class }, this);
		}
	}
}
