package theblockbox.huntersdream.entity;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.interfaces.IGun;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformation;

import javax.annotation.Nullable;
import java.util.Objects;

public class EntityHunter extends EntityMob implements IRangedAttackMob, ITransformation, IEntityAdditionalSpawnData {
    public static final Transformation TRANSFORMATION = Transformation.HUMAN;
    private static final DataParameter<NBTTagCompound> TRANSFORMATION_DATA = EntityDataManager
            .createKey(EntityHunter.class, DataSerializers.COMPOUND_TAG);
    private boolean usesMeleeWeapons;
    private Item rangedWeapon = Items.AIR;

    public EntityHunter(World world) {
        super(world);
        if (this.rand.nextBoolean()) {
            this.usesMeleeWeapons = true;
            this.rangedWeapon = ItemInit.FLINTLOCK_PISTOL;
        }
        this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(this.usesMeleeWeapons() ? this.rangedWeapon : Items.IRON_SWORD));
    }

    public EntityHunter(World world, boolean usesMeleeWeapons) {
        super(world);
        if (this.rand.nextBoolean()) {
            this.usesMeleeWeapons = true;
            this.rangedWeapon = ItemInit.FLINTLOCK_PISTOL;
        }
        this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(this.usesMeleeWeapons() ? this.rangedWeapon : Items.IRON_SWORD));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        NBTTagCompound transformationData = new NBTTagCompound();
        transformationData.setString("transformation", EntityHunter.TRANSFORMATION.toString());
        this.dataManager.register(EntityHunter.TRANSFORMATION_DATA, transformationData);
    }

    @Override
    protected void initEntityAI() {
        // TODO: Finish ai
        super.initEntityAI();
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        // TODO: Change speed
        this.tasks.addTask(2, this.usesMeleeWeapons() ? new EntityAIAttackMelee(this, EntityWerewolf.SPEED + 0.2D, false)
                : new EntityAIAttackRanged(this, 1.0D, 20, 15.0F));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));

        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        Predicate<EntityLivingBase> predicate = input -> !((input instanceof EntityPlayer) || (input instanceof EntityAnimal));
        this.targetTasks.addTask(3,
                new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, predicate));
    }

    @Override
    public void setAttackTarget(@Nullable EntityLivingBase entity) {
        super.setAttackTarget(entity);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(TransformationHelper.getHunterWeaponForEntity(entity, this.usesMeleeWeapons())));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // same damage and speed as players
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612D);
    }

    @Override
    public Transformation getTransformation() {
        return EntityHunter.TRANSFORMATION;
    }

    @Override
    public void setTransformation(Transformation transformation) {
        throw new UnsupportedOperationException("This creature's transformation has already been determined");
    }

    @Override
    public int getTextureIndex() {
        return 0;
    }

    @Override
    public void setTextureIndex(int index) {
        throw new UnsupportedOperationException("This creature's texture index has already been determined");
    }

    @Override
    public NBTTagCompound getTransformationData() {
        return this.dataManager.get(EntityHunter.TRANSFORMATION_DATA);
    }

    @Override
    public void setTransformationData(NBTTagCompound transformationData) {
        this.dataManager.set(EntityHunter.TRANSFORMATION_DATA, transformationData);
    }

    // same sounds as players
    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.PLAYERS;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return (damageSource == DamageSource.ON_FIRE) ? SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE :
                ((damageSource == DamageSource.DROWN) ? SoundEvents.ENTITY_PLAYER_HURT_DROWN : SoundEvents.ENTITY_PLAYER_HURT);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return (heightIn > 4) ? SoundEvents.ENTITY_PLAYER_BIG_FALL : SoundEvents.ENTITY_PLAYER_SMALL_FALL;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("transformationData", this.dataManager.get(EntityHunter.TRANSFORMATION_DATA));
        compound.setBoolean("usesMeleeWeapons", this.usesMeleeWeapons());
        compound.setString("rangedWeapon", Objects.toString(this.rangedWeapon.getRegistryName(), ""));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("transformationData"))
            this.setTransformationData((NBTTagCompound) compound.getTag("transformationData"));
        if (compound.hasKey("usesMeleeWeapons"))
            this.usesMeleeWeapons = compound.getBoolean("usesMeleeWeapons");
        if (compound.hasKey("rangedWeapon"))
            this.rangedWeapon = Item.getByNameOrId(compound.getString("rangedWeapon"));
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(this.usesMeleeWeapons());
        ByteBufUtils.writeUTF8String(buffer, Objects.toString(this.rangedWeapon.getRegistryName(), ""));
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.usesMeleeWeapons = additionalData.readBoolean();
        this.rangedWeapon = Item.getByNameOrId(ByteBufUtils.readUTF8String(additionalData));
    }

    /**
     * Returns true if this hunter uses melee weapons (swords, stakes, etc.), otherwise ranged weapons (guns).
     */
    public boolean usesMeleeWeapons() {
        return this.usesMeleeWeapons;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        ItemStack heldStack = this.getHeldItemMainhand();
        if (heldStack.getItem() instanceof IGun) {
            ((IGun) heldStack.getItem()).spawnBullet(this, heldStack);
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        // TODO: Test what this actually does
    }

    /**
     * Returns true if this hunter should be drawn with folded arms.
     */
    public boolean shouldFoldArms() {
        return true;
    }
}
