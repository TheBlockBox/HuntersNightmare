package theblockbox.huntersdream.util.helpers;

import java.util.stream.IntStream;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.EventHandler;
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

	/** Returns the player's current werewolf as a double level */
	public static double getWerewolfLevel(EntityPlayer player) {
		if (TransformationHelper.getTransformation(player) == TransformationInit.WEREWOLF) {
			ITransformationPlayer cap = TransformationHelper.getCap(player);
			double level = (cap.getXP()) / 500.0;
			if (!cap.hasRitual(Rituals.LUPUS_ADVOCABIT))
				level = 0;
			else
				level += 1;

			if (level >= 6 && !cap.hasRitual(Rituals.WEREWOLF_SECOND_RITE)) {
				level = 5.99999D;
			}

			if (level >= 9) {
				level = 8.99999D;
			}
			return level;
		} else {
			throw new WrongTransformationException("Given player is not a werewolf",
					TransformationHelper.getTransformation(player));
		}
	}

	/**
	 * Returns how much damage the given entity does unarmed (this method can also
	 * be called when the entity is not transformed, as long as it's still a
	 * werewolf)
	 */
	public static float calculateUnarmedDamage(EntityLivingBase entity) {
		if (TransformationHelper.getTransformation(entity) == TransformationInit.WEREWOLF) {
			// if the werewolf is either not transformed or has something in its hands
			// (shouldn't happen for players)
			if (TransformationHelper.getITransformation(entity).transformed()
					|| !entity.getHeldItemMainhand().isEmpty()) {
				if (entity instanceof EntityPlayer) {
					int level = TransformationHelper.getCap((EntityPlayer) entity).getLevelFloor();
					float unarmedDamage = IntStream.of(1, 7, 9, 11, 12).filter(i -> level >= i).mapToLong(i -> 3).sum();
					// if no damage, set to 1 (0 would cause the player to deal no damage because x
					// * 0 = 0)
					unarmedDamage = (unarmedDamage > 0) ? unarmedDamage : 1;
					return unarmedDamage;
				} else {
					// standard
					return 12F;
				}
			} else {
				// no extra damage when not transformed
				return 1F;
			}
		} else {
			throw new WrongTransformationException("The given entity is not a werewolf",
					TransformationHelper.getTransformation(entity));
		}
	}

	/**
	 * Returns how much protection the given entity has (this method can also be
	 * called when the entity is not transformed, as long as it's still a werewolf)
	 */
	public static float calculateProtection(EntityLivingBase entity) {
		if (TransformationHelper.getTransformation(entity) == TransformationInit.WEREWOLF) {
			if (TransformationHelper.getITransformation(entity).transformed()) {
				if (entity instanceof EntityPlayer) {
					int level = TransformationHelper.getCap((EntityPlayer) entity).getLevelFloor();
					float protection = (IntStream.of(6, 7, 9, 11, 12).filter(i -> level >= i).mapToLong(i -> 4).sum());
					// if no protection, set to 1 (0 would cause an ArithmaticException because of
					// dividing by zero)
					protection = (protection > 0) ? protection : 1;
					return protection;
				} else {
					return 20F;
				}
			} else {
				// no protection when not transformed
				return 1F;
			}
		} else {
			throw new WrongTransformationException("The given entity is not a werewolf",
					TransformationHelper.getTransformation(entity));
		}
	}

	/** Applies potion effects to transformed werewolf players */
	public static void applyLevelBuffs(EntityPlayer player) {
		// if (hasControl(player)) { // TODO: Uncomment this when no control is done
		if (TransformationHelper.getTransformation(player) == TransformationInit.WEREWOLF
				&& TransformationHelper.getITransformation(player).transformed()) {
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
		if (TransformationHelper.getTransformation(werewolf) == TransformationInit.WEREWOLF) {
			return !werewolf.isPotionActive(PotionInit.POTION_WOLFSBANE);
		} else {
			throw new WrongTransformationException("Entity is not a werewolf",
					TransformationHelper.getTransformation(werewolf));
		}
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

	public static IInfectOnNextMoon getIInfectOnNextMoon(EntityLivingBase entity) {
		return entity.getCapability(CAPABILITY_INFECT_ON_NEXT_MOON, null);
	}

	/**
	 * Returns true if the given entity can infect other entities (it also checks if
	 * the entity is a werewolf and transformed!)
	 */
	public static boolean canInfect(EntityLivingBase entity) {
		// all werewolves (players included) can always infect (given they're
		// transformed)
		return transformedWerewolf(entity);
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
				return 0; // TODO: Set to 50
			}
		} else {
			throw new WrongTransformationException("The given entity is not a werewolf",
					TransformationHelper.getTransformation(entity));
		}
	}

	/**
	 * Returns true when the player has control in the werewolf form
	 * 
	 * @deprecated Don't use, we're going to make a new no control mechanic. Instead
	 *             use {@link #hasMainlyControl(EntityPlayer)}.
	 */
	@Deprecated
	public static boolean hasControl(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		if (cap.getTransformation() != TransformationInit.WEREWOLF) {
			throw new WrongTransformationException("The given player is not a werewolf", cap.getTransformation());
		}
		return (cap.getLevelFloor() > 0);
	}

	/**
	 * Returns true when the given player has mainly control in the werewolf form
	 */
	public static boolean hasMainlyControl(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		if (cap.getTransformation() != TransformationInit.WEREWOLF) {
			throw new WrongTransformationException("The given player is not a werewolf", cap.getTransformation());
		}
		return (cap.getLevelFloor() > 0);
	}

	/**
	 * @deprecated Will soon be removed because we are going to change the no
	 *             control mechanic
	 */
	@Deprecated
	public static EntityPlayer getPlayer(EntityWerewolf werewolf) {
		if (werewolf.getUntransformedEntityName().startsWith("player")) {
			return werewolf.world.getPlayerEntityByName(
					werewolf.getUntransformedEntityName().substring(6, werewolf.getUntransformedEntityName().length()));
		} else {
			throw new IllegalArgumentException("Werewolf is not a player");
		}
	}

	/**
	 * Shortcut method for
	 * {@link TransformationHelper#transformedTransformation(EntityLivingBase, Transformation)}
	 */
	public static boolean transformedWerewolf(EntityLivingBase entity) {
		ITransformation transformation = TransformationHelper.getITransformation(entity);
		return (transformation != null) && (transformation.getTransformation() == TransformationInit.WEREWOLF)
				&& transformation.transformed();
	}

	/** Shortcut method for getting the IWerewolf capability */
	public static IWerewolf getIWerewolf(EntityPlayer player) {
		return player.getCapability(CAPABILITY_WEREWOLF, null);
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
						if (transformation.getTransformation() == TransformationInit.WEREWOLF) {
							EntityWerewolf werewolf = new EntityWerewolf(world, transformation.getTextureIndex(),
									entity.getClass().getName(), TransformationHelper.postExtraDataEvent(entity, true));
							return werewolf;
						} else {
							throw new WrongTransformationException("Entity is not a werewolf",
									TransformationHelper.getTransformation(entity));
						}
					} else {
						ITransformationCreature tc = TransformationHelper.getITransformationCreature(entity);
						if (tc != null) {
							if (tc.getTransformation() == TransformationInit.WEREWOLF) {
								EntityWerewolf werewolf = new EntityWerewolf(world, tc.getTextureIndex(),
										"$bycap" + entity.getClass().getName(),
										TransformationHelper.postExtraDataEvent(entity, true));
								return werewolf;
							} else {
								throw new WrongTransformationException("Entity is not a werewolf",
										tc.getTransformation());
							}
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
}
