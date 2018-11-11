package theblockbox.huntersdream.util.helpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.event.CanLivingBeInfectedEvent;
import theblockbox.huntersdream.event.ExtraDataEvent;
import theblockbox.huntersdream.event.IsLivingInfectedEvent;
import theblockbox.huntersdream.event.TransformationEvent;
import theblockbox.huntersdream.event.TransformationEvent.TransformationEventReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.enums.Rituals;
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
	public static ITransformationPlayer getCap(@Nonnull EntityPlayer player) {
		Validate.notNull(player);
		return player.getCapability(CAPABILITY_TRANSFORMATION_PLAYER, null);
	}

	/**
	 * Changes the player's transformation also resets xp and transformed and sends
	 * the data to the client (this method is only to be called server side!)
	 */
	private static void changeTransformation(EntityPlayerMP player, Transformation transformation) {
		transformation.validateIsTransformation();
		ITransformationPlayer cap = getCap(player);
		cap.setRituals(new Rituals[0]); // reset rituals
		cap.setTransformation(transformation);
		cap.setTextureIndex(cap.getTransformation().getRandomTextureIndex());
		if (ConfigHandler.common.showPacketMessages)
			Main.getLogger()
					.info("Transformation of player " + player.getName() + " changed to " + transformation.toString());
		PacketHandler.sendTransformationMessage(player); // sync data with client
	}

	public static void changeTransformation(@Nonnull EntityLivingBase entity, @Nonnull Transformation transformation,
			TransformationEventReason reason) {
		if (entity != null && transformation.isTransformation()) {
			if (!entity.world.isRemote) {
				if (!MinecraftForge.EVENT_BUS.post(new TransformationEvent(entity, transformation, reason))) {
					if (entity instanceof EntityPlayer) {
						changeTransformation((EntityPlayerMP) entity, transformation);
					} else if (entity instanceof EntityCreature) {
						getITransformation(entity).setTransformation(transformation);
						ITransformationCreature tc = getITransformationCreature((EntityCreature) entity);
						if (tc != null) {
							tc.setTextureIndex(tc.getTransformation().getRandomTextureIndex());
						}
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

		ITransformationCreature tc = getITransformationCreature(entity);
		boolean flag = canChangeTransformation(entity)
				&& (tc != null ? tc.notImmuneToTransformation(transformation) : true);
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
		ITransformation transformation = getITransformation(entity);
		return (transformation != null) && (transformation.isTransformationChangeable());
	}

	/** Returns the entity's transformation */
	@Nonnull
	public static Transformation getTransformation(EntityLivingBase entity) {
		ITransformation transformation = getITransformation(entity);
		return (transformation == null) ? TransformationInit.NONE : transformation.getTransformation();
	}

	/**
	 * This is a way to get the {@link ITransformation} interface from every entity
	 * that has it in some form (capability or implemented)
	 */
	@Nullable
	public static ITransformation getITransformation(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return getCap((EntityPlayer) entity);
		} else if (entity instanceof ITransformation) {
			return (ITransformation) entity;
		} else if (entity instanceof EntityCreature) {
			return getITransformationCreature((EntityCreature) entity);
		} else {
			return null;
		}
	}

	@Nullable
	public static ITransformationCreature getITransformationCreature(@Nonnull EntityCreature entity) {
		Validate.notNull(entity);
		return (entity instanceof ITransformationCreature) ? (ITransformationCreature) entity
				: entity.getCapability(CAPABILITY_TRANSFORMATION_CREATURE, null);
	}

	/**
	 * Returns true when the given entity can be infected with the given
	 * infection/transformation
	 */
	public static boolean canBeInfectedWith(Transformation infection, EntityLivingBase entity) {
		Validate.notNull(infection, "The transformation isn't allowed to be null");
		if (canChangeTransformation(entity)) {
			IInfectInTicks iit = getIInfectInTicks(entity);
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
			if (ionm != null && infection == TransformationInit.WEREWOLF) {
				return true;
			}
			if (iit != null) {
				if (entity instanceof EntityCreature) {
					ITransformationCreature tc = getITransformationCreature((EntityCreature) entity);
					if (tc != null) {
						return tc.notImmuneToTransformation(infection)
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
			ITransformationCreature tc = getITransformationCreature(entity);
			if (tc != null) {
				return tc.notImmuneToTransformation(infection);
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/** Returns the {@link IInfectInTicks} capability of the given entity */
	@Nullable
	public static IInfectInTicks getIInfectInTicks(@Nonnull EntityLivingBase entity) {
		Validate.notNull(entity);
		return entity.getCapability(CAPABILITY_INFECT_IN_TICKS, null);
	}

	/**
	 * Infects the given entity in the given amount of ticks with the given
	 * infection/transformation
	 */
	public static void infectIn(int ticksUntilInfection, EntityLivingBase entityToBeInfected, Transformation infectTo) {
		Validate.notNull(infectTo, "The transformation isn't allowed to be null");
		IInfectInTicks iit = getIInfectInTicks(entityToBeInfected);
		if (iit != null) {
			iit.setTime(ticksUntilInfection);
			iit.setCurrentlyInfected(true);
			iit.setInfectionTransformation(infectTo);
		} else {
			throw new IllegalArgumentException(
					"The given entity does not have the capability IInfectInTicks/infectinticks");
		}
	}

	/** Returns true if the given entity is infected */
	public static boolean isInfected(EntityLivingBase entity) {
		IInfectInTicks iit = getIInfectInTicks(entity);
		IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);

		if ((iit != null) && iit.currentlyInfected()) {
			return true;
		} else if (ionm != null && (ionm.getInfectionStatus() != InfectionStatus.NOT_INFECTED)) {
			return true;
		}

		return MinecraftForge.EVENT_BUS.post(new IsLivingInfectedEvent(entity));
	}

	public static void changePlayerSize(EntityPlayer player) {
		if (WerewolfHelper.isTransformedWerewolf(player)) {
			// we have to use 0.6 because otherwise the players move without doing anything
			GeneralHelper.changePlayerSize(player, 0.6F, WerewolfHelper.getWerewolfHeight(player));
			player.eyeHeight = WerewolfHelper.getWerewolfEyeHeight(player);
		} else {
			player.eyeHeight = player.getDefaultEyeHeight();
		}
	}

	public static NBTTagCompound postExtraDataEvent(EntityCreature creature, boolean onDataSave) {
		ExtraDataEvent event = new ExtraDataEvent(creature, GeneralHelper.writeEntityToNBT(creature), onDataSave);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getExtraData();
	}
}
