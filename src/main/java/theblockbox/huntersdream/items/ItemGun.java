package theblockbox.huntersdream.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.entity.EntityBullet;
import theblockbox.huntersdream.util.interfaces.IGun;

// TODO: Add damage tooltip
public abstract class ItemGun extends ItemBow implements IGun {
    protected final double damage;
    protected final int cooldown;

    public ItemGun(double damage, int ticksCooldown) {
        this.damage = damage;
        this.cooldown = ticksCooldown;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        // TODO: What does this method even do?
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (this.isLoaded(stack)) {
            // if the gun has already been loaded shoot the bullet
            playerIn.setActiveHand(handIn);
            GeneralHelper.getTagCompoundFromItemStack(stack).setLong("huntersdream:last_shot", worldIn.getTotalWorldTime());
            this.shoot(playerIn, stack);
            playerIn.resetActiveHand();
            return new ActionResult<>(EnumActionResult.PASS, stack);
        } else if (this.hasSufficientAmmunition(playerIn, stack)) {
            if ((this.cooldown + GeneralHelper.getTagCompoundFromItemStack(stack).getLong("huntersdream:last_shot"))
                    <= worldIn.getTotalWorldTime()) {
                // set active hand to make #onUsingTick(ItemStack, EntityLivingBase, int) get called
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            } else {
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
        } else {
            return new ActionResult<>(EnumActionResult.PASS, stack);
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
                player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON,
                        SoundCategory.PLAYERS, 1.0F, 0.6F);
            }
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entity, int timeLeft) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!this.isLoaded(stack) && this.canReload(player, stack, timeLeft) && this.hasSufficientAmmunition(player, stack)
                    && ((this.cooldown + GeneralHelper.getTagCompoundFromItemStack(stack).getLong("huntersdream:last_shot"))
                    <= worldIn.getTotalWorldTime())) {
                // if the isn't loaded but has enough ammo, reload it
                this.reload(player, stack);
            }
        }
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
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                1.0F, 1.0F / ((ItemGun.itemRand.nextFloat() * 0.4F) + 1.2F) + (this.getArrowVelocity(player, stack) * 0.5F));
        player.addStat(StatList.getObjectUseStats(this));
    }

    public void spawnBullet(EntityPlayer player, ItemStack stack) {
        World world = player.world;
        Item ammunition = Item.getByNameOrId(GeneralHelper.getTagCompoundFromItemStack(stack).getString("huntersdream:ammunition"));
        if (ammunition == Items.AIR) {
            ammunition = ItemInit.MUSKET_BALL;
        }
        EntityBullet bullet = new EntityBullet(world, player, ammunition, this.damage);
        bullet.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F,
                this.getArrowVelocity(player, stack) * 3.0F, this.getInaccuracy());
        world.spawnEntity(bullet);
    }

    public float getArrowVelocity(EntityPlayer player, ItemStack stack) {
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
            ResourceLocation registryName = ammunition.getItem().getRegistryName();
            if (registryName != null) {
                GeneralHelper.getTagCompoundFromItemStack(stack).setString("huntersdream:ammunition", registryName.toString());
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
