package theblockbox.huntersdream.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.entity.EntityBullet;

// TODO: Finish
public class ItemFlintlockGunBlunderBuss extends ItemFlintlockGun {
    public ItemFlintlockGunBlunderBuss(double damage, int durability, int consumedAmmunition, int consumedGunpowder) {
        super(damage, durability, consumedAmmunition, consumedGunpowder);
    }

    @Override
    public void spawnBullet(EntityPlayer player, ItemStack stack) {
        World world = player.world;
        Item ammunition = Item.getByNameOrId(GeneralHelper.getTagCompoundFromItemStack(stack).getString("huntersdream:ammunition"));
        if (ammunition == Items.AIR) {
            ammunition = ItemInit.MUSKET_BALL;
        }
        // shoot as many bullets as you consume ammunition
        // TODO: Make them go into different directions
        for (int i = 0; i < this.consumedAmmunition; i++) {
            EntityBullet bullet = new EntityBullet(world, player, ammunition, this.damage);
            float rotation = (Item.itemRand.nextFloat() - 0.5F) * 90;
            bullet.shoot(player, player.rotationPitch + rotation, player.rotationYaw + rotation, 0.0F,
                    this.getArrowVelocity(player, stack) * 3.0F, this.getInaccuracy());
            world.spawnEntity(bullet);
        }
    }
}
