package theblockbox.huntersdream.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.apache.commons.lang3.ObjectUtils;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.items.ItemHunterArmor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents effects that can be applied to items that are instances of {@link ItemHunterArmor}.
 */
// TODO: Make this work with ore dict names
public class HunterArmorEffect {
    private static final Map<Item, HunterArmorEffect> EFFECTS = new HashMap<>();
    public static final HunterArmorEffect NONE = new HunterArmorEffect(Items.AIR);
    // TODO: Create effect
    public static final HunterArmorEffect SILVER = new HunterArmorEffect(ItemInit.SILVER_INGOT);
    private final Item effectItem;

    public HunterArmorEffect(Item effectItem) {
        this.effectItem = effectItem;
        if ((effectItem != null) && (effectItem != Items.AIR)) {
            HunterArmorEffect oldEffect = HunterArmorEffect.EFFECTS.get(effectItem);
            if (oldEffect == null) {
                HunterArmorEffect.EFFECTS.put(effectItem, this);
            } else {
                Main.getLogger().error("Tried to create new HunterArmorEffect with the effect item " + effectItem +
                        " but couldn't as the HunterArmorEffect " + oldEffect + " had already been registered with that item.");
            }
        }
    }

    /**
     * Returns set of all items that are associated with a specific HunterArmorEffect. (Except for {@link #NONE}.)
     * The set will be updated when new effects are added, but can't add or remove any by itself.
     */
    public static Set<Item> getAllItems() {
        return Collections.unmodifiableSet(HunterArmorEffect.EFFECTS.keySet());
    }

    /**
     * Returns an instance of {@link HunterArmorEffect} for the given item. If none could be found, {@link #NONE} will
     * be returned.
     */
    public static HunterArmorEffect getEffectFromItem(Item item) {
        return ObjectUtils.defaultIfNull(HunterArmorEffect.EFFECTS.get(item), HunterArmorEffect.NONE);
    }

    /**
     * Returns the item associated with this effect. (This item is unique for every effect and can only
     */
    public Item getEffectItem() {
        return this.effectItem;
    }

    /**
     * Gets called every 101 ticks to apply the effect to the given entity on client and server. This won't be called at
     * all when the entity doesn't have the full armor set or only once when they have two armor parts with this effect,
     * so that there wouldn't be any difference between having the effect on one or two parts except this method would
     * test for that specific case.
     */
    public void onTick(EntityLivingBase entity) {
    }

    @Override
    public String toString() {
        return "HunterArmorEffect#" + this.effectItem;
    }
}
