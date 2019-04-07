package theblockbox.huntersdream.api.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.init.PotionInit;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.PacketHandler;

public class VampireHelper {
    public static void drinkBlood(EntityLivingBase vampire, EntityLivingBase drinkFrom) {
        VampireHelper.validateIsVampire(vampire);

        if (drinkFrom.isEntityUndead()) {
            vampire.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 60, 2));
        }
        if (vampire instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) vampire;
            if (VampireHelper.getBlood(vampire) < 20) {
                VampireHelper.incrementBlood(vampire);
            }
            PacketHandler.sendTransformationMessage(player);
        }
        drinkFrom.attackEntityFrom(new DamageSource("vampireDrankBlood"), 1.0F);
    }

    public static float calculateReducedDamage(EntityLivingBase vampire, float initialDamage) {
        VampireHelper.validateIsVampire(vampire);
        return initialDamage;
    }

    // TODO: Make better values
    public static float calculateDamage(EntityLivingBase vampire, float initialDamage) {
        VampireHelper.validateIsVampire(vampire);
        if (vampire.getHeldItemMainhand().isEmpty()) {
            return 10.0F * initialDamage;
        } else {
            return initialDamage;
        }
    }

    /**
     * Throws a {@link WrongTransformationException} if the given entity is not a
     * vampire
     */
    public static void validateIsVampire(EntityLivingBase toCheck) {
        TransformationHelper.getTransformation(toCheck).validateEquals(Transformation.VAMPIRE);
    }

    public static boolean shouldVampireBurn(EntityLivingBase vampire) {
        VampireHelper.validateIsVampire(vampire);
        World world = vampire.world;
        if (world.isRemote)
            throw new WrongSideException("Can only test if vampire should burn on server side", world);
        // TODO: Check for glasses
        BlockPos pos = vampire.getPosition();
        boolean canSeeSky = world.canSeeSky(pos);
        if (!canSeeSky) {
            canSeeSky = GeneralHelper.canBlockSeeSky(world, pos);
        }
        return !vampire.isPotionActive(PotionInit.POTION_SUNSCREEN) && world.isDaytime() && canSeeSky;
    }

    /**
     * Returns a vampire's current blood. One blood is half a blood drop.
     */
    public static int getBlood(EntityLivingBase entity) {
        return MathHelper.ceil(VampireHelper.getBloodDouble(entity));
    }

    public static double getBloodDouble(EntityLivingBase entity) {
        VampireHelper.validateIsVampire(entity);
        return TransformationHelper.getTransformationData(entity).getDouble("blood");
    }

    /**
     * Sets the blood of a vampire to the given double
     * No packet is being sent to the client
     */
    public static void setBlood(EntityLivingBase entity, double blood) {
        VampireHelper.validateIsVampire(entity);
        NBTTagCompound compound = TransformationHelper.getTransformationData(entity);
        compound.setDouble("blood", blood);
    }

    public static void incrementBlood(EntityLivingBase entity) {
        VampireHelper.setBlood(entity, VampireHelper.getBloodDouble(entity) + 1.0D);
    }

    public static void decrementBlood(EntityLivingBase entity) {
        VampireHelper.setBlood(entity, VampireHelper.getBloodDouble(entity) - 1.0D);
    }

    public static long getTimeDrinking(EntityLivingBase entity) {
        VampireHelper.validateIsVampire(entity);
        return TransformationHelper.getTransformationData(entity).getLong("timeDrinking");
    }

    /**
     * Sets the time since which the given vampire is drinking blood.
     * No packet is being sent to the client
     */
    public static void setTimeDrinking(EntityLivingBase entity, long blood) {
        VampireHelper.validateIsVampire(entity);
        NBTTagCompound compound = TransformationHelper.getTransformationData(entity);
        compound.setLong("timeDrinking", blood);
    }
}
