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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.ExtraDataEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent.WerewolfTransformingReason;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.Reference;
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
	public static final DamageSource WEREWOLF_TRANSFORMATION_DAMAGE = new DamageSource(
			"huntersdream:werewolfTransformationDamage");
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
		validateIsWerewolf(entity);
		// if the werewolf is either not transformed or has something in its hands
		// (shouldn't happen for players)
		boolean transformed = isTransformed(entity);
		if (entity instanceof EntityPlayer) {
			if (transformed) {
				// unarmed damage is 6
				return entity.getHeldItemMainhand().isEmpty() ? 6F : initialDamage;
			} else {
				// "Their [untransformed] melee damage is normal damage +2"
				return initialDamage + 2;
			}
		} else {
			if (transformed) {
				// "Their attack is 4 hearts per slash and 5 hearts per bite"
				return wasLastAttackBite(entity) ? 5F : 4F;
			} else {
				// no damage change for untransformed werewolves
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
		validateIsWerewolf(entity);
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
		validateIsWerewolf(werewolf);
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
	public static boolean canInfect(EntityLivingBase entity) {
		// all werewolves (players included) can always infect (given they're
		// transformed)
		return isTransformed(entity) && wasLastAttackBite(entity);
	}

	/**
	 * Returns the chance of a werewolf infecting a mob/player
	 * 
	 * @param entity The entity whose chance to return
	 * @return Returns the chance of infection (100% = always, 0% = never)
	 */
	public static int getInfectionPercentage(EntityLivingBase entity) {
		if (canInfect(entity)) {
			// TODO: Return different infection percantage if entity is player
			return 25;
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
		validateIsWerewolf(player);
		return (cap.getLevelFloor() > 0);
	}

	/**
	 * Returns true when the given entity is transformed and also a werewolf
	 */
	public static boolean isTransformed(EntityLivingBase entity) {
		return (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF)
				&& TransformationHelper.getTransformationData(entity).getBoolean("transformed");
	}

	/** Transforms a werewolf. No packet is being sent to the client. */
	public static void setTransformed(EntityLivingBase werewolf, boolean transformed) {
		validateIsWerewolf(werewolf);
		NBTTagCompound compound = TransformationHelper.getTransformationData(werewolf);
		compound.setBoolean("transformed", transformed);
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
						validateIsWerewolf(entity);
						EntityWerewolf werewolf = new EntityWerewolf(world, transformation.getTextureIndex(),
								entity.getClass().getName(), postExtraDataEvent(entity, true));
						return werewolf;
					} else {
						validateIsWerewolf(entity);
						EntityWerewolf werewolf = new EntityWerewolf(world, TransformationHelper
								.getITransformationCreature(entity)
								.orElseThrow(() -> new IllegalArgumentException(
										"Entity does not implement interface \"ITransformation\" or has TransformationCreature capability"))
								.getTextureIndex(), "$bycap" + entity.getClass().getName(),
								postExtraDataEvent(entity, true));
						return werewolf;
					}
				}
			}
		} else {
			throw new WrongSideException("Can't transform entity to werewolf on client side", world);
		}
		return null;
	}

	private static NBTTagCompound postExtraDataEvent(EntityCreature creature, boolean onDataSave) {
		ExtraDataEvent event = new ExtraDataEvent(creature, GeneralHelper.writeEntityToNBT(creature), onDataSave);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getExtraData();
	}

	public static int getTransformationStage(EntityPlayerMP player) {
		validateIsWerewolf(player);
		return TransformationHelper.getTransformationData(player).getInteger("transformationStage");
	}

	public static void setTransformationStage(EntityPlayerMP player, int stage) {
		validateIsWerewolf(player);
		NBTTagCompound compound = TransformationHelper.getTransformationData(player);
		compound.setInteger("transformationStage", stage);
	}

	/** Returns the player.ticksExisted when the transformation has started */
	public static int getTimeSinceTransformation(EntityPlayerMP player) {
		validateIsWerewolf(player);
		return TransformationHelper.getTransformationData(player).getInteger("timeSinceTransformation");
	}

	/**
	 * Sets a werewolf player's time since the last transformation. No packet is
	 * being sent to the client
	 */
	public static void setTimeSinceTransformation(EntityPlayerMP player, int time) {
		validateIsWerewolf(player);
		NBTTagCompound compound = TransformationHelper.getTransformationData(player);
		compound.setInteger("timeSinceTransformation", time);
	}

	public static int getSoundTicks(EntityPlayerMP player) {
		validateIsWerewolf(player);
		return TransformationHelper.getTransformationData(player).getInteger("soundTicks");
	}

	/**
	 * Sets a werewolf player's sound ticks (used for the passive werewolf sounds).
	 * No packet is being sent to the client
	 */
	public static void setSoundTicks(EntityPlayerMP player, int ticks) {
		validateIsWerewolf(player);
		NBTTagCompound compound = TransformationHelper.getTransformationData(player);
		compound.setInteger("soundTicks", ticks);
	}

	public static boolean wasLastAttackBite(EntityLivingBase entity) {
		validateIsWerewolf(entity);
		return TransformationHelper.getTransformationData(entity).getBoolean("lastAttackBite");
	}

	/**
	 * Sets the werewolf's last attack to bite. No packet is being sent to the
	 * client
	 */
	public static void setLastAttackBite(EntityLivingBase entity, boolean wasBite) {
		validateIsWerewolf(entity);
		NBTTagCompound compound = TransformationHelper.getTransformationData(entity);
		compound.setBoolean("lastAttackBite", wasBite);
	}

	// add potion effects to werewolves
	// TODO: Make that it practically would also work when it's day, as long as the
	// werewolf is transformed
	public static void applyLevelBuffs(EntityPlayerMP werewolf) {
		int duration = 101;
		werewolf.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 400, 0, false, false));
		werewolf.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 0, false, false));
		werewolf.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, 0, false, false));
		werewolf.addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration, 2, false, false));
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
		validateIsWerewolf(werewolf);
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

	public static void validateIsWerewolf(EntityLivingBase entity) {
		TransformationHelper.getTransformation(entity).validateEquals(Transformation.WEREWOLF);
	}
}
