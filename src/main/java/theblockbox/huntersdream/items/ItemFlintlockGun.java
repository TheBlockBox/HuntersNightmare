package theblockbox.huntersdream.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.interfaces.IAmmunition;

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
    public float getInaccuracy() {
        return 1.5F;
    }

    @Override
    public float getArrowVelocity(EntityPlayer player, ItemStack stack) {
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
            return (GeneralHelper.getAmmunitionStackForWeapon(player, stack, false).getCount() >= this.consumedAmmunition)
                    && (Stream.of(player.inventory.offHandInventory, player.inventory.mainInventory,
                    player.inventory.armorInventory).flatMap(Collection::stream).anyMatch(itemStack ->
                    (itemStack.getItem() == Items.GUNPOWDER) && (itemStack.getCount() >= this.consumedGunpowder)));
        }
    }

    @Override
    public boolean removeAmmunition(EntityPlayer player, ItemStack stack) {
        if (this.hasSufficientAmmunition(player, stack)) {
            ItemStack ammunition = GeneralHelper.getAmmunitionStackForWeapon(player, stack, false);
            ResourceLocation registryName = ammunition.getItem().getRegistryName();
            if (registryName != null) {
                GeneralHelper.getTagCompoundFromItemStack(stack).setString("huntersdream:ammunition", registryName.toString());
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
        return super.getIsRepairable(toRepair, repair) || ArrayUtils.contains(OreDictionary.getOreIDs(repair),
                OreDictionary.getOreID("ingotIron"));
    }
}
