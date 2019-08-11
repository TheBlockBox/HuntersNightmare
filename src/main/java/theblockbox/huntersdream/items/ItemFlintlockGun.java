package theblockbox.huntersdream.items;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.init.SoundInit;
import theblockbox.huntersdream.api.interfaces.IAmmunition;

import java.util.Collection;
import java.util.stream.Stream;

public class ItemFlintlockGun extends ItemGun {
    public static final IAmmunition.AmmunitionType[] AMMUNITION_TYPES = {IAmmunition.AmmunitionType.MUSKET_BALL};
    protected final int consumedAmmunition;
    protected final int consumedGunpowder;

    public ItemFlintlockGun(double damage, int durability, int consumedAmmunition, int consumedGunpowder) {
        super(damage, 0);
        this.setMaxDamage(durability);
        this.consumedAmmunition = consumedAmmunition;
        this.consumedGunpowder = consumedGunpowder;
    }

    @Override
    public IAmmunition.AmmunitionType[] getAllowedAmmunitionTypes() {
        return ItemFlintlockGun.AMMUNITION_TYPES;
    }

    @Override
    public Item getDefaultAmmunition() {
        return ItemInit.MUSKET_BALL;
    }

    @Override
    public float getInaccuracy() {
        return 1.5F;
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
    public boolean hasSufficientAmmunition(EntityPlayer player, ItemStack stack) {
        if (player.capabilities.isCreativeMode) {
            return true;
        } else {
            return (GeneralHelper.getAmmunitionStackForWeapon(player, stack, false, null).getCount() >= this.consumedAmmunition)
                    && (Stream.of(player.inventory.offHandInventory, player.inventory.mainInventory,
                    player.inventory.armorInventory).flatMap(Collection::stream).anyMatch(itemStack ->
                    (itemStack.getItem() == Items.GUNPOWDER) && (itemStack.getCount() >= this.consumedGunpowder)));
        }
    }

    @Override
    public boolean removeAmmunition(EntityPlayer player, ItemStack stack) {
        if (this.hasSufficientAmmunition(player, stack)) {
            ItemStack ammunition = GeneralHelper.getAmmunitionStackForWeapon(player, stack, false, null);
            if (ammunition.getItem().getRegistryName() != null) {
                this.setAmmunition(stack, ammunition, player.capabilities.isCreativeMode);
            }
            if (!player.capabilities.isCreativeMode) {
                ItemStack gunpowder = Stream.of(player.inventory.offHandInventory, player.inventory.mainInventory,
                        player.inventory.armorInventory).flatMap(Collection::stream).filter(itemStack ->
                        itemStack.getItem() == Items.GUNPOWDER).findFirst().get();
                ammunition.shrink(this.consumedAmmunition);
                gunpowder.shrink(this.consumedGunpowder);
                if (ammunition.isEmpty()) {
                    player.inventory.deleteStack(ammunition);
                }
                if (gunpowder.isEmpty()) {
                    player.inventory.deleteStack(gunpowder);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        // repair with itself or with iron
        return super.getIsRepairable(toRepair, repair) || ArrayUtils.contains(OreDictionary.getOreIDs(repair),
                OreDictionary.getOreID("ingotIron"));
    }

    @Override
    public void onReloadCanceled(EntityLivingBase entity, ItemStack stack) {
        if (entity.world.isRemote) {
            Minecraft.getMinecraft().getSoundHandler().stop(SoundInit.FLINTLOCK_RELOAD.getSoundName().toString(), SoundCategory.PLAYERS);
        }
    }

    @Override
    public void playShootSound(EntityLivingBase entity, ItemStack stack) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundInit.FLINTLOCK_FIRE, SoundCategory.PLAYERS,
                2.0F, 1.0F);
    }

    @Override
    public void playReloadSoundStart(EntityLivingBase entity, ItemStack stack) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundInit.FLINTLOCK_RELOAD, SoundCategory.PLAYERS,
                2.0F, 1.0F);
    }

    @Override
    public void playReloadSoundEnd(EntityLivingBase entity, ItemStack stack) {
        // don't play any sound when reloading ends
    }
}
