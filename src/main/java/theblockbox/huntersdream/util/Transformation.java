package theblockbox.huntersdream.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.event.TransformationRegistryEvent;
import theblockbox.huntersdream.event.TransformingEvent;
import theblockbox.huntersdream.event.TransformingEvent.TransformingEventReason;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.functional.ToFloatFunction;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationEntityTransformed;

/**
 * A class to represent transformations and their properties. To register new
 * ones subscribe to
 * {@link theblockbox.huntersdream.event.TransformationRegistryEvent} and add
 * your transformation. Create a new one with
 * {@link TransformationEntry#create(ResourceLocation)}
 */
public class Transformation {
	private static Transformation[] transformations = null;

	private final TransformationEntry ENTRY;
	private final ResourceLocation registryName;
	/**
	 * The id of this transformation for this game session. Gets set when
	 * transformation gets registered
	 */
	private int temporaryID = -1;
	private final Optional<Transformation> optional = Optional.of(this);
	private final TextComponentTranslation translation;
	private final String registryNameString;

	protected Transformation(TransformationEntry entry, ResourceLocation registryName) {
		this.ENTRY = entry;
		this.registryName = registryName;
		this.registryNameString = registryName.toString();
		this.translation = new TextComponentTranslation(this.registryNameString);
	}

	/**
	 * Returns the transformation that has the same name. If no transformation has
	 * that name, {@link TransformationInit#NONE} will be returned
	 * 
	 * @param name The name of the resourcelocation (obtained through
	 *             {@link ResourceLocation#toString()}, something like
	 *             "huntersdream:werewolf"
	 */
	public static Transformation fromName(String name) {
		Transformation transformation = fromNameWithoutError(name);
		if (transformation == TransformationInit.NONE) {
			Main.getLogger()
					.error("The given string \"" + name
							+ "\" does not have a corresponding transformation. Please report this\nStacktrace: "
							+ (new ExecutionPath()).getAll());
			return TransformationInit.NONE;
		}
		return transformation;
	}

	/**
	 * Does exactly the same thing as {@link #fromName(String)} but without logging
	 * an error
	 */
	public static Transformation fromNameWithoutError(String name) {
		for (Transformation transformation : transformations)
			if (transformation.getRegistryName().toString().equals(name))
				return transformation;
		return TransformationInit.NONE;
	}

	public static Transformation fromResourceLocation(ResourceLocation resourceLocation) {
		return fromName(resourceLocation.toString());
	}

	public static Transformation fromTemporaryID(int temporaryID) throws ArrayIndexOutOfBoundsException {
		return transformations[temporaryID];
	}

	/**
	 * Gets the transformation with the temporary id of the given transformation + 1
	 * or 1 if a higher one doesn't exist (because 0 would be
	 * {@link theblockbox.huntersdream.init.TransformationInit#NONE}). Doesn't allow
	 * {@link TransformationInit#NONE}
	 */
	public static Transformation cycle(Transformation transformation) {
		transformation.validateIsTransformation();
		int id = transformation.getTemporaryID();
		return fromTemporaryID((getTransformationLength() > id) ? id + 1 : 1);
	}

	/** Returns an array of all currently registered transformations */
	public static Transformation[] getAllTransformations() {
		return transformations.clone();
	}

	public static void preInit() {
		TransformationRegistryEvent event = new TransformationRegistryEvent();
		MinecraftForge.EVENT_BUS.post(event);
		transformations = event.getTransformations();
		for (int i = 0; i < transformations.length; i++) {
			transformations[i].temporaryID = i;
		}
	}

	/**
	 * Gets the transformation with the temporary id of this transformation + 1 or 1
	 * if this temporary id is the highest one (0 is always
	 * {@link TransformationInit#NONE}, so that won't be used)
	 */
	public Transformation cycle() {
		int newID = this.temporaryID + 1;
		return fromTemporaryID((getTransformationLength() > newID) ? newID : 1);
	}

	public boolean hasBeenRegistered() {
		return this.temporaryID >= 0;
	}

	public ResourceLocation getRegistryName() {
		return this.registryName;
	}

	public static int getTransformationLength() {
		// if not initialized, return 7 (default transformations)
		return (transformations != null) ? transformations.length : 7;
	}

	public double getLevel(EntityPlayerMP player) {
		return this.getCalculateLevel().applyAsDouble(player);
	}

	public boolean isSupernatural() {
		return this.ENTRY.supernatural;
	}

	/** Returns the xp bar texture for the given transformation */
	public ResourceLocation getXPBarTexture() {
		return new ResourceLocation(getRegistryName().getNamespace(),
				"textures/gui/transformation_xp_bar_" + getRegistryName().getPath() + ".png");
	}

	public TextComponentTranslation getTranslation() {
		return this.translation;
	}

	/**
	 * This method returns true if the transformation is a transformation (meaning
	 * it's not {@link TransformationInit#NONE})
	 */
	public boolean isTransformation() {
		return true;
	}

	/** Throws an exception if {@link #isTransformation()} returns false */
	public void validateIsTransformation() {
		if (!this.isTransformation())
			throw new WrongTransformationException("No transformation not allowed here", this);
	}

	/**
	 * This method returns the resource location obtained through
	 * {@link #getResourceLocation()} in a string representation. So for example for
	 * werewolf with a resource location of new ResourceLocation("huntersdream",
	 * "werewolf") this method here would return "huntersdream:werewolf"
	 */
	@Override
	public String toString() {
		return this.registryNameString;
	}

	public ToDoubleFunction<EntityPlayerMP> getCalculateLevel() {
		return this.ENTRY.calculateLevel;
	}

	public ResourceLocation[] getTextures() {
		return this.ENTRY.textures.clone();
	}

	public float getGeneralDamage(EntityLivingBase entity) {
		return this.ENTRY.calculateDamage.applyAsFloat(entity);
	}

	public float getProtection(EntityLivingBase entity) {
		return this.ENTRY.calculateProtection.applyAsFloat(entity);
	}

	/**
	 * Returns a random texture index within the bounds of the given
	 * transformation's textures
	 */
	public int getRandomTextureIndex() {
		return ChanceHelper.randomInt(this.getTextures().length);
	}

	public Consumer<EntityLivingBase> getInfect() {
		return this.ENTRY.infect;
	}

	public void onLevelUp(EntityPlayerMP player, double newLevel) {
		this.ENTRY.onLevelUp.accept(player, newLevel);
	}

	public void transformCreatureWhenPossible(@Nonnull EntityCreature creature) {
		if (this.ENTRY.transformCreature != null) {
			if (creature == null)
				throw new NullPointerException("The given \"parameter\" creature is null");
			else if (creature instanceof ITransformationEntityTransformed)
				throw new IllegalArgumentException("The given entity " + creature.toString()
						+ " is an instance of ITransformationEntityTransformed and can therefore not be transformed");
			else {
				if (!MinecraftForge.EVENT_BUS
						.post(new TransformingEvent(creature, false, TransformingEventReason.ENVIROMENT))) {
					EntityLivingBase returned = this.ENTRY.transformCreature.apply(creature);
					if (returned != null) {
						World world = returned.world;
						returned.setPosition(creature.posX, creature.posY, creature.posZ);
						returned.setHealth(returned.getMaxHealth() / (creature.getMaxHealth() / creature.getHealth()));
						world.removeEntity(creature);
						world.spawnEntity(returned);
						returned.setPositionAndUpdate(creature.posX, creature.posY, creature.posZ);
					}
				}
			}
		}
	}

	/**
	 * Returns a temporary id for the transformation. Don't use the id for writing
	 * to or reading from nbt because it may change the next time the game is
	 * started
	 */
	public int getTemporaryID() {
		return this.temporaryID;
	}

	public Optional<Transformation> toOptional() {
		return this.optional;
	}

	@Override
	public int hashCode() {
		return this.getTemporaryID();
	}

	/** Used to make register new transformations */
	public static class TransformationEntry {
		private boolean supernatural = true;
		private ResourceLocation[] textures = new ResourceLocation[0];
		private ToDoubleFunction<EntityPlayerMP> calculateLevel = player -> TransformationHelper.getCap(player).getXP()
				/ 500.0D;
		private Function<EntityCreature, EntityLivingBase> transformCreature = null;
		private Consumer<EntityLivingBase> infect = null;
		/** A runnable that is executed when the player levels up */
		private ObjDoubleConsumer<EntityPlayerMP> onLevelUp = (d, p) -> {
		};
		private ToFloatFunction<EntityLivingBase> calculateDamage = e -> 1F;
		private ToFloatFunction<EntityLivingBase> calculateProtection = e -> 1F;

		private TransformationEntry() {
		}

		/** Returns a new instance of type TransformationEntry */
		public static TransformationEntry of() {
			return new TransformationEntry();
		}

		public TransformationEntry setSupernatural(boolean supernatural) {
			this.supernatural = supernatural;
			return this;
		}

		public TransformationEntry setTransformCreature(Function<EntityCreature, EntityLivingBase> transformCreature) {
			this.transformCreature = transformCreature;
			return this;
		}

		public TransformationEntry setCalculateDamage(ToFloatFunction<EntityLivingBase> calculateDamage) {
			this.calculateDamage = calculateDamage;
			return this;
		}

		public TransformationEntry setCalculateProtection(ToFloatFunction<EntityLivingBase> calculateProtection) {
			this.calculateProtection = calculateProtection;
			return this;
		}

		public TransformationEntry setCalculateLevel(ToDoubleFunction<EntityPlayerMP> calculateLevel) {
			this.calculateLevel = calculateLevel;
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
				resourceLocations[i] = GeneralHelper.newResLoc(Reference.ENTITY_TEXTURE_PATH + textures[i] + ".png");
			}
			this.textures = resourceLocations;
			return this;
		}

		public TransformationEntry setInfect(Consumer<EntityLivingBase> infect) {
			this.infect = infect;
			return this;
		}

		public TransformationEntry setOnLevelUp(ObjDoubleConsumer<EntityPlayerMP> onLevelUp) {
			this.onLevelUp = onLevelUp;
			return this;
		}

		/** Creates a new Transformation with the given registry name */
		public Transformation create(ResourceLocation registryName) {
			return new Transformation(this, registryName);
		}

		/**
		 * Does exactly the same as {@link #create(ResourceLocation)} except that, if a
		 * string with ':' is given, the domain defaults to hunter's dream
		 */
		public Transformation create(String registryName) {
			return new Transformation(this, GeneralHelper.newResLoc(registryName));
		}
	}
}
