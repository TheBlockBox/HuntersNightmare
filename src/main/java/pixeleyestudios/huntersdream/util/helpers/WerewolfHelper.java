package pixeleyestudios.huntersdream.util.helpers;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import pixeleyestudios.huntersdream.entity.EntityWerewolf;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.interfaces.IEffectiveAgainstWerewolf;
import pixeleyestudios.huntersdream.util.interfaces.ITransformationPlayer;

public class WerewolfHelper {
	private static final ArrayList<Entity> ENTITIES_EFFECTIVE_AGAINST_WEREWOLF = new ArrayList<>();
	private static final ArrayList<Item> ITEMS_EFFECTIVE_AGAINST_WEREWOLF = new ArrayList<>();

	/**
	 * Returns true when a werewolf can transform in this world (Caution! This
	 * method doesn't test if the entity is a werewolf or not)
	 */
	public static boolean isWerewolfTime(Entity entity) {
		boolean isNight = !entity.getServer().worlds[0].isDaytime();
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
			return -1;
		}
	}

	public static void applyLevelBuffs(EntityPlayer player) {
		if (TransformationHelper.getTransformation(player) == Transformations.WEREWOLF) {
			int level = TransformationHelper.getTransformation(player).getLevelFloor(player);

			// Caution! Duration in ticks
			switch (level) {
			case 11:
				player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 600, 20, false, false));
			case 10:

			case 9:
				player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 600, 20, false, false));
			case 8:

			case 7:
				player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 600, 20, false, false));
			case 6:

			case 5:
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, 0, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 600, 0, false, false));
			case 4:

			case 3:
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 600, 0, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 600, 0, false, false));
			case 2:

			case 1:
				player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 600, 0, false, false));
				break;
			}
		}
	}

	// TODO: Make infection
	public static void infect(EntityLivingBase entity) {
		// addLupumVocant(entity);
	}

	public static boolean effectiveAgainstWerewolf(Object object) {
		if (object instanceof Entity) {
			Entity entity = (Entity) object;
			return (entity instanceof IEffectiveAgainstWerewolf
					|| ENTITIES_EFFECTIVE_AGAINST_WEREWOLF.contains(entity));
		} else if (object instanceof Item) {
			Item item = (Item) object;
			return (item instanceof IEffectiveAgainstWerewolf || ITEMS_EFFECTIVE_AGAINST_WEREWOLF.contains(item));
		} else if (object instanceof ItemStack) {
			Item item = ((ItemStack) object).getItem();
			return (item instanceof IEffectiveAgainstWerewolf || ITEMS_EFFECTIVE_AGAINST_WEREWOLF.contains(item));
		} else {
			return false;
		}
	}

	public static void addEffectiveAgainstWerewolf(EntityLivingBase entity) {
		ENTITIES_EFFECTIVE_AGAINST_WEREWOLF.add(entity);
	}

	public static void addEffectiveAgainstWerewolf(Item item) {
		ITEMS_EFFECTIVE_AGAINST_WEREWOLF.add(item);
	}

	public static int getEffectivenessAgainstWerewolf(Object object) {
		if (effectiveAgainstWerewolf(object)) {
			if (object instanceof IEffectiveAgainstWerewolf) {
				return ((IEffectiveAgainstWerewolf) object).getEffectiveness();
			} else {
				return IEffectiveAgainstWerewolf.DEFAULT_EFFECTIVENESS;
			}
		} else {
			return 1 / 20;
		}
	}

	public static int getWerewolfStrengthMultiplier(EntityLivingBase entity) {
		if ((TransformationHelper.getTransformation(entity) == Transformations.WEREWOLF)
				&& TransformationHelper.getITransformation(entity).transformed()) {
			if (entity instanceof EntityPlayer) {
				return 8;
			} else {
				return 8;
			}
		} else {
			return 0;
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
			}
		}

		return 0;
	}

	// TODO: Set min level to 8 (4 only because you can't level higher)
	/** Returns true when the player has control in the werewolf form */
	public static boolean hasControl(EntityPlayer player) {
		ITransformationPlayer cap = TransformationHelper.getCap(player);
		if (cap.getTransformation() != Transformations.WEREWOLF)
			throw new IllegalArgumentException(
					"Can't use this method on a non werewolf player (" + cap.getTransformation().toString() + ")");
		return (cap.getTransformation().getLevelFloor(player) >= 0);
	}

	public static EntityPlayer getPlayer(EntityWerewolf werewolf) {
		if (werewolf.getEntityName().startsWith("player")) {
			return werewolf.world
					.getPlayerEntityByName(werewolf.getEntityName().substring(6, werewolf.getEntityName().length()));
		} else {
			return null;
		}
	}
}
