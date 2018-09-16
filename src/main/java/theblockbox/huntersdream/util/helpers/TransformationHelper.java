package theblockbox.huntersdream.util.helpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.model.ModelLycanthropeBiped;
import theblockbox.huntersdream.entity.model.ModelLycanthropeQuadruped;
import theblockbox.huntersdream.event.ExtraDataEvent;
import theblockbox.huntersdream.event.TransformationEvent;
import theblockbox.huntersdream.event.TransformationEvent.TransformationEventReason;
import theblockbox.huntersdream.event.TransformationXPEvent;
import theblockbox.huntersdream.event.TransformationXPEvent.TransformationXPSentReason;
import theblockbox.huntersdream.event.TransformingEvent;
import theblockbox.huntersdream.event.TransformingEvent.TransformingEventReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
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
	 * special damage source for things that are effective against specific
	 * transformations
	 */
	public static final DamageSource EFFECTIVE_AGAINST_TRANSFORMATION = new DamageSource(
			"effectiveAgainstTransformation");

	/**
	 * Returns the transformation capability of the given player (just a short-cut
	 * method)
	 */
	public static ITransformationPlayer getCap(EntityPlayer player) {
		return player.getCapability(CAPABILITY_TRANSFORMATION_PLAYER, null);
	}

	/**
	 * Changes the player's transformation also resets xp and transformed and sends
	 * the data to the client (this method is only to be called server side!)
	 */
	private static void changeTransformation(EntityPlayerMP player, @Nonnull Transformations transformation) {
		if (transformation == null)
			throw new NullPointerException("Null not allowed here");
		ITransformationPlayer cap = getCap(player);
		cap.setXP(0); // reset xp
		cap.setRituals(new Rituals[0]); // reset rituals
		cap.setLevel(0);
		cap.setTransformed(false); // reset transformed
		cap.setTransformation(transformation);
		cap.setTextureIndex(cap.getTransformation().getRandomTextureIndex());
		if (ConfigHandler.common.showPacketMessages)
			Main.getLogger()
					.info("Transformation of player " + player.getName() + " changed to " + transformation.toString());
		PacketHandler.sendTransformationMessage(player); // sync data with client
	}

	public static void changeTransformation(@Nonnull EntityLivingBase entity, @Nonnull Transformations transformation,
			TransformationEventReason reason) {
		if (entity != null && transformation != null) {
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

	public static void changeTransformationWhenPossible(@Nonnull EntityCreature entity,
			@Nonnull Transformations transformation, TransformationEventReason reason) {
		if (transformation == null || entity == null) {
			throw new NullPointerException("A null argument was passed. Entity null: " + (entity == null)
					+ " Transformation null: " + (transformation == null));
		}
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
	@Nullable
	public static Transformations getTransformation(EntityLivingBase entity) {
		ITransformation transformation = getITransformation(entity);
		return (transformation == null) ? null : transformation.getTransformation();
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
	public static ITransformationCreature getITransformationCreature(EntityCreature entity) {
		if (entity != null) {
			return (entity instanceof ITransformationCreature) ? (ITransformationCreature) entity
					: entity.getCapability(CAPABILITY_TRANSFORMATION_CREATURE, null);
		} else {
			return null;
		}
	}

	/**
	 * Shortcut for {@code addXP(player, 1, reason)}
	 */
	public static void incrementXP(EntityPlayerMP player, TransformationXPSentReason reason) {
		addXP(player, 1, reason);
	}

	/**
	 * Shortcut for {@code
	 * ITransformationPlayer cap = getCap(player);
	 * setXP(player, (cap.getXP() + xpToAdd), reason);}
	 */
	public static void addXP(EntityPlayerMP player, int xpToAdd, TransformationXPSentReason reason) {
		ITransformationPlayer cap = getCap(player);
		setXP(player, (cap.getXP() + xpToAdd), reason);
	}

	/**
	 * Sets the players xp to the given xp, sends a message on levelup and an xp
	 * packet
	 */
	public static void setXP(EntityPlayerMP player, int xp, TransformationXPSentReason reason) {
		ITransformationPlayer cap = getCap(player);
		int levelBefore = cap.getLevelFloor();
		TransformationXPEvent event = new TransformationXPEvent(player, xp, reason);

		if (!MinecraftForge.EVENT_BUS.post(event)) {
			cap.setXP(event.getAmount());
			cap.setLevel(cap.getTransformation().getLevel((EntityPlayerMP) player));
			int levelAfter = cap.getLevelFloor();
			if (levelBefore < levelAfter) {
				player.sendMessage(new TextComponentTranslation("transformations.huntersdream.onLevelUp", levelAfter));
				cap.getTransformation().onLevelUp(player, levelAfter);
			}
			PacketHandler.sendTransformationXPMessage(player);
		}
	}

	/**
	 * Returns true when the given entity can be infected with the given
	 * infection/transformation
	 */
	public static boolean canBeInfectedWith(Transformations infection, EntityLivingBase entity) {
		if (canChangeTransformation(entity)) {
			IInfectInTicks iit = getIInfectInTicks(entity);
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entity);
			if (ionm != null) {
				if (infection == Transformations.WEREWOLF) {
					return true;
				}
			}
			if (iit != null) {
				if (entity instanceof EntityCreature) {
					ITransformationCreature tc = getITransformationCreature((EntityCreature) entity);
					if (tc != null) {
						return tc.notImmuneToTransformation(infection);
					}
				}
				return true;
			}

		}
		return false;
	}

	/**
	 * Returns true when the given entity can be infected with the given
	 * infection/transformation without accounting for current infections
	 */
	public static boolean onInfectionCanBeInfectedWith(Transformations infection, EntityCreature entity) {
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
	public static IInfectInTicks getIInfectInTicks(EntityLivingBase entity) {
		return entity.getCapability(CAPABILITY_INFECT_IN_TICKS, null);
	}

	/**
	 * Infects the given entity in the given amount of ticks with the given
	 * infection/transformation
	 */
	public static void infectIn(int ticksUntilInfection, EntityLivingBase entityToBeInfected,
			Transformations infectTo) {
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
		boolean flag = false;

		if (iit != null) {
			flag = iit.currentlyInfected();
		}

		if (ionm != null) {
			if (!flag) {
				flag = !(ionm.getInfectionStatus() == InfectionStatus.NOT_INFECTED);
			}
		}
		return flag;
	}

	public static void refreshLevel(EntityPlayerMP player) {
		ITransformationPlayer cap = getCap(player);
		cap.setLevel(cap.getTransformation().getLevel(player));
	}

	/**
	 * Sets the player size when the player is for example a transformed, sprinting
	 * werewolf
	 */
	public static void configurePlayerSize(EntityPlayer player) {
		if (WerewolfHelper.transformedWerewolf(player)) {
			boolean quadruped = (player.isSprinting() || player.isSneaking());
			float height = quadruped ? ModelLycanthropeQuadruped.HEIGHT : ModelLycanthropeBiped.HEIGHT;
			float width = quadruped ? ModelLycanthropeQuadruped.WIDTH : GeneralHelper.STANDARD_PLAYER_WIDTH;
			float eyeheight = quadruped ? ModelLycanthropeQuadruped.EYE_HEIGHT : ModelLycanthropeBiped.EYE_HEIGHT;

			if (!quadruped && !GeneralHelper.canEntityExpandHeight(player, height)) {
				quadruped = true;
				height = ModelLycanthropeQuadruped.HEIGHT;
				width = ModelLycanthropeQuadruped.WIDTH;
				eyeheight = ModelLycanthropeQuadruped.EYE_HEIGHT;
			}

			GeneralHelper.changePlayerSize(player, width, height);
			player.eyeHeight = eyeheight;
		} else {
			player.eyeHeight = player.getDefaultEyeHeight();
		}
	}

	public static void transform(@Nonnull EntityLivingBase entity, boolean transformed,
			TransformingEventReason reason) {
		if (getITransformation(entity).transformed() != transformed) {
			if (!MinecraftForge.EVENT_BUS.post(new TransformingEvent(entity, !transformed, reason))) {
				getITransformation(entity).setTransformed(transformed);
			}
		}
	}

	public static NBTTagCompound postExtraDataEvent(EntityCreature creature, boolean onDataSave) {
		ExtraDataEvent event = new ExtraDataEvent(creature, GeneralHelper.writeEntityToNBT(creature), onDataSave);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getExtraData();
	}
}
