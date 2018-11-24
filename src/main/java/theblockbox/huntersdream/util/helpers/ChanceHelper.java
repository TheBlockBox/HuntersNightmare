package theblockbox.huntersdream.util.helpers;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ChanceHelper {
	/**
	 * Returns a random byte. If the given bound is 0, 0 is returned. If the given
	 * byte is negative, an exception is thrown
	 */
	public static byte randomByte(Random random, byte bound) {
		return bound == 0 ? 0 : (byte) random.nextInt(bound);
	}

	public static boolean chanceOf(Random random, float percentage) {
		if (percentage > 100 || percentage < 0) {
			throw new IllegalArgumentException("Percentage can't be over 100 / under 0");
		}

		return ((random.nextInt(1000) + 1) <= ((int) (percentage * 10F)));
	}

	public static boolean chanceOf(Random random, int percentage) {
		if (percentage > 100 || percentage < 0) {
			throw new IllegalArgumentException("Percentage can't be over 100 / under 0");
		}

		return ((random.nextInt(1000) + 1) <= ((int) (percentage * 10F)));
	}

	public static boolean chanceOf(Entity entity, float percentage) {
		return chanceOf(entity.world, percentage);
	}

	public static boolean chanceOf(World world, float percentage) {
		return chanceOf(world.rand, percentage);
	}

	public static boolean chanceOf(Entity entity, int percentage) {
		return chanceOf(entity.world, percentage);
	}

	public static boolean chanceOf(World world, int percentage) {
		return chanceOf(world.rand, percentage);
	}
}
