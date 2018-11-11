package theblockbox.huntersdream.util.helpers;

import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
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
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.EventHandler;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.handlers.TransformationEventHandler;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf;

public class WerewolfHelper {
	public static final Capability<IInfectOnNextMoon> CAPABILITY_INFECT_ON_NEXT_MOON = CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON;
	public static final Capability<IWerewolf> CAPABILITY_WEREWOLF = CapabilitiesInit.CAPABILITY_WEREWOLF;

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
		WrongTransformationException.ifNotTransformationThrow(entity, TransformationInit.WEREWOLF);
		// if the werewolf is either not transformed or has something in its hands
		// (shouldn't happen for players)
//		if (isTransformed(entity) || !entity.getHeldItemMainhand().isEmpty()) {
//			if (entity instanceof EntityPlayer) {
//				return initialDamage + 2F;
//			} else {
//				// standard
//				return 9F * initialDamage;
//			}
//		} else {
//			// no extra damage when not transformed
//			return initialDamage;
//		}
		boolean transformed = isTransformed(entity);
		if (entity instanceof EntityPlayer) {
			if (transformed) {
				if (entity.getHeldItemMainhand().isEmpty()) {
					// unarmed damage does the equivalent as an iron sword
					return 5F;
				} else {
					return initialDamage;
				}
			} else {
				return initialDamage + 2;
			}
		} else {
			return transformed ? initialDamage * EntityWerewolf.ATTACK_DAMAGE : initialDamage;
		}
	}

	/**
	 * /** Returns how much damage the given entity should get if the entity and the
	 * initial damage are the passed arguments (this method can also be called when
	 * the entity is not transformed, as long as it's still a werewolf)
	 */
	public static float calculateReducedDamage(EntityLivingBase entity, float initialDamage) {
		WrongTransformationException.ifNotTransformationThrow(entity, TransformationInit.WEREWOLF);
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

	/** Applies potion effects to transformed werewolf players */
	public static void applyLevelBuffs(EntityPlayer player) {
		// if (hasControl(player)) { // TODO: Uncomment this when no control is done
		if (isTransformedWerewolf(player)) {
			int level = TransformationHelper.getCap(player).getLevelFloor();
			int duration = 120;

			// Caution! Duration in ticks
			if (level >= 1) {
				player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 400, 0, false, false));
				if (level >= 3) {
					int speedAndJumpLevel = (int) (IntStream.of(3, 7, 9, 11, 12).filter(i -> level >= i).count() - 1);
					player.addPotionEffect(
							new PotionEffect(MobEffects.SPEED, duration, speedAndJumpLevel, false, false));
					player.addPotionEffect(
							new PotionEffect(MobEffects.JUMP_BOOST, duration, speedAndJumpLevel, false, false));
				}
			}

			player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration, 2, false, false));
		} else {
			throw new WrongTransformationException("The given player isn't a werewolf and/or transformed",
					TransformationHelper.getTransformation(player));
		}
		// }
	}

	/** Returns true if the werewolf can transform */
	public static boolean canWerewolfTransform(EntityLivingBase werewolf) {
		WrongTransformationException.ifNotTransformationThrow(werewolf, TransformationInit.WEREWOLF);
		return !werewolf.isPotionActive(PotionInit.POTION_WOLFSBANE);
	}

	/** Infects the given entity with lycantrophy */
	public static void infectEntityAsWerewolf(EntityLivingBase entityToBeInfected) {
		if (TransformationHelper.canChangeTransformation(entityToBeInfected)
				&& TransformationHelper.canBeInfectedWith(TransformationInit.WEREWOLF, entityToBeInfected)
				&& !entityToBeInfected.isPotionActive(PotionInit.POTION_WOLFSBANE)) {
			if (entityToBeInfected instanceof EntityPlayer) {
				entityToBeInfected.sendMessage(
						new TextComponentTranslation("transformations." + Reference.MODID + ".infected.werewolf"));
			}
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0, false, true));
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1, false, true));
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0, false, false));
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entityToBeInfected);
			ionm.setInfectionStatus(InfectionStatus.MOON_ON_INFECTION);
			ionm.setInfectionTick(entityToBeInfected.ticksExisted);
			ionm.setInfectionTransformation(TransformationInit.WEREWOLF);
		}
	}

	public static IInfectOnNextMoon getIInfectOnNextMoon(@Nonnull EntityLivingBase entity) {
		Validate.notNull(entity);
		return entity.getCapability(CAPABILITY_INFECT_ON_NEXT_MOON, null);
	}

	/**
	 * Returns true if the given entity can infect other entities (it also checks if
	 * the entity is a werewolf and transformed!)
	 */
	public static boolean canInfect(EntityLivingBase entity) {
		// all werewolves (players included) can always infect (given they're
		// transformed)
		return isTransformedWerewolf(entity);
	}

	/**
	 * Returns the chance of a werewolf infecting a mob/player
	 * 
	 * @param entity The entity whose chance to return
	 * @return Returns the chance of infection (100% = always, 0% = never)
	 */
	public static int getInfectionPercentage(EntityLivingBase entity) {
		if (canInfect(entity)) {
			if (entity instanceof EntityPlayer) {
				int percentage = TransformationHelper.getCap((EntityPlayer) entity).getLevelFloor() * 5;
				return (percentage > 100) ? 100 : percentage;
			} else {
				return 25;
			}
		} else {
			throw new WrongTransformationException("The given entity is not a werewolf",
					TransformationHelper.getTransformation(entity));
		}
	}

	/**
	 * Returns true when the given player has mainly control in the werewolf form
	 */
	public static boolean hasMainlyControl(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		WrongTransformationException.ifNotTransformationThrow(player, TransformationInit.WEREWOLF);
		return (cap.getLevelFloor() > 0);
	}

	/**
	 * Returns true when the given entity is a werewolf and also transformed
	 */
	public static boolean isTransformedWerewolf(EntityLivingBase entity) {
		ITransformation transformation = TransformationHelper.getITransformation(entity);
		return isTransformed(entity) && (transformation != null)
				&& (transformation.getTransformation() == TransformationInit.WEREWOLF);
	}

	/**
	 * Does the same as {@link #isTransformedWerewolf(EntityLivingBase)} except that
	 * it doesn't test if the entity is actually a werewolf. Should only be used if
	 * you know that the entity's transformation is
	 * {@link TransformationInit#WEREWOLF}
	 */
	@SuppressWarnings("deprecation")
	public static boolean isTransformed(EntityLivingBase werewolf) {
		return werewolf instanceof EntityPlayer ? getIWerewolf((EntityPlayer) werewolf).isTransformed()
				: werewolf instanceof ITransformedWerewolf;
	}

	/** Shortcut method for getting the IWerewolf capability */
	public static IWerewolf getIWerewolf(@Nonnull EntityPlayer player) {
		Validate.notNull(player);
		return player.getCapability(CAPABILITY_WEREWOLF, null);
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
				getIWerewolf(player).setTransformed(transformed);
				PacketHandler.sendWerewolfTransformedMessage(player);
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
						WrongTransformationException.ifNotTransformationThrow(entity, TransformationInit.WEREWOLF);
						EntityWerewolf werewolf = new EntityWerewolf(world, transformation.getTextureIndex(),
								entity.getClass().getName(), TransformationHelper.postExtraDataEvent(entity, true));
						return werewolf;
					} else {
						ITransformationCreature tc = TransformationHelper.getITransformationCreature(entity);
						if (tc != null) {
							WrongTransformationException.ifNotTransformationThrow(entity, TransformationInit.WEREWOLF);
							EntityWerewolf werewolf = new EntityWerewolf(world, tc.getTextureIndex(),
									"$bycap" + entity.getClass().getName(),
									TransformationHelper.postExtraDataEvent(entity, true));
							return werewolf;
						}
						throw new IllegalArgumentException(
								"Entity does not implement interface \"ITransformation\" or has TransformationCreature capability");
					}
				}
			}
		} else {
			throw new WrongSideException("Can't transform entity to werewolf on client side", world);
		}
		return null;
	}

	/**
	 * Called from
	 * {@link EventHandler#onPlayerJoin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent)}
	 */
	public static void resetTransformationStage(EntityPlayerMP player) {
		WerewolfHelper.getIWerewolf(player).setTransformationStage(0);
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
		WrongTransformationException.ifNotTransformationThrow(werewolf, TransformationInit.WEREWOLF);
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
		return shouldUseSneakingModel(werewolf) ? 1.9F : 2.4F;
	}

	public static float getWerewolfEyeHeight(Entity werewolf) {
		return shouldUseSneakingModel(werewolf) ? 1.7F : 2.2F;
	}

	public static boolean shouldUseSneakingModel(Entity werewolf) {
		return werewolf.isSneaking();
	}

	/**
	 * Implement this interface if you want
	 * {@link WerewolfHelper#isTransformed(net.minecraft.entity.EntityLivingBase)}
	 * to return true
	 */
	public static interface ITransformedWerewolf {
	}
}
