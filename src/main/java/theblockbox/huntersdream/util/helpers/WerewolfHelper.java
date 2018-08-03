package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

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

	// TODO: Add rituals that affect levelling
	public static double getWerewolfLevel(EntityPlayer player) {
		if (TransformationHelper.getTransformation(player) == Transformations.WEREWOLF) {
			double level = ((double) TransformationHelper.getCap(player).getXP()) / 500.0;

			if (level >= 6) {
				level = 5;
			}

			return level;
		} else {
			throw new WrongTransformationException("Given player is not a werewolf",
					TransformationHelper.getTransformation(player));
		}
	}

	public static void applyLevelBuffs(EntityPlayer player) {
		if (TransformationHelper.getTransformation(player) == Transformations.WEREWOLF
				&& TransformationHelper.getITransformation(player).transformed()) {
			int level = TransformationHelper.getTransformation(player).getLevelFloor(player);
			int duration = 40;

			// Caution! Duration in ticks
			switch (level) {
			case 11:
				player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, duration, 20, false, false));
			case 10:

			case 9:
				player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, duration, 20, false, false));
			case 8:

			case 7:
				player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, duration, 20, false, false));
			case 6:

			case 5:
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, 0, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, 0, false, false));
			case 4:

			case 3:
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 0, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, 0, false, false));
			case 2:

			case 1:
				player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, duration, 0, false, false));
				break;
			}
			player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration, 0, false, false));
		} else {
			throw new WrongTransformationException("The given player isn't a werewolf and/or transformed",
					TransformationHelper.getTransformation(player));
		}
	}

	// TODO: Make infection
	public static void infect(EntityLivingBase entityToBeInfected) {
		if (TransformationHelper.canChangeTransformation(entityToBeInfected)) {

		} else {
			throw new WrongTransformationException("Given entity can't be infected");
		}
	}

	/**
	 * Only for werewolves
	 */
	public static int getWerewolfStrengthMultiplier(EntityLivingBase entity) {
		if ((TransformationHelper.getTransformedTransformation(entity) == Transformations.WEREWOLF)
				&& TransformationHelper.getITransformation(entity).transformed()) {
			if (entity instanceof EntityPlayer) {
				return 8;
			} else {
				return 8;
			}
		} else if (!TransformationHelper.getITransformation(entity).transformed()
				&& (TransformationHelper.getTransformedTransformation(entity) == Transformations.WEREWOLF)) {
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
		if (TransformationHelper.getTransformation(entity) == Transformations.WEREWOLF
				&& TransformationHelper.getITransformation(entity).transformed()) {
			// if entity is a player
			if (entity instanceof EntityPlayer) {

				EntityPlayer player = (EntityPlayer) entity;
				// and level is higher than five
				if (TransformationHelper.getTransformation(player).getLevelFloor(player) >= 5) {
					return true;
				}

			} else if (!(entity instanceof EntityPlayer)) {
				return true;
			}
		}

		return false;
	}

	public static int getInfectionPercentage(EntityLivingBase entity) {
		if (canInfect(entity)) {
			if (entity instanceof EntityPlayer) {
				return 100;
			} else if (entity instanceof EntityLivingBase) {
				return 25;
			} else {
				System.err.println("Found entity that can infect but has no infection percantage... Using 25%");
				return 25;
			}
		} else {
			throw new WrongTransformationException("The given entity is not a werewolf");
		}
	}

	// TODO: Set min level to 8 (4 only because you can't level higher)
	/** Returns true when the player has control in the werewolf form */
	public static boolean hasControl(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		if (cap.getTransformation() != Transformations.WEREWOLF) {
			throw new WrongTransformationException("The given player is not a werewolf", cap.getTransformation());
		}
		return (cap.getTransformation().getLevelFloor(player) >= 0); // TODO: Change this!
	}

	public static EntityPlayer getPlayer(EntityWerewolf werewolf) {
		if (werewolf.getEntityName().startsWith("player")) {
			return werewolf.world
					.getPlayerEntityByName(werewolf.getEntityName().substring(6, werewolf.getEntityName().length()));
		} else {
			throw new IllegalArgumentException("Werewolf is not a player");
		}
	}

	/**
	 * Call this method in your {@link Entity#onUpdate()} method to let your mob
	 * transform into a werewolf when it is night. Caution! Your entity has to
	 * extend EntityLiving or a subclass and needs a constructor World, int,
	 * Transformations (World = spawn world, int = texture to use, Transformations =
	 * transformation that the entity should have) When the werewolf transforms
	 * back, this constructor will be called and World will be
	 * {@link EntityWerewolf#getEntityWorld()}, int will be
	 * {@link EntityWerewolf#getTextureIndex()} and Transformations will be
	 * {@link Transformations#WEREWOLF}
	 * 
	 * @param entity The entity to be transformed
	 */
	public static void toWerewolfWhenNight(EntityLiving entity) {
		World world = entity.world;
		if (!world.isRemote) {
			if (entity.ticksExisted % 40 == 0) {
				if (WerewolfHelper.isWerewolfTime(entity)) {
					if (entity instanceof ITransformation) {
						ITransformation transformation = (ITransformation) entity;
						if (TransformationHelper.getTransformedTransformation(entity) == Transformations.WEREWOLF) {
							EntityWerewolf werewolf = new EntityWerewolf(world, transformation.getTextureIndex(),
									entity.getClass().getName());
							werewolf.setPosition(entity.posX, entity.posY, entity.posZ);
							world.removeEntity(entity);
							world.spawnEntity(werewolf);
						} else {
							throw new WrongTransformationException("Entity is not a werewolf",
									TransformationHelper.getTransformedTransformation(entity));
						}
					} else {
						if (entity instanceof EntityCreature) {
							EntityCreature creature = (EntityCreature) entity;
							ITransformationCreature tc = TransformationHelper.getITransformationCreature(creature);
							if (tc != null) {
								if (tc.getCurrentTransformation() == Transformations.WEREWOLF) {
									EntityWerewolf werewolf = new EntityWerewolf(world, tc.getTextureIndex(),
											"$bycap" + entity.getClass().getName());
									werewolf.setPosition(entity.posX, entity.posY, entity.posZ);
									world.removeEntity(entity);
									world.spawnEntity(werewolf);
									return;
								} else {
									throw new WrongTransformationException("Entity is not a werewolf",
											tc.getCurrentTransformation());
								}
							}
						}
						throw new IllegalArgumentException(
								"Entity does not implement interface \"ITransformation\" or has TransformationCreature capability");
					}
				}
			}
		}
	}

	public static float getEffectivenessAgainstWerewolf(Object object) {
		float value = TransformationHelper.getEffectivenessAgainst(Transformations.WEREWOLF, object);
		System.out.println("Object: " + object.getClass().getName() + " Value: " + value);
		return value;
	}

	public static boolean effectiveAgainstWerewolf(Object object) {
		boolean flag = TransformationHelper.effectiveAgainstTransformation(Transformations.WEREWOLF, object);
		System.out.println("Object: " + object.getClass().getName() + " Effective: " + flag);
		return flag;
	}
}
