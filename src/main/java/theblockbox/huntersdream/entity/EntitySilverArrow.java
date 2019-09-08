package theblockbox.huntersdream.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.init.ItemInit;

public class EntitySilverArrow extends EntityArrow {

    public EntitySilverArrow(World worldIn) {
        super(worldIn);
    }

    public EntitySilverArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    public EntitySilverArrow(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ItemInit.SILVER_ARROW);
    }
}
