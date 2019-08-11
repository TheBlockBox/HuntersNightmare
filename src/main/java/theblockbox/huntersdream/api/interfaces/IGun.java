package theblockbox.huntersdream.api.interfaces;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

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
     * If infiniteAmmunition is set to true, more ammunition may be added that the player actually has. (Like bows not
     * consuming arrows when used in creative.) This method does NOT decrease the stack size of the given ammunition.
     */
    public Item setAmmunition(ItemStack stack, ItemStack ammunition, boolean infiniteAmmunition);

    /**
     * Returns true if the given gun is loaded by checking the item stack nbt.
     */
    public boolean isLoaded(ItemStack stack);

    /**
     * Returns the reticle as a 16x16 {@link TextureAtlasSprite} that should replace the vanilla crosshairs.
     * If they shouldn't be replaced, null should be returned here. You may also draw your own overlays or different
     * reticle here, though then, an empty texture (like {@link theblockbox.huntersdream.util.handlers.TransformationClientEventHandler#transparent16x16Texture})
     * should be returned so that the normal crosshairs won't show up.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getReticle(EntityLivingBase entity, ItemStack stack);
}
