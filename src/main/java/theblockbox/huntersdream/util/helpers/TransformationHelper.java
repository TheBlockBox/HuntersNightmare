package theblockbox.huntersdream.util.helpers;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.CanLivingBeInfectedEvent;
import theblockbox.huntersdream.api.event.IsLivingInfectedEvent;
import theblockbox.huntersdream.api.event.TransformationEvent;
import theblockbox.huntersdream.api.event.TransformationEvent.TransformationEventReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.interfaces.IInfectInTicks;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class TransformationHelper {
	public static final Capability<ITransformationPlayer> CAPABILITY_TRANSFORMATION_PLAYER = CapabilitiesInit.CAPABILITY_TRANSFORMATION_PLAYER;
	public static final Capability<ITransformationCreature> CAPABILITY_TRANSFORMATION_CREATURE = CapabilitiesInit.CAPABILITY_TRANSFORMATION_CREATURE;
	public static final Capability<IInfectInTicks> CAPABILITY_INFECT_IN_TICKS = CapabilitiesInit.CAPABILITY_INFECT_IN_TICKS;
	public static final Skill[] EMPTY_SKILL_ARRAY = new Skill[0];
	public static final String THORNS_DAMAGE_NAME = "huntersdream:effectiveAgainstTransformationThorns";
	/**
	 * Special damage source for things that are effective against specific
	 * transformations. If used, the attacked entity's protection won't work
	 */
	public static final DamageSource EFFECTIVE_AGAINST_TRANSFORMATION = new DamageSource(
			"effectiveAgainstTransformation");

	/**
	 * Returns the transformation capability of the given player (just a short-cut
	 * method)
	 */
	public static ITransformationPlayer getITransformationPlayer(@Nonnull EntityPlayer player) {
		Validate.notNull(player);
		return player.getCapability(CAPABILITY_TRANSFORMATION_PLAYER, null);
	}

	/**
	 * Changes the player's transformation also resets xp and transformed and sends
	 * the data to the client (this method is only to be called server side!)
	 */
	private static void changeTransformation(EntityPlayerMP player, Transformation transformation) {
		transformation.validateIsTransformation();
		ITransformationPlayer cap = getITransformationPlayer(player);
		cap.setSkills(EMPTY_SKILL_ARRAY); // reset rituals
		cap.setTransformation(transformation);
		cap.setTextureIndex(player);

		NBTTagCompound transformationData = new NBTTagCompound();
		transformationData.setString("transformation", transformation.toString());
		cap.setTransformationData(transformationData);

		if (ConfigHandler.common.showPacketMessages)
			Main.getLogger()
					.info("Transformation of player " + player.getName() + " changed to " + transformation.toString());
		PacketHandler.sendTransformationMessage(player); // sync data with client
	}

	public static void changeTransformation(@Nonnull EntityLivingBase entity, @Nonnull Transformation transformation,
			TransformationEventReason reason) {
		if (entity != null && transformation.isTransformation()) {
			World world = entity.world;
			if (!world.isRemote) {
				if (!MinecraftForge.EVENT_BUS.post(new TransformationEvent(entity, transformation, reason))) {
					if (entity instanceof EntityPlayer) {
						changeTransformation((EntityPlayerMP) entity, transformation);
					} else if (entity instanceof EntityCreature) {
						// TODO: Does this here make sense?
						ITransformation it = getITransformation(entity).get();
						it.setTransformation(transformation);

						NBTTagCompound transformationData = new NBTTagCompound();
						transformationData.setString("transformation", transformation.toString());
						it.setTransformationData(transformationData);

						getITransformationCreature((EntityCreature) entity).ifPresent(t -> t.setTextureIndex(entity));
					} else {
						throw new IllegalArgumentException("Can't transform entity " + entity.toString()
								+ " (it is neither a player nor an instance of EntityCreature)");
					}
				}
			} else {
				throw new WrongSideException("Can only change transformation on server side", entity.world);
			}
		} else {
			throw new NullPointerException("A null argument was passed. Entity null: " + (entity == null)
					+ " Transformation null: " + (transformation == null));
		}
	}

	public static void changeTransformationWhenPossible(EntityCreature entity, Transformation transformation,
			TransformationEventReason reason) {
		Validate.notNull(entity, "The entity isn't allowed to be null");
		Validate.notNull(transformation, "The transformation isn't allowed to be null");

		Optional<ITransformationCreature> tc = getITransformationCreature(entity);
		boolean flag = canChangeTransformation(entity)
				&& (tc.isPresent() ? tc.get().notImmuneToTransformation(transformation) : true);
		if (flag) {
			changeTransformation(entity, transformation, reason);
		}
	}

	/**
	 * Returns true when the given entity can change transformation without rituals
	 * (e.g. by werewolf infection)
	 */
	public static boolean canChangeTransformation(EntityLivingBase entity) {
		return canChangeTransformationOnInfection(entity) && !isInfected(entity);
	}

	/**
	 * Returns true if the given entity can change it's transformation while not
	 * accounting for infection
	 */
	public static boolean canChangeTransformationOnInfection(EntityLivingBase entity) {
		Optional<ITransformation> transformation = getITransformation(entity);
		return transformation.isPresent() && transformation.get().isTransformationChangeable();
	}

	/**
	 * Returns the entity's transformation. If the entity has none or the entity is
	 * null, {@link Transformation#NONE} is returned
	 */
	@Nonnull
	public static Transformation getTransformation(EntityLivingBase entity) {
		return getITransformation(entity).map(ITransformation::getTransformation).orElse(Transformation.NONE);
	}

	/**
	 * This is a way to get the {@link ITransformation} interface from every entity
	 * that has it in some form (capability or implemented)
	 */
	public static Optional<ITransformation> getITransformation(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return Optional.ofNullable(getITransformationPlayer((EntityPlayer) entity));
		} else if (entity instanceof ITransformation) {
			return Optional.ofNullable((ITransformation) entity);
		} else if (entity instanceof EntityCreature) {
			return getITransformationCreature((EntityCreature) entity).map(t -> (ITransformation) t);
		} else {
			return Optional.empty();
		}
	}

	public static Optional<ITransformationCreature> getITransformationCreature(@Nonnull EntityCreature entity) {
		Validate.notNull(entity);
		return Optional.ofNullable((entity instanceof ITransformationCreature) ? (ITransformationCreature) entity
				: entity.getCapability(CAPABILITY_TRANSFORMATION_CREATURE, null));
	}

	/**
	 * Returns true when the given entity can be infected with the given
	 * infection/transformation
	 */
	public static boolean canBeInfectedWith(Transformation infection, EntityLivingBase entity) {
		Validate.notNull(infection, "The transformation isn't allowed to be null");
		if (canChangeTransformation(entity)) {
			Optional<IInfectInTicks> iit = getIInfectInTicks(entity);
			Optional<IInfectOnNextMoon> ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
			// can't infect entity that already has the transformation
			if (TransformationHelper.getTransformation(entity) == infection) {
				return false;
			} else if (ionm.isPresent() && infection == Transformation.WEREWOLF) {
				return true;
			} else if (iit.isPresent()) {
				if (entity instanceof EntityCreature) {
					Optional<ITransformationCreature> otc = getITransformationCreature((EntityCreature) entity);
					if (otc.isPresent()) {
						return otc.get().notImmuneToTransformation(infection)
								|| MinecraftForge.EVENT_BUS.post(new CanLivingBeInfectedEvent(entity, infection));
					}
				}
				return true;
			}
		}
		return MinecraftForge.EVENT_BUS.post(new CanLivingBeInfectedEvent(entity, infection));
	}

	/**
	 * Returns true when the given entity can be infected with the given
	 * infection/transformation without accounting for current infections
	 */
	public static boolean onInfectionCanBeInfectedWith(Transformation infection, EntityCreature entity) {
		Validate.notNull(infection, "The transformation isn't allowed to be null");
		if (canChangeTransformationOnInfection(entity)) {
			Optional<ITransformationCreature> tc = getITransformationCreature(entity);
			return tc.isPresent() ? tc.get().notImmuneToTransformation(infection) : true;
		} else {
			return false;
		}
	}

	/** Returns the {@link IInfectInTicks} capability of the given entity */
	public static Optional<IInfectInTicks> getIInfectInTicks(@Nonnull EntityLivingBase entity) {
		Validate.notNull(entity);
		return Optional.ofNullable(entity.getCapability(CAPABILITY_INFECT_IN_TICKS, null));
	}

	/**
	 * Infects the given entity in the given amount of ticks with the given
	 * infection/transformation
	 */
	public static void infectIn(int ticksUntilInfection, EntityLivingBase entityToBeInfected, Transformation infectTo) {
		Validate.notNull(infectTo, "The transformation isn't allowed to be null");
		IInfectInTicks iit = getIInfectInTicks(entityToBeInfected).orElseThrow(() -> new IllegalArgumentException(
				"The given entity does not have the capability IInfectInTicks/infectinticks"));
		iit.setTime(ticksUntilInfection);
		iit.setCurrentlyInfected(true);
		iit.setInfectionTransformation(infectTo);
	}

	/** Returns true if the given entity is infected */
	public static boolean isInfected(EntityLivingBase entity) {
		Optional<IInfectInTicks> iit = getIInfectInTicks(entity);
		Optional<IInfectOnNextMoon> ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
		if (((iit.isPresent()) && iit.get().currentlyInfected())
				|| (ionm.isPresent() && (ionm.get().getInfectionStatus() != InfectionStatus.NOT_INFECTED))) {
			return true;
		}
		return MinecraftForge.EVENT_BUS.post(new IsLivingInfectedEvent(entity));
	}

	public static void changePlayerSize(EntityPlayer player) {
		if (WerewolfHelper.isTransformed(player)) {
			// we have to use 0.6 because otherwise the players move without doing anything
			GeneralHelper.changePlayerSize(player, 0.6F, WerewolfHelper.getWerewolfHeight(player));
			// TODO: Find a way to make eye height work correctly? (doesn't work correctly
			// if player is sneaking)
			player.eyeHeight = WerewolfHelper.getWerewolfEyeHeight(player);
		} else {
			player.eyeHeight = player.getDefaultEyeHeight();
		}
	}

	public static NBTTagCompound getTransformationData(EntityLivingBase entity) {
		Transformation transformation = getTransformation(entity);
		transformation.validateIsTransformation();
		ITransformation iTransformation = getITransformation(entity).get();
		NBTTagCompound compound = iTransformation.getTransformationData();

		String transformationValue = compound.getString("transformation");
		String strTransformation = transformation.toString();

		// support for old versions
		if (transformationValue.isEmpty()) {
			Main.getLogger().warn(
					"It seems like Hunter's Dream has been updated... (If this was not the case, please report this!)\n"
							+ "Setting transformation data for entity \"" + entity + "\" to a valid one");
			compound = new NBTTagCompound();
			compound.setString("transformation", strTransformation);
			compound.setBoolean("transformed", true);

			iTransformation.setTransformationData(compound);
			if (entity instanceof EntityPlayerMP) {
				PacketHandler.sendTransformationMessage((EntityPlayerMP) entity);
			}
			return compound;
		}

		Validate.isTrue(transformationValue.equals(strTransformation),
				"The NBTTagCompound should have the key \"transformation\" with the value \"%s\" but it was \"%s\"",
				strTransformation, transformationValue);
		return compound;
	}

	public static DamageSource causeEffectivenessThornsDamage(Entity source) {
		return new EntityDamageSource(THORNS_DAMAGE_NAME, source).setIsThornsDamage().setMagicDamage();
	}
}
