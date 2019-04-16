package theblockbox.huntersdream.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.helpers.VampireHelper;
import theblockbox.huntersdream.util.handlers.PacketHandler;

/**
 * A class for vampires extending {@link FoodStats} to change the way hunger
 * works for vampires
 */
public class VampireFoodStats extends FoodStats {
    public static final VampireFoodStats INSTANCE = new VampireFoodStats();

    private VampireFoodStats() {
    }

    /**
     * Tries to replace the given player's foodstats with a vampire one. Returns true when it was successful, meaning
     * that the logical side was server and the foodstats wasn't already set to the vampire foodstats.
     */
    public static boolean replaceFoodStats(EntityPlayer player) {
        if (!player.world.isRemote && (player.foodStats != VampireFoodStats.INSTANCE)) {
            player.foodStats = VampireFoodStats.INSTANCE;
            return true;
        }
        return false;
    }

    @Override
    public float getSaturationLevel() {
        return 0.0F;
    }

    @Override
    public int getFoodLevel() {
        // returning ten to make players move with normal
        return 20;
    }

    @Override
    public void onUpdate(EntityPlayer player) {
        if (player.ticksExisted % 2 == 0) {
            int blood = VampireHelper.getBlood(player);

            if (player.isPotionActive(MobEffects.HUNGER) && (blood != 0)) {
                double newBlood = VampireHelper.getBloodDouble(player)
                        - (0.04F * (player.getActivePotionEffect(MobEffects.HUNGER).getAmplifier() + 1.0F));

                if (newBlood <= 0) {
                    newBlood = 0;
                }

                VampireHelper.setBlood(player, newBlood);
            }

            if (player.ticksExisted % 78 == 0) {
                if (VampireHelper.getBloodDouble(player) >= 1 && player.shouldHeal()) {
                    VampireHelper.decrementBlood(player);
                    player.heal(1);
                } else if (blood <= 0 && player.getHealth() > 1.0F) {
                    player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                    // TODO: Add bloodlust effect here
                }

                if (player.ticksExisted % 156 == 0 && blood >= 1) {
                    VampireHelper.setBlood(player, VampireHelper.getBloodDouble(player) - 0.0416D);
                }
            }

            if (VampireHelper.getBlood(player) != blood)
                PacketHandler.sendTransformationMessage((EntityPlayerMP) player);
        }
    }

    @Override
    public void addStats(ItemFood foodItem, ItemStack stack) {
    }

    @Override
    public void addStats(int foodLevelIn, float foodSaturationModifier) {
    }

    @Override
    public void addExhaustion(float exhaustion) {
    }

    @Override
    public void setFoodLevel(int foodLevelIn) {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setFoodSaturationLevel(float foodSaturationLevelIn) {
    }

    @Override
    public boolean needFood() {
        return false;
    }

    @Override
    public void readNBT(NBTTagCompound compound) {
    }

    @Override
    public void writeNBT(NBTTagCompound compound) {
    }
}
