package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.enums.Rituals;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon.InfectionStatus;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.util.interfaces.transformation.IWerewolf;

public class WerewolfHelper {

	/**
	 * Returns true when a werewolf can transform in this world (Caution! This
	 * method doesn't test if the entity is a werewolf or not)
	 */
	public static boolean isWerewolfTime(Entity entity) {
		boolean isNight = !entity.getServer().getWorld(0).isDaytime();
		boolean werewolfTime = entity.world.getCurrentMoonPhaseFactor() == 1F && isNight;
		return werewolfTime;
	}

	public static boolean playerNotUnderBlock(EntityPlayer player) {
		return player.world.canBlockSeeSky(new BlockPos(player.posX, player.posY + 1, player.posZ));
	}

	public static double getWerewolfLevel(EntityPlayer player) {
		if (TransformationHelper.getTransformation(player) == Transformations.WEREWOLF) {
			ITransformationPlayer cap = TransformationHelper.getCap(player);
			double level = ((double) cap.getXP()) / 500.0;
			if (!cap.hasRitual(Rituals.LUPUS_ADVOCABIT))
				level = 0;
			else
				level += 1;

			if (level >= 6 && !cap.hasRitual(Rituals.WEREWOLF_SECOND_RITE))
				level = 5.99999D;

			if (level >= 8) {
				level = 7.99999D;
			}

			return level;
		} else {
			throw new WrongTransformationException("Given player is not a werewolf",
					TransformationHelper.getTransformation(player));
		}
	}

	public static void applyLevelBuffs(EntityPlayer player) {
		if (hasControl(player)) {
			if (TransformationHelper.getTransformation(player) == Transformations.WEREWOLF
					&& TransformationHelper.getITransformation(player).transformed()) {
				int level = TransformationHelper.getCap(player).getLevelFloor();
				int duration = 400;

				// Caution! Duration in ticks
				switch (level) {
				case 11:
					player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, duration, 20, false, false));
				case 10:

				case 9:
					player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, duration, 20, false, false));
				case 8:

				case 7:
					player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, duration, 10, false, false));
				case 6:

				case 5:
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, 0, false, false));
				case 4:

				case 3:
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 0, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, 0, false, false));
				case 2:

				case 1:
					player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, duration, 0, false, false));
					break;
				}
				player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration, 2, false, false));
			} else {
				throw new WrongTransformationException("The given player isn't a werewolf and/or transformed",
						TransformationHelper.getTransformation(player));
			}
		}
	}

	public static float calculateUnarmedDamage(EntityLivingBase entity) {
		if (TransformationHelper.getTransformation(entity) == Transformations.WEREWOLF) {
			if (TransformationHelper.getITransformation(entity).transformed()) {
				if (entity instanceof EntityPlayer) {
					return 12F;
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

	public static float calculateProtection(EntityLivingBase entity) {
		if (TransformationHelper.getTransformation(entity) == Transformations.WEREWOLF) {
			if (TransformationHelper.getITransformation(entity).transformed()) {
				if (entity instanceof EntityPlayer) {
					return 20F;
				} else {
					// standard
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

	/**
	 * Returns true if the werewolf can transform and applies effects (for example
	 * that werewolves get hunger when wolfsbane potion is active)
	 */
	public static boolean canWerewolfTransform(EntityLivingBase werewolf) {
		if (!canWerewolfTransformWithoutEffects(werewolf)) {
			if (werewolf.isPotionActive(PotionInit.POTION_WOLFSBANE)) {
				werewolf.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 100));
				werewolf.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100));
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Does exactly the same as {@link #canWerewolfTransform(EntityLivingBase)}
	 * except that no effects get applied
	 */
	public static boolean canWerewolfTransformWithoutEffects(EntityLivingBase werewolf) {
		if (TransformationHelper.getTransformation(werewolf) == Transformations.WEREWOLF) {
			return !werewolf.isPotionActive(PotionInit.POTION_WOLFSBANE);
		} else {
			throw new WrongTransformationException("Entity is not a werewolf",
					TransformationHelper.getTransformation(werewolf));
		}
	}

	public static void infectEntityAsWerewolf(EntityLivingBase entityToBeInfected) {
		if (TransformationHelper.canChangeTransformation(entityToBeInfected)
				&& TransformationHelper.canBeInfectedWith(Transformations.WEREWOLF, entityToBeInfected)) {
			if (entityToBeInfected instanceof EntityPlayer)
				entityToBeInfected
						.sendMessage(new TextComponentTranslation("transformations.huntersdream.infected.werewolf"));
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0, false, true));
			entityToBeInfected.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 1, false, true));
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(entityToBeInfected);
			ionm.setInfectionStatus(InfectionStatus.MOON_ON_INFECTION);
			ionm.setInfectionTick(entityToBeInfected.ticksExisted);
			ionm.setInfectionTransformation(Transformations.WEREWOLF);
		}
	}

	public static IInfectOnNextMoon getIInfectOnNextMoon(EntityLivingBase entity) {
		return entity.getCapability(CapabilitiesInit.CAPABILITY_INFECT_ON_NEXT_MOON, null);
	}

	/**
	 * Only for werewolves
	 */
	public static int getWerewolfStrengthMultiplier(EntityLivingBase entity) {
		if ((TransformationHelper.getTransformation(entity) == Transformations.WEREWOLF)
				&& TransformationHelper.getITransformation(entity).transformed()) {
			if (entity instanceof EntityPlayer) {
				return 8;
			} else {
				return 8;
			}
		} else if (WerewolfHelper.transformedWerewolf(entity)) {
			return 1;
		} else {
			throw new WrongTransformationException("The given entity is not a werewolf");
		}
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

	public static int getInfectionPercentage(EntityLivingBase entity) {
		if (canInfect(entity)) {
			if (entity instanceof EntityPlayer) {
				// TODO: could cause issues when player is higher than level 20
				return TransformationHelper.getCap((EntityPlayer) entity).getLevelFloor() * 5;
			} else if (entity instanceof EntityLivingBase) {
				return 50;
			} else {
				Main.getLogger().error("Found entity that can infect but has no infection percantage... Using 25%");
				return 50;
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
		if (cap.getTransformation() != Transformations.WEREWOLF) {
			throw new WrongTransformationException("The given player is not a werewolf", cap.getTransformation());
		}
		return (cap.getLevelFloor() > 0);
	}

	/**
	 * Returns true when the given player has mainly control in the werewolf form
	 */
	public static boolean hasMainlyControl(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		if (cap.getTransformation() != Transformations.WEREWOLF) {
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
	 * {@link TransformationHelper#transformedTransformation(EntityLivingBase, Transformations)}
	 */
	public static boolean transformedWerewolf(EntityLivingBase entity) {
		return TransformationHelper.transformedTransformation(entity, Transformations.WEREWOLF);
	}

	public static IWerewolf getIWerewolf(EntityPlayer player) {
		return player.getCapability(CapabilitiesInit.CAPABILITY_WEREWOLF, null);
	}

	/**
	 * Caution! Your entity has to extend EntityLiving or a subclass and needs a
	 * constructor World, int, Transformations (World = spawn world, int = texture
	 * to use, Transformations = transformation that the entity should have) When
	 * the werewolf transforms back, this constructor will be called and World will
	 * be {@link EntityWerewolf#getEntityWorld()}, int will be
	 * {@link EntityWerewolf#getTextureIndex()} and Transformations will be
	 * {@link Transformations#WEREWOLF}
	 * 
	 * @param entity The entity to be transformed
	 */
	public static EntityLivingBase toWerewolfWhenNight(EntityLiving entity) {
		World world = entity.world;
		if (!world.isRemote) {
			if (WerewolfHelper.isWerewolfTime(entity)) {
				if (canWerewolfTransform(entity)) {
					if (entity instanceof ITransformation) {
						ITransformation transformation = (ITransformation) entity;
						if (transformation.getTransformation() == Transformations.WEREWOLF) {
							EntityWerewolf werewolf = new EntityWerewolf(world, transformation.getTextureIndex(),
									entity.getClass().getName());
							return werewolf;
						} else {
							throw new WrongTransformationException("Entity is not a werewolf",
									TransformationHelper.getTransformation(entity));
						}
					} else {
						if (entity instanceof EntityCreature) {
							EntityCreature creature = (EntityCreature) entity;
							ITransformationCreature tc = TransformationHelper.getITransformationCreature(creature);
							if (tc != null) {
								if (tc.getTransformation() == Transformations.WEREWOLF) {
									EntityWerewolf werewolf = new EntityWerewolf(world, tc.getTextureIndex(),
											"$bycap" + entity.getClass().getName());
									return werewolf;
								} else {
									throw new WrongTransformationException("Entity is not a werewolf",
											tc.getTransformation());
								}
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
}
