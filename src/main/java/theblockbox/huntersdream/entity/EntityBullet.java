package theblockbox.huntersdream.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.init.SoundInit;
import theblockbox.huntersdream.api.interfaces.IAmmunition;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EntityBullet extends EntityArrow implements IAmmunition {
    private final Item item;
    private final IAmmunition ammunition;
    private int maxRange = -1;
    private BlockPos initialPosForRange = null;

    public EntityBullet(World worldIn) {
        super(worldIn);
        this.item = ItemInit.MUSKET_BALL;
        this.ammunition = (IAmmunition) ItemInit.MUSKET_BALL;
    }

    public EntityBullet(World worldIn, double x, double y, double z, @Nonnull Item ammunition, double damage) {
        super(worldIn, x, y, z);
        this.item = ammunition;
        // throw ClassCastException if the item isn't an instance of IAmmunition
        this.ammunition = (IAmmunition) Objects.requireNonNull(ammunition);
        this.setDamage(damage);
    }

    public EntityBullet(World worldIn, EntityLivingBase shooter, @Nonnull Item ammunition, double damage) {
        super(worldIn, shooter);
        this.item = ammunition;
        // throw ClassCastException if the item isn't an instance of IAmmunition
        this.ammunition = (IAmmunition) Objects.requireNonNull(ammunition);
        this.setDamage(damage);
    }

    @Override
    protected ItemStack getArrowStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public IAmmunition.AmmunitionType[] getAmmunitionTypes() {
        return this.ammunition.getAmmunitionTypes();
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        if (soundIn == SoundEvents.ENTITY_ARROW_HIT) {
            soundIn = SoundInit.BULLET_HIT;
        }
        super.playSound(soundIn, volume, pitch);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        if ((this.maxRange > 0) && (this.initialPosForRange != null) && (this.initialPosForRange.distanceSq(this.getPosition()) > this.maxRange)) {
            // if max range is reached, don't hurt hit entity but remove bullet
            this.setDead();
            return;
        }
        // if the bullet is made out of silver and the entity is undead, give twice as much damage
        Entity entity = raytraceResultIn.entityHit;
        boolean flag = ArrayUtils.contains(this.getAmmunitionTypes(), IAmmunition.AmmunitionType.SILVER)
                && (entity instanceof EntityLivingBase) && ((EntityLivingBase) entity).isEntityUndead();
        this.setDamage(this.getDamage() * (flag ? 0.33D : 0.16D));
        super.onHit(raytraceResultIn);
        // remove bullet since it shouldn't stay in its place like arrows do
        this.setDead();
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn) {
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        super.move(type, x, y, z);
    }

    public Item getItem() {
        return this.item;
    }

    public void setMaxRange(int maxRange, BlockPos currentBulletPos) {
        this.maxRange = maxRange;
        this.initialPosForRange = currentBulletPos;
    }
}
