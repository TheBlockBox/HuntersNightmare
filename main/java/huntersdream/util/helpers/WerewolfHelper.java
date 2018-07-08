package huntersdream.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;

public class WerewolfHelper {

	/**
	 * Returns true when a werewolf can transform in this world (Caution! This
	 * method doesn't test if the entity is a werewolf or not)
	 */
	public static boolean isWerewolfTime(Entity entity) {
		boolean isNight = !entity.getServer().worlds[0].isDaytime();
		boolean werewolfTime = entity.world.getCurrentMoonPhaseFactor() == 1F && isNight;
		return werewolfTime;
	}

	// TODO: Make real test
	public static boolean playerNotUnderBlock(EntityPlayer player) {
		for (int i = (int) player.posY; i < 256; i++) {
			if (!player.world.isAirBlock(new BlockPos(player.posX, i, player.posZ))) {
				return false;
			}
		}
		return true;
	}

	public static int getWerewolfLevel(EntityPlayer player) {
		int level = (int) (Math.floor(TransformationHelper.getCap(player).getXP()) / (50 * 120));
		// 1 min = 60 s = 60 * 1s = 60 * 20 ticks = 120 ticks
		if (level >= 6) {
			level = 5;
		}
		return level;
	}

	public static void applyLevelBuffs(EntityPlayer player) {
		int level = getWerewolfLevel(player);

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
