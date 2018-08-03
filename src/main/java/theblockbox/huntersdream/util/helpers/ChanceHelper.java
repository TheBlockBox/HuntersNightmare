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

	public static boolean chanceOf(Random random, int percentage) {
		if (percentage > 100 || percentage < 0) {
			throw new IllegalArgumentException("Percentage can't be over 100 / under 0");
		}

		return ((random.nextInt(100) + 1) <= percentage);
	}

	public static boolean chanceOf(int percentage) {
		return chanceOf(ChanceHelper.RANDOM, percentage);
	}
}
