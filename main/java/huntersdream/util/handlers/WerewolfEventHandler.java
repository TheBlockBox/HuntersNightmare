package huntersdream.util.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles events which are important for transforming into a werewolf
 */
@Mod.EventBusSubscriber
public class WerewolfEventHandler {

	/**
	 * Returns true when a werewolf can transform in this world
	 */
	public static boolean isWerewolfTime(World world) {
		boolean werewolfTime = world.getCurrentMoonPhaseFactor() == 1F && (!world.isDaytime());
		return (werewolfTime);
	}

	public static boolean isPlayerUnderBlock(EntityPlayer player) {
		return false;
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent tickEvent) {
		EntityPlayer player = tickEvent.player;
		NBTTagCompound nbt = player.getEntityData();
		// TODO: Remove this later!
		nbt.setBoolean("isWerewolf", true);
		if (isWerewolfTime(player.world)) {
			if (nbt.getBoolean("isWerewolf")) {
				nbt.setBoolean("isTransformed", true);
				applyLevelBuffs(player, nbt.getInteger("werewolfXP"));
				if (!isPlayerUnderBlock(player)) {
					nbt.setInteger("timeUnderMoon", nbt.getInteger("timeUnderMoon") + 1);
				} else {
					nbt.setInteger("timeUnderMoon", 0);
				}
			}
		} else if (nbt.getBoolean("isWerewolf") && nbt.getBoolean("isTransformed")) {
			nbt.setBoolean("isTransformed", false);
		}
	}

	@SubscribeEvent
	public static void onPlayerJoin(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		NBTTagCompound nbt = player.getEntityData();

		if (!nbt.hasKey("isWerewolf")) {
			nbt.setBoolean("isWerewolf", false);
			nbt.setInteger("werewolfXP", 0);
			nbt.setBoolean("isTransformed", false); // if the player is in werewolf form
			nbt.setInteger("timeUnderMoon", 0);
			System.out.println("Added werewolf nbt to player " + player.getDisplayNameString());
		}
	}

	public static int getWerewolfLevel(EntityPlayer player) {
		int level = (int) Math.floor(player.getEntityData().getInteger("werewolfXP") / 500);
		if (level >= 6) {
			level = 5;
		}
		// TODO: Add ritual stuff
		return level;
	}

	private static void applyLevelBuffs(EntityPlayer player, int level) {
		switch (level) {
		case 11:
			player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 2, 20));
		case 10:

		case 9:
			player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 2, 20));
		case 8:

		case 7:
			player.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 2, 20));
		case 6:

		case 5:
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 2));
		case 4:

		case 3:
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 2));
		case 2:

		case 1:
			player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 2));
			break;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
		EntityPlayer player = event.getEntityPlayer();
		NBTTagCompound nbt = player.getEntityData();

		if (nbt.getBoolean("isWerewolf") && nbt.getBoolean("isTransformed")) {
			event.setCanceled(true);
			System.out.println("Rendering player with differently"); // TODO: Add special renderer
		}
	}
}