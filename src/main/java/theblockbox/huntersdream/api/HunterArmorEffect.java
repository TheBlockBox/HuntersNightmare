package theblockbox.huntersdream.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;
import theblockbox.huntersdream.items.ItemHunterArmor;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents effects that can be applied to items that are instances of {@link ItemHunterArmor}.
 */
public class HunterArmorEffect {
    public static final HunterArmorEffect NONE = new HunterArmorEffect(Items.AIR);
    private static final Map<Item, HunterArmorEffect> EFFECTS = new HashMap<>();
    private final Item effectItem;

    public HunterArmorEffect(Item effectItem) {
        this.effectItem = effectItem;
        if ((effectItem != null) && (effectItem != Items.AIR)) {
            HunterArmorEffect.EFFECTS.put(effectItem, this);
        }
    }

    /**
     * Returns an instance of {@link HunterArmorEffect} for the given item. If none could be found, {@link #NONE} will
     * be returned.
     */
    public static HunterArmorEffect getEffectFromItem(Item item) {
        return ObjectUtils.defaultIfNull(HunterArmorEffect.EFFECTS.get(item), HunterArmorEffect.NONE);
    }

    /**
     * Returns the item associated with this effect.
     */
    public Item getEffectItem() {
        return this.effectItem;
    }

    /**
     * Gets called every
     */
    public void onTick(EntityLivingBase entity, ItemStack armor) {
    }
}
