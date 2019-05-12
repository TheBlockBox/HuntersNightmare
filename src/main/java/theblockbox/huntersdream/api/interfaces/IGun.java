package theblockbox.huntersdream.api.interfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IGun {
    /**
     * Returns the types of ammunition this gun accepts.
     */
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
    public boolean shouldRenderDifferently(EntityLivingBase entity, ItemStack gun);

    /**
     * Is called when a bullet should be spawned and shot by the given entity holding the given item stack.
     * (The item of the item stack should always equals the item this method is called on.)
     */
    public void spawnBullet(EntityLivingBase entity, ItemStack stack);

    public Item getDefaultAmmunition();

    /**
     * Sets the given item stack's ammunition to the given one so that {@link #spawnBullet(EntityLivingBase, ItemStack)}
     * shoots a bullet with that ammunition. If this succeedes, the passed ammunition will be returned. If the given
     * ammunition isn't allowed, the method will revert to the default ammunition gotten from {@link #getDefaultAmmunition()}
     * which will then be set and returned. The given ammunition should always be an instance of {@link IAmmunition}.
     */
    public Item setAmmunition(ItemStack stack, Item ammunition);
}
