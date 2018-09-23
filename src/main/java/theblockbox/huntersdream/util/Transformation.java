package theblockbox.huntersdream.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.event.TransformationRegistryEvent;
import theblockbox.huntersdream.event.TransformingEvent;
import theblockbox.huntersdream.event.TransformingEvent.TransformingEventReason;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationEntityTransformed;

public class Transformation {
	private static Transformation[] transformations = null;

	private final TransformationEntry ENTRY;
	private final ResourceLocation registryName;
	/**
	 * The id of this transformation for this game session. Gets set when
	 * transformation gets registered
	 */
	private int temporaryID = -1;

	private Transformation(TransformationEntry entry, ResourceLocation registryName) {
		this.ENTRY = entry;
		this.registryName = registryName;
	}

	/**
	 * Returns the transformation that has the same name. If no transformation has
	 * that name, null will be returned
	 * 
	 * @param name The name of the resourcelocation (obtained through
	 *             {@link ResourceLocation#toString()}, something like
	 *             "huntersdream:werewolf"
	 */
	public static Transformation fromName(String name) {
		Transformation transformation = fromNameWithoutError(name);
		if (transformation == null)
			Main.getLogger().error("The given string \"" + name
					+ "\" does not have a corresponding transformation. Please report this, NullPointerExceptions may occure\nStacktrace: "
					+ (new ExecutionPath()).getAll());
		return transformation;
	}

	/**
	 * Does exactly the same thing as {@link #fromName(String)} but without logging
	 * an error
	 */
	public static @Nullable Transformation fromNameWithoutError(String name) {
		for (Transformation transformation : transformations)
			if (transformation.getRegistryName().toString().equals(name))
				return transformation;
		return null;
	}

	public static @Nullable Transformation fromResourceLocation(ResourceLocation resourceLocation) {
		return fromName(resourceLocation.toString());
	}

	public static Transformation fromTemporaryID(int temporaryID) throws ArrayIndexOutOfBoundsException {
		return transformations[temporaryID];
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
		return new ResourceLocation(getRegistryName().getResourceDomain(),
				"textures/gui/transformation_xp_bar_" + getRegistryName().getResourcePath() + ".png");
	}

	/**
	 * This method returns the resource location obtained through
	 * {@link #getResourceLocation()} in a string representation. So for example for
	 * werewolf with a resource location of new ResourceLocation("huntersdream",
	 * "werewolf") this method here would return "huntersdream:werewolf"
	 */
	@Override
	public String toString() {
		return this.getRegistryName().toString();
	}

	public ToDoubleFunction<EntityPlayerMP> getCalculateLevel() {
		return this.ENTRY.calculateLevel;
	}

	public ResourceLocation[] getTextures() {
		return this.ENTRY.textures.clone();
	}

	public float getGeneralDamage(EntityLivingBase entity) {
		return this.ENTRY.calculateDamage.apply(entity);
	}

	public float getProtection(EntityLivingBase entity) {
		return this.ENTRY.calculateProtection.apply(entity);
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
		private Function<EntityLivingBase, Float> calculateDamage = e -> 1F;
		private Function<EntityLivingBase, Float> calculateProtection = e -> 1F;

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

		public TransformationEntry setCalculateDamage(Function<EntityLivingBase, Float> calculateDamage) {
			this.calculateDamage = calculateDamage;
			return this;
		}

		public TransformationEntry setCalculateProtection(Function<EntityLivingBase, Float> calculateProtection) {
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
