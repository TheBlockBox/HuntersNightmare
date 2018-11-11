package theblockbox.huntersdream.util.helpers;

import java.util.Random;

public class ChanceHelper {
	private static final Random RANDOM = new Random();

	public static int randomInt(int bound) {
		if (bound > 0) {
			return ChanceHelper.RANDOM.nextInt(bound);
		} else if (bound < 0) {
			throw new IllegalArgumentException("Bound must be positive");
		}

		return -1;
	}

	public static byte randomByte(byte bound) {
		if (bound > 0) {
			return (byte) ChanceHelper.RANDOM.nextInt(bound);
		} else if (bound < 0) {
			throw new IllegalArgumentException("Bound must be positive");
		}

		return -1;
	}

	public static boolean chanceOf(Random random, float percentage) {
		if (percentage > 100 || percentage < 0) {
			throw new IllegalArgumentException("Percentage can't be over 100 / under 0");
		}

		return ((random.nextInt(1000) + 1) <= ((int) (percentage * 10F)));
	}

	public static boolean randomBoolean() {
		return RANDOM.nextBoolean();
	}

	public static boolean chanceOf(float percentage) {
		return chanceOf(ChanceHelper.RANDOM, percentage);
	}

	public static boolean chanceOf(int percentage) {
		return chanceOf((float) percentage);
	}
}
