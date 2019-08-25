package theblockbox.huntersdream.items.gun;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.entity.EntityBullet;

public class ItemFlintlockGunBlunderBuss extends ItemFlintlockGun {
    public ItemFlintlockGunBlunderBuss(double damage, int durability, int consumedAmmunition, int consumedGunpowder) {
        super(damage, durability, consumedAmmunition, consumedGunpowder);
    }

    @Override
    public void spawnBullet(EntityLivingBase player, ItemStack stack) {
        World world = player.world;
        Item ammunition = Item.getByNameOrId(GeneralHelper.getTagCompoundFromItemStack(stack).getString("huntersdream:ammunition"));
        if (ammunition == Items.AIR) {
            ammunition = this.getDefaultAmmunition();
        }
        // shoot as many bullets as you consume ammunition
        for (int i = 0; i < this.consumedAmmunition; i++) {
            EntityBullet bullet = new EntityBullet(world, player, ammunition, this.damage);
            float rotation = (Item.itemRand.nextFloat() - 0.5F) * 25;
            bullet.shoot(player, player.rotationPitch + rotation, player.rotationYaw + rotation, 0.0F,
                    this.getArrowVelocity(player, stack) * 3.0F, this.getInaccuracy());
            world.spawnEntity(bullet);
        }
    }
}
