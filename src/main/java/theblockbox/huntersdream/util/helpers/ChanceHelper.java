package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Rotation;
import net.minecraft.world.World;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ChanceHelper {
    private static final InternalRandom INTERNAL_RANDOM = new InternalRandom();

    /**
     * Returns a random byte. If the given bound is 0, 0 is returned. If the given
     * byte is negative, an exception is thrown
     */
    public static byte randomByte(Random random, byte bound) {
        return bound == 0 ? 0 : (byte) random.nextInt(bound);
    }

    public static boolean chanceOf(Random random, double percentage) {
        if (percentage > 100.0D || percentage < 0.0D) {
            throw new IllegalArgumentException("Percentage can't be over 100 / under 0");
        }

        return random.nextDouble() < (percentage / 100.0D);
    }

    public static boolean chanceOf(Random random, int percentage) {
        if (percentage > 100 || percentage < 0) {
            throw new IllegalArgumentException("Percentage can't be over 100 / under 0");
        }

        return random.nextInt(100) < percentage;
    }

    public static boolean chanceOf(EntityLivingBase entity, double percentage) {
        return ChanceHelper.chanceOf(entity.getRNG(), percentage);
    }

    public static boolean chanceOf(World world, double percentage) {
        return ChanceHelper.chanceOf(world.rand, percentage);
    }

    public static boolean chanceOf(EntityLivingBase entity, int percentage) {
        return ChanceHelper.chanceOf(entity.getRNG(), percentage);
    }

    public static boolean chanceOf(World world, int percentage) {
        return ChanceHelper.chanceOf(world.rand, percentage);
    }

    public static Rotation randomRotation(Random random) {
        Rotation[] rotations = Rotation.values();
        return rotations[random.nextInt(rotations.length)];
    }

    // TODO: Make the random numbers less memory intensive?
    /**
     * Returns a pseudo random float between 0.0 (inclusive) and 1.0 (exclusive) from the given long.
     * Always returns the same float for the same long.
     */
    public static float consistentFloatFromSeed(long seed) {
        ChanceHelper.INTERNAL_RANDOM.setSeed(seed);
        return ChanceHelper.INTERNAL_RANDOM.nextFloat();
    }

    /**
     * Returns a pseudo int float between 0 (inclusive) and the given int (exclusive) from the given long.
     * Always returns the same int for the same long and bound.
     */
    public static int consistentIntFromSeed(long seed, int bound) {
        ChanceHelper.INTERNAL_RANDOM.setSeed(seed);
        System.out.println(seed + " " + INTERNAL_RANDOM.currentSeed);
        return ChanceHelper.INTERNAL_RANDOM.nextInt(bound);
    }

    private static class InternalRandom extends Random {
        private long currentSeed;

        @Override
        protected int next(int bits) {
            super.setSeed(this.currentSeed);
            int toReturn = super.next(bits);
            super.setSeed(this.currentSeed);
            return toReturn;
        }

        @Override
        public synchronized void setSeed(long seed) {
            this.currentSeed = seed;
        }
    }
}
