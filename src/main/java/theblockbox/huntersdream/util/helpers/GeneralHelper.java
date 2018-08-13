package theblockbox.huntersdream.util.helpers;

import java.util.ArrayList;
import java.util.Comparator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.TimedRunnable;
import theblockbox.huntersdream.util.exceptions.WrongSideException;

/** A utility class for all things that don't fit into the other helpers */
public class GeneralHelper {
	public static final ArrayList<TimedRunnable> RUNNABLES_TO_BE_EXECUTED = new ArrayList<>();

	public static Side getSideFromWorld(World world) {
		return (world.isRemote ? Side.CLIENT : Side.SERVER);
	}

	public static Side getSideFromEntity(Entity entity) {
		return getSideFromWorld(entity.world);
	}

	public static Side getOtherSide(Side side) {
		if (side != null) {
			return side == Side.CLIENT ? Side.SERVER : Side.CLIENT;
		} else {
			throw new NullPointerException("The given side is null");
		}
	}

	public static EntityPlayer getNearestPlayer(World world, Entity entity, double range) {
		double d0 = -1.0D;
		EntityPlayer entityplayer = null;

		for (int i = 0; i < world.playerEntities.size(); ++i) {
			EntityPlayer entityplayer1 = world.playerEntities.get(i);

			double distance = entityplayer1.getDistanceSq(entity.posX, entity.posY, entity.posZ);

			if ((range < 0.0D || distance < range * range) && (d0 == -1.0D || distance < d0) && (distance > 1.1)) {
				d0 = distance;
				entityplayer = entityplayer1;
			}
		}

		return entityplayer;
	}

	/**
	 * Executes a {@link Runnable} on or after a specific tick of a specific player
	 * (server side only!)
	 * 
	 * @param runnableToBeExecuted The runnable which should be executed
	 * @param onTick               The tick on that or after that the runnable
	 *                             should be executed
	 * @param player               The player whose tick must be equal to or higher
	 *                             than the onTick parameter so that the runnable
	 *                             can be executed
	 */
	public static void executeIn(Runnable runnableToBeExecuted, int onTick, EntityPlayer player) {
		if (GeneralHelper.getSideFromEntity(player) == Side.CLIENT)
			throw new WrongSideException("This method is server-side only", player.world);
		RUNNABLES_TO_BE_EXECUTED.add(new TimedRunnable(runnableToBeExecuted, onTick, player));
		RUNNABLES_TO_BE_EXECUTED.sort(new Comparator<TimedRunnable>() {
			/** Note: this comparator imposes orderings that are inconsistent with equals */
			@Override
			public int compare(TimedRunnable tr1, TimedRunnable tr2) {
				if (tr1.ON_TICK == tr2.ON_TICK) {
					return 0;
				} else if (tr1.ON_TICK > tr2.ON_TICK) {
					return 1;
				} else if (tr1.ON_TICK < tr2.ON_TICK) {
					return -1;
				} else {
					return Integer.MIN_VALUE;
				}
			}
		});
	}
}
