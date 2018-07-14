package pixeleyestudios.huntersdream.util.helpers;

import java.util.Random;

public class ChanceHelper {
	public static Boolean chanceOf(Random random, int percentage) {
		if (percentage > 100 || percentage <= 0) {
			return null;
		}

		return ((random.nextInt(100) + 1) <= percentage);
	}

	public static Boolean chanceOf(int percentage) {
		return chanceOf(new Random(), percentage);
	}
}
