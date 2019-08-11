package theblockbox.huntersdream.items;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.interfaces.IAmmunition;
import theblockbox.huntersdream.entity.EntityBullet;

import java.util.function.Supplier;

public class ItemShotgun extends ItemPercussionGun {
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite reticleShotgun = null;

    public ItemShotgun(double damage, int durability, int ticksShotCooldown, Supplier<Item> defaultAmmunition, int maximumAmmunitionStorage) {
        super(damage, durability, ticksShotCooldown, defaultAmmunition, maximumAmmunitionStorage, 1.5F, IAmmunition.AmmunitionType.SHOTGUN_SHELL);
    }

    @Override
    public void spawnBullet(EntityLivingBase player, ItemStack stack) {
        World world = player.world;
        Item ammunition = Item.getByNameOrId(GeneralHelper.getTagCompoundFromItemStack(stack).getString("huntersdream:ammunition"));
        if (ammunition == Items.AIR) {
            ammunition = this.getDefaultAmmunition();
        }
        // shoot twelve bullets
        for (int i = 0; i < 12; i++) {
            EntityBullet bullet = new EntityBullet(world, player, ammunition, this.damage);
            float rotation = (Item.itemRand.nextFloat() - 0.5F) * 16;
            bullet.shoot(player, player.rotationPitch + rotation, player.rotationYaw + rotation, 0.0F,
                    this.getArrowVelocity(player, stack) * 3.0F, this.getInaccuracy());
            world.spawnEntity(bullet);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TextureAtlasSprite getReticle(EntityLivingBase entity, ItemStack stack) {
        return ItemShotgun.reticleShotgun;
    }
}
