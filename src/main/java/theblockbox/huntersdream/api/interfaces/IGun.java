package theblockbox.huntersdream.api.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IGun {
    // TODO: Write java doc for this
    public IAmmunition.AmmunitionType[] getAllowedAmmunitionTypes();

    /**
     * Returns how inaccurate this gun is. Vanilla arrows, potions, ender pearls, snowballs and eggs use 1.0,
     * droppers 6.0, witches 8.0, llamas 10.0 and snowmen 12.0.
     */
    public float getInaccuracy();

    /**
     * When this method returns true, the given gun will be rendered differently than normal, depending on what
     * {@link net.minecraft.item.Item#getItemUseAction(ItemStack)} returns.
     */
    public boolean shouldRenderDifferently(EntityPlayer entity, ItemStack gun);
}
