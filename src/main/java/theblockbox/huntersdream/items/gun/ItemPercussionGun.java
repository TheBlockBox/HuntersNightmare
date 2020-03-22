package theblockbox.huntersdream.items.gun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.SoundInit;
import theblockbox.huntersdream.api.interfaces.IAmmunition;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ItemPercussionGun extends ItemGun {
    private final IAmmunition.AmmunitionType[] ammunitionTypes;
    private final Supplier<Item> defaultAmmunition;
    private final int maximumAmmunitionStorage;
    private final int ticksShotCooldown;
    private final float inaccuracy;
    protected SoundEvent fireSound = SoundInit.REVOLVER_FIRE;
    protected SoundEvent reloadSound = SoundInit.REVOLVER_RELOAD;

    public ItemPercussionGun(double damage, int durability, int ticksShotCooldown, Supplier<Item> defaultAmmunition,
                             int maximumAmmunitionStorage, float inaccuracy, IAmmunition.AmmunitionType... ammunitionTypes) {
        super(damage, 0);
        this.setMaxDamage(durability);
        this.ticksShotCooldown = ticksShotCooldown;
        this.defaultAmmunition = defaultAmmunition;
        this.maximumAmmunitionStorage = maximumAmmunitionStorage;
        this.inaccuracy = inaccuracy;
        this.ammunitionTypes = ammunitionTypes;
    }

    @Override
    public IAmmunition.AmmunitionType[] getAllowedAmmunitionTypes() {
        return this.ammunitionTypes;
    }

    @Override
    public Item getDefaultAmmunition() {
        return this.defaultAmmunition.get();
    }

    @Override
    public float getInaccuracy() {
        return this.inaccuracy;
    }

    @Override
    public float getArrowVelocity(EntityLivingBase entity, ItemStack stack) {
        // twice as fast as normal arrows
        return 2.0F;
    }

    @Override
    public float getTimeForReload(EntityPlayer player, ItemStack stack) {
        // 40 ticks = 2 seconds; twice as long as full charged bow
        return 40.0F;
    }

    @Override
    public boolean canShoot(EntityLivingBase entity, ItemStack stack) {
        return (entity.world.getTotalWorldTime() - this.getTimeLastShot(stack)) >= this.ticksShotCooldown;
    }

    @Override
    public boolean isLoaded(ItemStack stack) {
        return this.getStoredAmmunition(stack) > 0;
    }

    @Override
    public void setLoaded(ItemStack stack, boolean loaded) {
        if (!loaded) {
            this.setStoredAmmunition(stack, Math.max(0, this.getStoredAmmunition(stack) - 1));
        }
    }

    @Override
    public Item setAmmunition(ItemStack stack, ItemStack ammunition, boolean infiniteAmmunition) {
        // change how many ammunition will be loaded
        this.setStoredAmmunition(stack, infiniteAmmunition ? this.maximumAmmunitionStorage : Math.min(this.maximumAmmunitionStorage,
                this.getStoredAmmunition(stack) + ammunition.getCount()));
        return super.setAmmunition(stack, ammunition, infiniteAmmunition);
    }

    @Override
    public boolean removeAmmunition(EntityPlayer player, ItemStack stack) {
        // if this has enough ammuntion,
        if (this.hasSufficientAmmunition(player, stack)) {
            // introduce new variables
            int maxAmmunition = this.maximumAmmunitionStorage;
            Item ammunitionType = Items.AIR; // the type of ammunition that the gun will shoot
            boolean hasFoundAmmunition = false;
            // do-while loop as long as the maximum number of ammunition hasn't been reached, the player isn't in creative
            // mode and ammunition has been found in the last loop
            do {
                // get found ammunition (if ammunition has been found before, test if the item of the new found one
                // equals the item of the currently found one)
                final Item ammunitionTypeCopy = ammunitionType;
                ItemStack foundAmmunition = GeneralHelper.getAmmunitionStackForWeapon(player, stack, false,
                        s -> (ammunitionTypeCopy == Items.AIR) || (ammunitionTypeCopy == s.getItem()));
                hasFoundAmmunition = !foundAmmunition.isEmpty();

                // if no ammunition has been found before,
                if ((ammunitionType == Items.AIR) || (ammunitionType == null)) {
                    // make the ammunition type equal to the item of the found ammunition
                    ammunitionType = foundAmmunition.getItem();
                }

                // add the found ammunition
                if (foundAmmunition.getItem().getRegistryName() != null) {
                    this.setAmmunition(stack, foundAmmunition, player.capabilities.isCreativeMode);
                }

                // if the player isn't in creative mode,
                if (!player.capabilities.isCreativeMode) {
                    // change the stack size of the found ammunition
                    int ammunitionSize = foundAmmunition.getCount();
                    ammunitionSize = Math.min(ammunitionSize, maxAmmunition);
                    maxAmmunition -= ammunitionSize;
                    foundAmmunition.shrink(ammunitionSize);
                    if (foundAmmunition.isEmpty()) {
                        player.inventory.deleteStack(foundAmmunition);
                    }
                }
            } while ((maxAmmunition > 0) && !player.capabilities.isCreativeMode && hasFoundAmmunition);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onReloadCanceled(EntityLivingBase entity, ItemStack stack) {
        if (entity.world.isRemote) {
            Minecraft.getMinecraft().getSoundHandler().stop(this.reloadSound.getSoundName().toString(), SoundCategory.PLAYERS);
        }
    }

    @Override
    public void playShootSound(EntityLivingBase entity, ItemStack stack) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, this.fireSound, SoundCategory.PLAYERS,
                2.0F, 1.0F);
    }

    @Override
    public void playReloadSoundStart(EntityLivingBase entity, ItemStack stack) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, this.reloadSound, SoundCategory.PLAYERS,
                2.0F, 1.0F);
    }

    @Override
    public void playReloadSoundEnd(EntityLivingBase entity, ItemStack stack) {
        // don't play any sound when reloading ends
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        // repair with itself or with iron
        return super.getIsRepairable(toRepair, repair) || ArrayUtils.contains(OreDictionary.getOreIDs(repair),
                OreDictionary.getOreID("ingotIron"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.huntersdream.gun." + (this.isLoaded(stack) ? "loaded" : "unloaded") + "_and_ammunition.tooltip",
                this.getStoredAmmunition(stack), this.maximumAmmunitionStorage));
    }

    public int getStoredAmmunition(ItemStack stack) {
        return GeneralHelper.getTagCompoundFromItemStack(stack).getInteger("huntersdream:percussion_gun_stored_ammunition");
    }

    public void setStoredAmmunition(ItemStack stack, int storedAmmunition) {
        GeneralHelper.getTagCompoundFromItemStack(stack).setInteger("huntersdream:percussion_gun_stored_ammunition", storedAmmunition);
    }
}
