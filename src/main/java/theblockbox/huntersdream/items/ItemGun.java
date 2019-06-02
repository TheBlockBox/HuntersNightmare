package theblockbox.huntersdream.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.interfaces.IAmmunition;
import theblockbox.huntersdream.api.interfaces.IGun;
import theblockbox.huntersdream.entity.EntityBullet;

import java.util.Objects;

// TODO: Add damage tooltip
public abstract class ItemGun extends ItemBow implements IGun {
    protected final double damage;
    protected final int cooldown;

    public ItemGun(double damage, int ticksCooldown) {
        this.damage = damage;
        this.cooldown = ticksCooldown;
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
            // if the gun has already been loaded shoot the bullet
            playerIn.setActiveHand(handIn);
            GeneralHelper.getTagCompoundFromItemStack(stack).setLong("huntersdream:last_shot", worldIn.getTotalWorldTime());
            this.shoot(playerIn, stack);
            playerIn.resetActiveHand();
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else if ((this.cooldown + GeneralHelper.getTagCompoundFromItemStack(stack).getLong("huntersdream:last_shot"))
                <= worldIn.getTotalWorldTime() && this.hasSufficientAmmunition(playerIn, stack)) {
            // set active hand to make #onUsingTick(ItemStack, EntityLivingBase, int) get called
            playerIn.setActiveHand(handIn);
            // play sound
            this.playReloadSoundStart(playerIn, stack);
            // don't play reequip animation
            return new ActionResult<>(EnumActionResult.PASS, stack);
        } else {
            // don't play reequip animation
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            // if the gun has been used for long enough, play a sound to indicate that the gun is ready to reload/that it'll
            // reload when stopped being used
            if (!this.isLoaded(stack) && this.hasSufficientAmmunition(player, stack) && this.hasJustBeenReloaded(player, stack, count)
                    && ((this.cooldown + GeneralHelper.getTagCompoundFromItemStack(stack).getLong("huntersdream:last_shot"))
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
                        && ((this.cooldown + GeneralHelper.getTagCompoundFromItemStack(stack).getLong("huntersdream:last_shot"))
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
        if (ammunition == Items.AIR) {
            ammunition = ItemInit.MUSKET_BALL;
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
    public Item setAmmunition(ItemStack stack, Item ammunition) {
        Item actualAmmunition = this.getDefaultAmmunition();
        if (ammunition instanceof IAmmunition) {
            for (IAmmunition.AmmunitionType ammunitionType : ((IAmmunition) ammunition).getAmmunitionTypes()) {
                if (ArrayUtils.contains(this.getAllowedAmmunitionTypes(), ammunitionType)) {
                    actualAmmunition = ammunition;
                    break;
                }
            }
        }
        GeneralHelper.getTagCompoundFromItemStack(stack).setString("huntersdream:ammunition", Objects.toString(actualAmmunition.getRegistryName()));
        return actualAmmunition;
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

    public boolean isLoaded(ItemStack stack) {
        return GeneralHelper.getTagCompoundFromItemStack(stack).getBoolean("huntersdream:loaded");
    }

    public void shoot(EntityPlayer player, ItemStack stack) {
        GeneralHelper.getTagCompoundFromItemStack(stack).setBoolean("huntersdream:loaded", false);
        stack.damageItem(1, player);
        World world = player.world;
        if (!world.isRemote) {
            this.spawnBullet(player, stack);
        }
        this.playShootSound(player, stack);
        player.addStat(StatList.getObjectUseStats(this));
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

    public void reload(EntityPlayer player, ItemStack stack) {
        this.removeAmmunition(player, stack);
        GeneralHelper.getTagCompoundFromItemStack(stack).setBoolean("huntersdream:loaded", true);
    }

    public boolean hasSufficientAmmunition(EntityPlayer player, ItemStack stack) {
        return player.capabilities.isCreativeMode || !GeneralHelper.getAmmunitionStackForWeapon(player, stack, false).isEmpty();
    }

    public boolean removeAmmunition(EntityPlayer player, ItemStack stack) {
        if (this.hasSufficientAmmunition(player, stack)) {
            ItemStack ammunition = GeneralHelper.getAmmunitionStackForWeapon(player, stack, false);
            if (ammunition.getItem().getRegistryName() != null) {
                this.setAmmunition(stack, ammunition.getItem());
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
