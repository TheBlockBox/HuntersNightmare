package theblockbox.huntersdream.util.enums;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.event.TransformingEvent;
import theblockbox.huntersdream.event.TransformingEvent.TransformingEventReason;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationEntityTransformed;
import theblockbox.huntersdream.util.interfaces.transformation.IUntransformedCreatureExtraData;

public enum Transformations {

	// TODO: Add levelling system for VAMPIRE, WITCH, CLOCKWORKANDROID, HYBRID and
	// HUNTER
	HUMAN(TransformationEntry.create("human").setSupernatural(false)),
	WEREWOLF(TransformationEntry.create("werewolf").setCalculateDamage(WerewolfHelper::calculateUnarmedDamage)
			.setCalculateProtection(WerewolfHelper::calculateProtection)
			.setCalculateLevel(WerewolfHelper::getWerewolfLevel)
			.setTransformCreature(WerewolfHelper::toWerewolfWhenNight)
			.setTexturesHD("werewolf_beta_black", "werewolf_beta_brown", "werewolf_beta_white")),
	VAMPIRE(TransformationEntry.create("vampire")), WITCH(TransformationEntry.create("witch")),
	CLOCKWORKANDROID(TransformationEntry.create("clockworkandroid")), HYBRID(TransformationEntry.create("hybrid")),
	HUNTER(TransformationEntry.create("hunter").setSupernatural(false));

	// when an entity has no transformation, use null
	// (no transformation meaning not infectable)

	public static class Helper {
		public static final ArrayList<Transformations> TRANSFORMATIONS = new ArrayList<>();
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
		Transformations transformation = fromNameWithoutError(name);
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
	public static @Nullable Transformations fromNameWithoutError(String name) {
		for (Transformations transformations : Helper.TRANSFORMATIONS)
			if (transformations.getResourceLocation().toString().equals(name))
				return transformations;
		return null;
	}

	public static Transformations fromResourceLocation(ResourceLocation resourceLocation) {
		return fromName(resourceLocation.toString());
	}

	/** Returns an array of all currently registered transformations */
	public static Transformations[] getAllTransformations() {
		return Helper.TRANSFORMATIONS.toArray(new Transformations[0]);
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
		return (id == 0) ? (Transformations.HUMAN) : ((id == 1) ? Transformations.WEREWOLF : null);
	}

	public ResourceLocation getResourceLocation() {
		return this.ENTRY.resourceLocation;
	}

	public double getLevel(EntityPlayerMP player) {
		return this.getCalculateLevel().applyAsDouble(player);
	}

	public boolean isSupernatural() {
		return this.ENTRY.supernatural;
	}

	/** Returns the xp bar texture for the given transformation */
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

	public ToDoubleFunction<EntityPlayerMP> getCalculateLevel() {
		return this.ENTRY.calculateLevel;
	}

	public ResourceLocation[] getTextures() {
		return this.ENTRY.textures;
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

	public void transformCreatureWhenPossible(@Nonnull EntityLiving creature) {
		if (this.ENTRY.transformCreature != null) {
			if (creature == null)
				throw new NullPointerException("The given parameter creature is null");
			else if (creature instanceof ITransformationEntityTransformed)
				throw new IllegalArgumentException("The given entity " + creature.toString()
						+ " is an instance of ITransformationEntityTransformed and can therefore not be transformed");
			else {
				EntityLivingBase returned = this.ENTRY.transformCreature.apply(creature);
				if (!MinecraftForge.EVENT_BUS
						.post(new TransformingEvent(creature, false, TransformingEventReason.ENVIROMENT))) {
					if (returned != null) {
						World world = returned.world;
						returned.setPosition(creature.posX, creature.posY, creature.posZ);
						returned.setHealth(returned.getMaxHealth() / (creature.getMaxHealth() / creature.getHealth()));
						world.removeEntity(creature);

						// apply extra data
						if (creature instanceof EntityCreature) {
							// the returned creature always has to be an instanve of
							// ITransformationEntityTransformed
							IUntransformedCreatureExtraData<EntityCreature> uced = IUntransformedCreatureExtraData
									.getFromEntity((EntityCreature) creature);
							if (uced != null) {
								((ITransformationEntityTransformed) returned)
										.setExtraData(uced.getExtraData((EntityCreature) creature));
							}
						}
						world.spawnEntity(returned);
						returned.setPositionAndUpdate(creature.posX, creature.posY, creature.posZ);
					}
				}
			}
		}
	}

	/** Used to make new transformations */
	public static class TransformationEntry {
		private boolean supernatural = true;
		private ResourceLocation[] textures = new ResourceLocation[0];
		private ToDoubleFunction<EntityPlayerMP> calculateLevel = player -> TransformationHelper.getCap(player).getXP()
				/ 500.0D;
		private Function<EntityLiving, EntityLivingBase> transformCreature = null;
		private Consumer<EntityLivingBase> infect = null;
		private ResourceLocation resourceLocation = null;
		/** A runnable that is executed when the player levels up */
		private ObjDoubleConsumer<EntityPlayerMP> onLevelUp = (d, p) -> {
		};
		private Function<EntityLivingBase, Float> calculateDamage = e -> 1F;
		private Function<EntityLivingBase, Float> calculateProtection = e -> 1F;

		private TransformationEntry(ResourceLocation resourceLocation) {
			this.resourceLocation = resourceLocation;
			if (resourceLocation.getResourceDomain().equals("") || resourceLocation.getResourcePath().equals("")
					|| resourceLocation == null
					|| resourceLocation.getResourceDomain() == null | resourceLocation.getResourcePath() == null)
				throw new NullPointerException(
						"A value or the resource location itself is either null or an empty string");
		}

		/** Returns a new instance of type TransformationEntry */
		public static TransformationEntry create(ResourceLocation resourceLocation) {
			return new TransformationEntry(resourceLocation);
		}

		/**
		 * Caution! Domain defaults to huntersdream. If you're making an addon, use
		 * {@link #create(ResourceLocation)}
		 */
		public static TransformationEntry create(String name) {
			return new TransformationEntry(GeneralHelper.newResLoc(name));
		}

		public TransformationEntry setSupernatural(boolean supernatural) {
			this.supernatural = supernatural;
			return this;
		}

		public TransformationEntry setTransformCreature(Function<EntityLiving, EntityLivingBase> transformCreature) {
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

		public Transformations createTransformation(String enumName) {
			return EnumHelper.addEnum(Transformations.class, enumName, new Class<?>[] { TransformationEntry.class },
					this);
		}
	}
}
