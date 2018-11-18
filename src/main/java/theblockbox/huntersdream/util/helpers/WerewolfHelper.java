package theblockbox.huntersdream.util.helpers;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.event.WerewolfTransformingEvent.WerewolfTransformingReason;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.TransformationEventHandler;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class WerewolfHelper {
	public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON;
	public static final float WEREWOLF_HEIGHT = 2.4F;

	/**
	 * Returns true when a werewolf can transform in this world
	 */
	public static boolean isWerewolfTime(World world) {
		if (world.isRemote)
			throw new WrongSideException("Can only test if it is werewolf time on server side", world);
		boolean isNight = !world.isDaytime();
		boolean werewolfTime = world.getCurrentMoonPhaseFactor() == 1F && isNight;
		return werewolfTime;
	}

	/**
	 * Returns how much damage the given entity does unarmed (this method can also
	 * be called when the entity is not transformed, as long as it's still a
	 * werewolf)
	 */
	public static float calculateUnarmedDamage(EntityLivingBase entity, float initialDamage) {
		WrongTransformationException.ifNotTransformationThrow(entity, Transformation.WEREWOLF);
		// if the werewolf is either not transformed or has something in its hands
		// (shouldn't happen for players)
		boolean transformed = isTransformed(entity);
		if (entity instanceof EntityPlayer) {
			if (transformed) {
				// unarmed damage is 6 and transformed werewolf players can't hold items, so
				// there is no armed damage for transformed werewolf players
				return 6F;
			} else {
				return initialDamage + 2;
			}
		} else {
			if (transformed) {
				// TODO: is it 5 full or half hearts?
				return wasLastAttackBite(entity) ? 2.5F : 2F;
			} else {
				return initialDamage;
			}
		}
	}

	/**
	 * /** Returns how much damage the given entity should get if the entity and the
	 * initial damage are the passed arguments (this method can also be called when
	 * the entity is not transformed, as long as it's still a werewolf)
	 */
	public static float calculateReducedDamage(EntityLivingBase entity, float initialDamage) {
		WrongTransformationException.ifNotTransformationThrow(entity, Transformation.WEREWOLF);
		if (isTransformed(entity)) {
			if (entity instanceof EntityPlayer) {
				// same as leather armour
				return initialDamage / 1.28F;
			} else {
				// same as diamond armor
				return initialDamage / 4.35F;
			}
		} else {
			// no protection when not transformed
			return initialDamage;
		}
	}

	/** Returns true if the werewolf can transform */
	public static boolean canWerewolfTransform(EntityLivingBase werewolf) {
		WrongTransformationException.ifNotTransformationThrow(werewolf, Transformation.WEREWOLF);
		return !werewolf.isPotionActive(PotionInit.POTION_WOLFSBANE);
	}

	/** Infects the given entity with lycantrophy */
	public static void infectEntityAsWerewolf(EntityLivingBase entityToBeInfected) {
		if (TransformationHelper.canChangeTransformation(entityToBeInfected)
				&& TransformationHelper.canBeInfectedWith(Transformation.WEREWOLF, entityToBeInfected)
				&& !entityToBeInfected.isPotionActive(PotionInit.POTION_WOLFSBANE)) {
			if (entityToBeInfected instanceof EntityPlayer) {
				entityToBeInfected.sendMessage(
						new TextComponentTranslation("transformations." + Reference.MODID + ".infected.werewolf"));
			}
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0, false, true));
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1, false, true));
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0, false, false));
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entityToBeInfected).get();
			ionm.setInfectionStatus(InfectionStatus.MOON_ON_INFECTION);
			ionm.setInfectionTick(entityToBeInfected.ticksExisted);
			ionm.setInfectionTransformation(Transformation.WEREWOLF);
		}
	}

	public static Optional<IInfectOnNextMoon> getIInfectOnNextMoon(@Nonnull EntityLivingBase entity) {
		Validate.notNull(entity);
		return Optional.ofNullable(entity.getCapability(CAPABILITY_INFECT_ON_NEXT_MOON, null));
	}

	/**
	 * Returns true if the given entity can infect other entities (it also checks if
	 * the entity is a werewolf and transformed!)
	 */
	// TODO: Unnecessary because entities can only infect with bite and they can
	// only use bite when transformed, so remove this when everything's set in stone
	public static boolean canInfect(EntityLivingBase entity) {
		// all werewolves (players included) can always infect (given they're
		// transformed)
		return isTransformed(entity);
	}

	/**
	 * Returns the chance of a werewolf infecting a mob/player
	 * 
	 * @param entity The entity whose chance to return
	 * @return Returns the chance of infection (100% = always, 0% = never)
	 */
	public static int getInfectionPercentage(EntityLivingBase entity) {
		if (canInfect(entity)) {
			return wasLastAttackBite(entity) ? 25 : 0;
		} else {
			throw new WrongTransformationException("The given entity is not a werewolf",
					TransformationHelper.getTransformation(entity));
		}
	}

	/**
	 * Returns true when the given player has mainly control in the werewolf form
	 */
	public static boolean hasMainlyControl(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getITransformationPlayer(player);
		WrongTransformationException.ifNotTransformationThrow(player, Transformation.WEREWOLF);
		return (cap.getLevelFloor() > 0);
	}

	/**
	 * Returns true when the given entity is transformed and also a werewolf
	 */
	public static boolean isTransformed(EntityLivingBase entity) {
		return (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF)
				&& TransformationHelper.getTransformationData(entity).getBoolean("transformed");
	}

	public static void setTransformed(EntityLivingBase werewolf, boolean transformed) {
		WrongTransformationException.ifNotTransformationThrow(werewolf, Transformation.WEREWOLF);
		NBTTagCompound compound = TransformationHelper.getTransformationData(werewolf);
		compound.setBoolean("transformed", transformed);
		TransformationHelper.onTransformationDataModified(werewolf, compound);
	}

	/**
	 * Tries to transform a player. Returns true if successful and false if not
	 * (also returns true if the player was already transformed). Also sends a
	 * packet if something changed.
	 */
	public static boolean transformPlayer(@Nonnull EntityPlayerMP player, boolean transformed,
			WerewolfTransformingReason reason) {
		if (isTransformed(player) != transformed) {
			if (!MinecraftForge.EVENT_BUS.post(new WerewolfTransformingEvent(player, !transformed, reason))) {
				// TODO: Send packet
				setTransformed(player, transformed);
				PacketHandler.sendTransformationMessage(player);
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Caution! Your entity has to extend EntityLiving or a subclass and needs a
	 * constructor World, int, Transformations (World = spawn world, int = texture
	 * to use, Transformations = transformation that the entity should have) When
	 * the werewolf transforms back, this constructor will be called and World will
	 * be {@link EntityWerewolf#getEntityWorld()}, int will be
	 * {@link EntityWerewolf#getTextureIndex()} and Transformations will be
	 * {@link Transformation#WEREWOLF}
	 * 
	 * @param entity The entity to be transformed
	 */
	public static EntityWerewolf toWerewolfWhenNight(EntityCreature entity) {
		World world = entity.world;
		if (!world.isRemote) {
			if (WerewolfHelper.isWerewolfTime(entity.world)) {
				if (canWerewolfTransform(entity)) {
					if (entity instanceof ITransformation) {
						ITransformation transformation = (ITransformation) entity;
						WrongTransformationException.ifNotTransformationThrow(entity, Transformation.WEREWOLF);
						EntityWerewolf werewolf = new EntityWerewolf(world, transformation.getTextureIndex(),
								entity.getClass().getName(), TransformationHelper.postExtraDataEvent(entity, true));
						return werewolf;
					} else {
						WrongTransformationException.ifNotTransformationThrow(entity, Transformation.WEREWOLF);
						EntityWerewolf werewolf = new EntityWerewolf(world, TransformationHelper
								.getITransformationCreature(entity)
								.orElseThrow(() -> new IllegalArgumentException(
										"Entity does not implement interface \"ITransformation\" or has TransformationCreature capability"))
								.getTextureIndex(), "$bycap" + entity.getClass().getName(),
								TransformationHelper.postExtraDataEvent(entity, true));
						return werewolf;
					}
				}
			}
		} else {
			throw new WrongSideException("Can't transform entity to werewolf on client side", world);
		}
		return null;
	}

	public static int getTransformationStage(EntityPlayerMP player) {
		WrongTransformationException.ifNotTransformationThrow(player, Transformation.WEREWOLF);
		return TransformationHelper.getTransformationData(player).getInteger("transformationStage");
	}

	public static void setTransformationStage(EntityPlayerMP player, int stage) {
		WrongTransformationException.ifNotTransformationThrow(player, Transformation.WEREWOLF);
		NBTTagCompound compound = TransformationHelper.getTransformationData(player);
		compound.setInteger("transformationStage", stage);
	}

	/** Returns the player.ticksExisted when the transformation has started */
	public static int getTimeSinceTransformation(EntityPlayerMP player) {
		WrongTransformationException.ifNotTransformationThrow(player, Transformation.WEREWOLF);
		return TransformationHelper.getTransformationData(player).getInteger("timeSinceTransformation");
	}

	public static void setTimeSinceTransformation(EntityPlayerMP player, int time) {
		WrongTransformationException.ifNotTransformationThrow(player, Transformation.WEREWOLF);
		NBTTagCompound compound = TransformationHelper.getTransformationData(player);
		compound.setInteger("timeSinceTransformation", time);
	}

	public static int getSoundTicks(EntityPlayerMP player) {
		WrongTransformationException.ifNotTransformationThrow(player, Transformation.WEREWOLF);
		return TransformationHelper.getTransformationData(player).getInteger("soundTicks");
	}

	public static void setSoundTicks(EntityPlayerMP player, int ticks) {
		WrongTransformationException.ifNotTransformationThrow(player, Transformation.WEREWOLF);
		NBTTagCompound compound = TransformationHelper.getTransformationData(player);
		compound.setInteger("soundTicks", ticks);
	}

	public static boolean wasLastAttackBite(EntityLivingBase entity) {
		WrongTransformationException.ifNotTransformationThrow(entity, Transformation.WEREWOLF);
		return TransformationHelper.getTransformationData(entity).getBoolean("lastAttackBite");
	}

	public static void setLastAttackBite(EntityLivingBase entity, boolean wasBite) {
		WrongTransformationException.ifNotTransformationThrow(entity, Transformation.WEREWOLF);
		NBTTagCompound compound = TransformationHelper.getTransformationData(entity);
		compound.setBoolean("lastAttackBite", wasBite);
		TransformationHelper.onTransformationDataModified(entity, compound);
	}

	/**
	 * This method is called when an entity picks up an item that is effective
	 * against the entity and then sends a specific message to everyone involved
	 */
	public static void sendItemPickupMessage(EntityPlayerMP sendTo, String reply, EntityLivingBase entity,
			Item pickedUp) {
		if ((!(reply == null)) && (!reply.equals(""))) {
			TextComponentTranslation message = null;
			TextComponentTranslation item = new TextComponentTranslation(pickedUp.getTranslationKey() + ".name");

			if (reply.contains("fp")) {
				if (reply.contains("picked")) {
					message = new TextComponentTranslation(reply, item);
				} else if (reply.contains("touched")) {
					message = new TextComponentTranslation(reply, item);
				}
			} else if (reply.contains("tp")) {
				if (reply.contains("picked")) {
					message = new TextComponentTranslation(reply, entity.getDisplayName().getFormattedText(), item);
				} else if (reply.contains("touched")) {
					message = new TextComponentTranslation(reply, entity.getDisplayName().getFormattedText(), item);
				}
			}
			sendTo.sendMessage(message);
		}
	}

	/**
	 * Called in
	 * {@link TransformationEventHandler#onEntityTick(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent)}
	 */
	public static void onWerewolfTick(EntityCreature werewolf) {
		if (!isTransformed(werewolf)) {
			transformWerewolfWhenPossible(werewolf, WerewolfTransformingReason.FULL_MOON);
		}
	}

	public static void transformWerewolfWhenPossible(@Nonnull EntityCreature werewolf,
			WerewolfTransformingReason reason) {
		Validate.notNull(werewolf, "The argument werewolf is not allowed to be null");
		WrongTransformationException.ifNotTransformationThrow(werewolf, Transformation.WEREWOLF);
		if (isTransformed(werewolf)) {
			throw new IllegalArgumentException(
					"The given entity " + werewolf.toString() + " is not allowed to be transformed");
		}
		if (!MinecraftForge.EVENT_BUS.post(new WerewolfTransformingEvent(werewolf, false, reason))) {
			EntityLivingBase returned = toWerewolfWhenNight(werewolf);
			if (returned != null) {
				World world = returned.world;
				returned.setPosition(werewolf.posX, werewolf.posY, werewolf.posZ);
				returned.setHealth(returned.getMaxHealth() / (werewolf.getMaxHealth() / werewolf.getHealth()));
				world.removeEntity(werewolf);
				world.spawnEntity(returned);
				returned.setPositionAndUpdate(werewolf.posX, werewolf.posY, werewolf.posZ);
			}
		}
	}

	public static float getWerewolfHeight(Entity werewolf) {
		// TODO: Change eye height if it's possible to change it when sneaking
		return shouldUseSneakingModel(werewolf) ? 2F : WEREWOLF_HEIGHT;
	}

	public static float getWerewolfEyeHeight(Entity werewolf) {
		return shouldUseSneakingModel(werewolf) ? 1.85F : 2.1F;
	}

	public static boolean shouldUseSneakingModel(Entity werewolf) {
		// if player was sneaking before, don't use sneaking model
		return werewolf.isSneaking() || !GeneralHelper.canEntityExpandHeight(werewolf, WEREWOLF_HEIGHT);
	}
}
