package theblockbox.huntersdream.items.gun;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.interfaces.IAmmunition;
import theblockbox.huntersdream.api.interfaces.IGun;
import theblockbox.huntersdream.entity.EntityBullet;
import theblockbox.huntersdream.util.handlers.TransformationClientEventHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class ItemGun extends ItemBow implements IGun {
    protected final double damage;
    protected final int reloadCooldown;

    public ItemGun(double damage, int ticksReloadCooldown) {
        this.damage = damage;
        this.reloadCooldown = ticksReloadCooldown;
        this.addPropertyOverride(new ResourceLocation("is_loaded"), ((stack, worldIn, entityIn) ->
                BooleanUtils.toInteger((entityIn != null) && this.isLoaded(stack))));
        this.setFull3D();
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (this.isLoaded(stack)) {
            // if the gun has already been loaded
            if (this.canShoot(playerIn, stack)) {
                // and it is allowed to fire, shoot the bullet
                playerIn.setActiveHand(handIn);
                GeneralHelper.getTagCompoundFromItemStack(stack).setLong("huntersdream:last_shot", worldIn.getTotalWorldTime());
                this.shoot(playerIn, stack);
                playerIn.resetActiveHand();
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        } else if ((this.reloadCooldown + this.getTimeLastShot(stack))
                <= worldIn.getTotalWorldTime() && this.hasSufficientAmmunition(playerIn, stack)) {
            // set active hand to make #onUsingTick(ItemStack, EntityLivingBase, int) get called
            playerIn.setActiveHand(handIn);
            // play sound
            this.playReloadSoundStart(playerIn, stack);
            // don't play reequip animation
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        // don't play reequip animation
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            // if the gun has been used for long enough, play a sound to indicate that the gun is ready to reload/that it'll
            // reload when stopped being used
            if (!this.isLoaded(stack) && this.hasSufficientAmmunition(player, stack) && this.hasJustBeenReloaded(player, stack, count)
                    && ((this.reloadCooldown + this.getTimeLastShot(stack))
                    <= entity.world.getTotalWorldTime())) {
                this.playReloadSoundEnd(entity, stack);
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entity, int timeLeft) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!this.isLoaded(stack)) {
                if (this.canReload(player, stack, timeLeft) && this.hasSufficientAmmunition(player, stack)
                        && ((this.reloadCooldown + this.getTimeLastShot(stack))
                        <= worldIn.getTotalWorldTime())) {
                    // if the gun isn't loaded but has enough ammo, reload it
                    this.reload(player, stack);
                } else {
                    this.onReloadCanceled(entity, stack);
                }
            }
        }
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public void spawnBullet(EntityLivingBase entity, ItemStack stack) {
        World world = entity.world;
        Item ammunition = Item.getByNameOrId(GeneralHelper.getTagCompoundFromItemStack(stack).getString("huntersdream:ammunition"));
        if ((ammunition == Items.AIR) || (ammunition == null)) {
            ammunition = this.getDefaultAmmunition();
        }
        EntityBullet bullet = new EntityBullet(world, entity, ammunition, this.damage);
        bullet.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F,
                this.getArrowVelocity(entity, stack) * 3.0F, this.getInaccuracy());
        world.spawnEntity(bullet);
    }

    @Override
    public boolean shouldRenderDifferently(EntityLivingBase entity, ItemStack gun) {
        // when the gun is loaded, render it in both hands
        return this.isLoaded(gun);
    }

    @Override
    public Item setAmmunition(ItemStack stack, ItemStack ammunition, boolean infiniteAmmunition) {
        Item ammunitionItem = ammunition.getItem();
        Item actualAmmunition = this.getDefaultAmmunition();
        if (ammunitionItem instanceof IAmmunition) {
            for (IAmmunition.AmmunitionType ammunitionType : ((IAmmunition) ammunitionItem).getAmmunitionTypes()) {
                if (ArrayUtils.contains(this.getAllowedAmmunitionTypes(), ammunitionType)) {
                    actualAmmunition = ammunitionItem;
                    break;
                }
            }
        }
        GeneralHelper.getTagCompoundFromItemStack(stack).setString("huntersdream:ammunition", Objects.toString(actualAmmunition.getRegistryName()));
        return actualAmmunition;
    }

    @Override
    public boolean isLoaded(ItemStack stack) {
        return GeneralHelper.getTagCompoundFromItemStack(stack).getBoolean("huntersdream:loaded");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getReticle(EntityLivingBase entity, ItemStack stack) {
        return this.isLoaded(stack) ? TransformationClientEventHandler.reticleNormal : TransformationClientEventHandler.reticleReload;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("item.huntersdream.gun." + (this.isLoaded(stack) ? "loaded" : "unloaded") + ".tooltip"));
    }

    @Override
    public EntityArrow customizeArrow(EntityArrow arrow) {
        if (arrow.shootingEntity instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) arrow.shootingEntity;
            if (!(entity instanceof EntityPlayer)) {
                this.shoot(entity, ItemStack.EMPTY);
                return new EntityTippedArrow(arrow.world) {
                    @Override
                    public void shoot(double p_shoot_1_, double p_shoot_3_, double p_shoot_3_2, float p_shoot_5_, float p_shoot_5_2) {
                    }
                    @Override
                    public void shoot(Entity p_shoot_1_, float p_shoot_2_, float p_shoot_3_, float p_shoot_4_, float p_shoot_5_, float p_shoot_6_) {
                    }
                };
            }
        }
        return arrow;
    }

    /**
     * Gets called when the given entity stops reloading this weapon. (Does NOT get called when the reload is successful
     * but only when it isn't.) Can be used to e.g. cancel the sound played in {@link #playReloadSoundStart(EntityLivingBase, ItemStack)}.
     */
    public void onReloadCanceled(EntityLivingBase entity, ItemStack stack) {
    }

    /**
     * Is called to play the shoot sound when ammunition is shot.
     */
    public void playShootSound(EntityLivingBase entity, ItemStack stack) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0F, 1.0F / ((ItemGun.itemRand.nextFloat() * 0.4F) + 1.2F) + (this.getArrowVelocity(entity, stack) * 0.5F));
    }

    /**
     * Is called to play the reload sound when the entity starts reloading. (Therefore, it's not guaranteed that it'll
     * fully reload.)
     */
    public void playReloadSoundStart(EntityLivingBase entity, ItemStack stack) {
    }

    /**
     * Is called to play the reload sound when the entity has stopped reloading and now has a loaded weapon. (Doesn't get
     * called when the reloading wasn't successful.)
     */
    public void playReloadSoundEnd(EntityLivingBase entity, ItemStack stack) {
        entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON,
                SoundCategory.PLAYERS, 3.0F, 0.6F);
    }

    /**
     * Returns true if the entity is allowed to shoot the next bullet and gets called to decide whether to shoot the
     * bullet when the gun is loaded. Therefore, the returned boolean does not depend on whether the gun is loaded or not.<br>
     * The standard implementation always returns true and should be overridden for e.g. adding a cooldown between multiple
     * shots.
     */
    public boolean canShoot(EntityLivingBase entity, ItemStack stack) {
        return true;
    }

    /**
     * Sets the given gun to either loaded or unloaded, depending on the boolean.
     */
    public void setLoaded(ItemStack stack, boolean loaded) {
        GeneralHelper.getTagCompoundFromItemStack(stack).setBoolean("huntersdream:loaded", loaded);
    }

    /**
     * Returns the long that was retrieved from {@link World#getTotalWorldTime()} when the last shot was fired.
     */
    public long getTimeLastShot(ItemStack stack) {
        return GeneralHelper.getTagCompoundFromItemStack(stack).getLong("huntersdream:last_shot");
    }

    /**
     * Unloads and damages the weapon, shoots a bullet and plays the shoot sound.
     */
    public void shoot(EntityLivingBase entity, ItemStack stack) {
        this.setLoaded(stack, false);
        stack.damageItem(1, entity);
        World world = entity.world;
        if (!world.isRemote) {
            this.spawnBullet(entity, stack);
        }
        this.playShootSound(entity, stack);
        if (entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).addStat(StatList.getObjectUseStats(this));
        }
    }

    public float getArrowVelocity(EntityLivingBase entity, ItemStack stack) {
        return 1.0F;
    }

    /**
     * Returns the time in ticks this weapon needs to reload. For reference, a bow needs 20 ticks (= 1 second) in order
     * to be fully charged.
     */
    public float getTimeForReload(EntityPlayer player, ItemStack stack) {
        return 20.0F;
    }

    /**
     * Returns true if the player has successfully reloaded, meaning that the time they've been reloading was either
     * the same or higher than the cooldown of their weapon.
     */
    public boolean canReload(EntityPlayer player, ItemStack stack, int timeLeft) {
        return (this.getMaxItemUseDuration(stack) - timeLeft) >= this.getTimeForReload(player, stack);
    }

    /**
     * Returns true when this gun has just been reloaded (in this tick). Currently only used to test if the reload sound
     * should be played.
     */
    public boolean hasJustBeenReloaded(EntityPlayer player, ItemStack stack, int timeLeft) {
        return (this.getMaxItemUseDuration(stack) - timeLeft) == this.getTimeForReload(player, stack);
    }

    /**
     * Reloads the gun by removing ammunition and, if the former succeeded, setting "loaded" to true.
     */
    public void reload(EntityPlayer player, ItemStack stack) {
        if (this.removeAmmunition(player, stack)) {
            this.setLoaded(stack, true);
        }
    }

    /**
     * Returns true if the player either is in creative mode or has fitting ammunition in their inventory.
     * This is used to determine whether a player has sufficient ammunition to reload or not.
     */
    public boolean hasSufficientAmmunition(EntityPlayer player, ItemStack stack) {
        return player.capabilities.isCreativeMode || !GeneralHelper.getAmmunitionStackForWeapon(player, stack, false, null).isEmpty();
    }

    /**
     * Tries to remove ammunition from the given player holding the given gun item stack.
     * If this succeeds, true will be returned, otherwise, if nothing happened, false will be returned.
     */
    public boolean removeAmmunition(EntityPlayer player, ItemStack stack) {
        if (this.hasSufficientAmmunition(player, stack)) {
            ItemStack ammunition = GeneralHelper.getAmmunitionStackForWeapon(player, stack, false, null);
            if (ammunition.getItem().getRegistryName() != null) {
                this.setAmmunition(stack, ammunition, player.capabilities.isCreativeMode);
            }
            if (!player.capabilities.isCreativeMode) {
                ammunition.shrink(1);
                if (ammunition.isEmpty()) {
                    player.inventory.deleteStack(ammunition);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
