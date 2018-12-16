package theblockbox.huntersdream.api;

import static theblockbox.huntersdream.api.Transformation.TransformationEntry.of;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToIntFunction;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.event.TransformationRegistryEvent;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.VampireHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.functional.ToFloatObjFloatFunction;

/**
 * A class to represent transformations and their properties. To register new
 * ones subscribe to
 * {@link theblockbox.huntersdream.api.event.TransformationRegistryEvent} and
 * add your transformation. Create a new one with
 * {@link TransformationEntry#create()}.
 */
public class Transformation {
	// Transformation constants
	/** Used to indicate that no transformation is present and it won't change */
	public static final Transformation NONE = of("none").setSupernatural(false).create();
	/**
	 * Used to indicate that no transformation is currently present but it could
	 * change
	 */
	public static final Transformation HUMAN = of("human").setSupernatural(false).create();
	/** The transformation for werewolves */
	public static final Transformation WEREWOLF = of("werewolf")
			.setCalculateDamage(WerewolfHelper::calculateUnarmedDamage)
			.setCalculateReducedDamage(WerewolfHelper::calculateReducedDamage)
			.setTexturesHD("werewolf/lycanthrope_brown", "werewolf/lycanthrope_black", "werewolf/lycanthrope_white",
					"werewolf/lycanthrope_yellow")
			.setGetTextureIndex(WerewolfHelper::getTextureIndexForWerewolf).create();
	/** The transformation for vampires */
	public static final Transformation VAMPIRE = of("vampire").setCalculateDamage(VampireHelper::calculateDamage)
			.setCalculateReducedDamage(VampireHelper::calculateReducedDamage).create();
	/** The transformation for hunters */
	public static final Transformation HUNTER = of("hunter").create();

	private static Transformation[] transformations = null;

	private final TransformationEntry entry;
	private final ResourceLocation registryName;
	/**
	 * The id of this transformation for this game session. Gets set when
	 * transformation gets registered
	 */
	private int temporaryID = -1;
	private final Optional<Transformation> optional = Optional.of(this);
	private final TextComponentTranslation translation;
	private final String registryNameString;

	/**
	 * Returns the transformation that has the same name. If no transformation has
	 * that name, {@link Transformation#NONE} will be returned
	 * 
	 * @param name The name of the resourcelocation (obtained through
	 *             {@link ResourceLocation#toString()}, something like
	 *             "huntersdream:werewolf"
	 * @see #fromNameWithoutError(String)
	 */
	public static Transformation fromName(String name) {
		Transformation transformation = fromNameWithoutError(name);
		if (transformation == NONE) {
			Main.getLogger()
					.error("The given string \"" + name
							+ "\" does not have a corresponding transformation. Please report this\nStacktrace: "
							+ ExecutionPath.getAll());
		}
		return transformation;
	}

	/** Returns the number of registered transformations */
	public static int getRegisteredTransformations() {
		// if not initialized, return 5 (default transformations)
		return (transformations != null) ? transformations.length : 5;
	}

	/**
	 * Does exactly the same as {@link #fromName(String)} but without logging an
	 * error
	 * 
	 * @see #fromName(String)
	 */
	public static Transformation fromNameWithoutError(String name) {
		for (Transformation transformation : transformations)
			if (transformation.getRegistryName().toString().equals(name))
				return transformation;
		return NONE;
	}

	/**
	 * Does exactly the same as {@link #fromName(String)} except that it accepts a
	 * {@link ResourceLocation}.
	 * 
	 * @see #fromName(String)
	 */
	public static Transformation fromResourceLocation(ResourceLocation resourceLocation) {
		return fromName(resourceLocation.toString());
	}

	/**
	 * Tries to get a transformation from its temporary id. For a version that works
	 * without temporary ids over game restarts, use {@link #fromName(String)}.
	 * 
	 * @see #fromName(String)
	 */
	public static Transformation fromTemporaryID(int temporaryID) throws ArrayIndexOutOfBoundsException {
		return transformations[temporaryID];
	}

	/**
	 * Gets the transformation with the temporary id of the given transformation + 1
	 * or 1 if a higher one doesn't exist (because 0 would be
	 * {@link theblockbox.huntersdream.api.Transformation#NONE}). Doesn't allow
	 * {@link Transformation#NONE}
	 */
	public static Transformation cycle(Transformation transformation) {
		transformation.validateIsTransformation();
		int id = transformation.getTemporaryID();
		return fromTemporaryID((getRegisteredTransformations() > id) ? id + 1 : 1);
	}

	/** Returns an array of all currently registered transformations */
	public static Transformation[] getAllTransformations() {
		return transformations.clone();
	}

	/**
	 * Called in the preInit phase to register all transformations. Should
	 * <b>not</b> be called outside of Hunter's Dream.
	 */
	public static void preInit() {
		TransformationRegistryEvent event = new TransformationRegistryEvent();
		MinecraftForge.EVENT_BUS.post(event);
		transformations = event.getTransformations();
		for (int i = 0; i < transformations.length; i++) {
			transformations[i].temporaryID = i;
		}
	}

	/**
	 * A protected constructor for everyone who needs to extend this class.
	 * 
	 * @see TransformationEntry#create()
	 */
	protected Transformation(TransformationEntry entry) {
		this.entry = entry;
		this.registryName = entry.registryName;
		this.registryNameString = this.registryName.toString();
		this.translation = new TextComponentTranslation(this.registryNameString);
	}

	/**
	 * Gets the transformation with the temporary id of this transformation + 1 or 1
	 * if this temporary id is the highest one (0 is always
	 * {@link Transformation#NONE}, so that won't be used)
	 */
	public Transformation cycle() {
		int newID = this.temporaryID + 1;
		return fromTemporaryID((getRegisteredTransformations() > newID) ? newID : 1);
	}

	/** Returns true if this transformation has been registered */
	public boolean hasBeenRegistered() {
		return this.temporaryID >= 0;
	}

	/** Returns this transformation's registry name */
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}

	/** Returns true if this transformation is supernatural */
	public boolean isSupernatural() {
		return this.entry.supernatural;
	}

	/** Returns the {@link TextComponentTranslation} for this transformation */
	public TextComponentTranslation getTranslation() {
		return this.translation;
	}

	/**
	 * This method returns true if the transformation is a transformation (meaning
	 * it's not {@link Transformation#NONE})
	 */
	public boolean isTransformation() {
		return this != NONE;
	}

	/** Throws an exception if {@link #isTransformation()} returns false */
	public void validateIsTransformation() {
		if (!this.isTransformation())
			throw new WrongTransformationException("No transformation not allowed here", this);
	}

	/**
	 * Throws an exception if this transformation does not equal the given
	 * transformation
	 */
	public void validateEquals(Transformation other) {
		if (this != other)
			throw new WrongTransformationException(
					String.format("The transformation should be \"%s\" but is \"%s\"\n", this, other), other);
	}

	/**
	 * This method returns the resource location obtained through
	 * {@link #getRegistryName()} in a string representation. So for example for
	 * werewolf with a resource location of new ResourceLocation("huntersdream",
	 * "werewolf") this method here would return "huntersdream:werewolf"
	 */
	@Override
	public String toString() {
		return this.registryNameString;
	}

	/**
	 * Returns the resource locations of the textures this transformation has (used
	 * for texture index)
	 */
	public ResourceLocation[] getTextures() {
		return this.entry.textures.clone();
	}

	/**
	 * Used to get the dealt damage from an entity and the initial damage. Currently
	 * only used for players.
	 */
	public float getDamage(EntityLivingBase entity, float initialDamage) {
		return this.entry.calculateDamage.applyAsFloat(entity, initialDamage);
	}

	/**
	 * Used to get the damage an entity gets by passing the entity and the initial
	 * damage the entity got
	 */
	public float getReducedDamage(EntityLivingBase entity, float initialDamage) {
		return this.entry.calculateReducedDamage.applyAsFloat(entity, initialDamage);
	}

	/**
	 * Returns the texture index an entity should get when it just spawned/changed
	 * transformation.
	 */
	public int getTextureIndexForEntity(EntityLivingBase entity) {
		return this.entry.getTextureIndex.applyAsInt(entity);
	}

	public Consumer<EntityLivingBase> getInfect() {
		return this.entry.infect;
	}

	/**
	 * Returns the resource location to the icon of this transformation. The icon is
	 * used in the skill survival tab.
	 */
	public ResourceLocation getIcon() {
		return this.entry.icon;
	}

	/**
	 * Returns a temporary id for the transformation. Don't use the id for writing
	 * to or reading from nbt because it may change the next time the game is
	 * started
	 */
	public int getTemporaryID() {
		return this.temporaryID;
	}

	/**
	 * Returns this transformation in form of an {@link Optional}. Always returns
	 * the same optional
	 */
	public Optional<Transformation> toOptional() {
		return this.optional;
	}

	/**
	 * {@inheritDoc}<br>
	 * <br>
	 * The transformation version always returns the temporary id
	 * ({@link #getTemporaryID()})
	 * 
	 * @see #getTemporaryID()
	 */
	@Override
	public int hashCode() {
		return this.getTemporaryID();
	}

	/** Used to register new transformations. All set methods are optional. */
	public static class TransformationEntry {
		private ResourceLocation registryName;
		private boolean supernatural = true;
		private ResourceLocation[] textures = new ResourceLocation[0];
		private Consumer<EntityLivingBase> infect = null;
		/** An {@link ObjDoubleConsumer} that is executed when the player levels up */
		private ToFloatObjFloatFunction<EntityLivingBase> calculateDamage = (e, f) -> f;
		private ToFloatObjFloatFunction<EntityLivingBase> calculateReducedDamage = (e, f) -> f;
		private ToIntFunction<EntityLivingBase> getTextureIndex = e -> 0;
		private ResourceLocation icon;

		private TransformationEntry(ResourceLocation registryName) {
			this.registryName = registryName;
			this.icon = new ResourceLocation(registryName.getNamespace(),
					"textures/gui/icon_" + registryName.getPath() + ".png");
		}

		/**
		 * Returns a new instance of type TransformationEntry from the given
		 * ResourceLocation.
		 * 
		 * @param registryName A ResourceLocation that should be used as the registry
		 *                     name for the Transformation that is going to be created.
		 */
		public static TransformationEntry of(ResourceLocation registryName) {
			return new TransformationEntry(registryName);
		}

		/**
		 * Returns a new instance of type TransformationEntry from the given
		 * ResourceLocation. Exists for Hunter's Dream only!
		 */
		static TransformationEntry of(String name) {
			return new TransformationEntry(GeneralHelper.newResLoc(name));
		}

		/** Sets this transformation to be supernatural */
		public TransformationEntry setSupernatural(boolean supernatural) {
			this.supernatural = supernatural;
			return this;
		}

		/**
		 * Sets a {@link ToFloatObjFloatFunction} to calculate the damage an entity,
		 * that has this transformation, deals
		 */
		public TransformationEntry setCalculateDamage(ToFloatObjFloatFunction<EntityLivingBase> calculateDamage) {
			this.calculateDamage = calculateDamage;
			return this;
		}

		/**
		 * Sets a {@link ToFloatObjFloatFunction} to calculate the damage an entity,
		 * that has this transformation, gets
		 */
		public TransformationEntry setCalculateReducedDamage(
				ToFloatObjFloatFunction<EntityLivingBase> calculateReducedDamage) {
			this.calculateReducedDamage = calculateReducedDamage;
			return this;
		}

		/**
		 * Sets the {@link ResourceLocation}s of the textures for this transformation.
		 */
		public TransformationEntry setTextures(ResourceLocation... textures) {
			this.textures = textures;
			return this;
		}

		private TransformationEntry setTexturesHD(String... textures) {
			ResourceLocation[] resourceLocations = new ResourceLocation[textures.length];
			for (int i = 0; i < textures.length; i++) {
				resourceLocations[i] = GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + textures[i] + ".png");
			}
			this.textures = resourceLocations;
			return this;
		}

		/**
		 * Sets the given {@link Consumer} to the function that is called when an entity
		 * is infected with this tranformation
		 * 
		 * @param infect Gets called when an entity is infected with this tranformation.
		 *               Is allowed to be null.
		 */
		public TransformationEntry setInfect(@Nullable Consumer<EntityLivingBase> infect) {
			this.infect = infect;
			return this;
		}

		/**
		 * Sets the getTextureIndex method to the given {@link ToIntFunction} that will
		 * be called to determine the texture index of an entity.
		 */
		public TransformationEntry setGetTextureIndex(ToIntFunction<EntityLivingBase> getTextureIndex) {
			this.getTextureIndex = getTextureIndex;
			return this;
		}

		/**
		 * Sets the icon that is used in {@link Transformation#getIcon()} to the given
		 * ResourceLocation. If this method is not called, the icon will be an auto
		 * generated ResourceLocation in the format
		 * {@code modid:textures/gui/icon_transformation_name.png}.
		 */
		public void setIcon(ResourceLocation icon) {
			this.icon = icon;
		}

		/** Creates a new Transformation from this TransformationEntry */
		public Transformation create() {
			return new Transformation(this);
		}
	}
}
