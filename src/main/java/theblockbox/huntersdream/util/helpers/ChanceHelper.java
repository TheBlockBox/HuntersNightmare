package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Rotation;
import net.minecraft.world.World;

import java.util.Random;

public class ChanceHelper {
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
}
