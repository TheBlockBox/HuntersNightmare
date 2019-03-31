package theblockbox.huntersdream.api.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.init.CapabilitiesInit;
import theblockbox.huntersdream.api.init.PotionInit;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.exceptions.WrongTransformationException;
import theblockbox.huntersdream.util.handlers.PacketHandler;
import theblockbox.huntersdream.util.interfaces.transformation.IVampirePlayer;

import javax.annotation.Nonnull;

public class VampireHelper {
    public static final Capability<IVampirePlayer> CAPABILITY_VAMPIRE = CapabilitiesInit.CAPABILITY_VAMPIRE;

    public static void drinkBlood(EntityLivingBase vampire, EntityLivingBase drinkFrom) {
        VampireHelper.validateIsVampire(vampire);

        if (drinkFrom.isEntityUndead()) {
            vampire.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 60, 2));
        }
        if (vampire instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) vampire;
            IVampirePlayer vamp = VampireHelper.getIVampire(player);
            if (vamp.getBlood() < 20) {
                vamp.incrementBlood();
            }
            PacketHandler.sendBloodMessage(player);
        }
        drinkFrom.attackEntityFrom(new DamageSource("vampireDrankBlood"), 1.0F);
    }

    public static IVampirePlayer getIVampire(@Nonnull EntityPlayer player) {
        Validate.notNull(player);
        return player.getCapability(VampireHelper.CAPABILITY_VAMPIRE, null);
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
}
