package theblockbox.huntersdream.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.interfaces.IGun;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformation;

import javax.annotation.Nullable;

public class EntityHunter extends EntityMob implements IRangedAttackMob, ITransformation {
    public static final Transformation TRANSFORMATION = Transformation.HUMAN;
    private static final DataParameter<NBTTagCompound> TRANSFORMATION_DATA = EntityDataManager
            .createKey(EntityHunter.class, DataSerializers.COMPOUND_TAG);

    public EntityHunter(World world) {
        super(world);
        this.enablePersistence();
        this.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(TransformationHelper.getHunterWeaponForEntity(null)));
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
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        // TODO: Change speed
        this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.0D, 20, 15.0F));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class,
                10, true, false, input -> ((input instanceof EntityMob) || WerewolfHelper.isTransformed(input)) && !(input instanceof EntityHunter)));
    }

    @Override
    public void setAttackTarget(@Nullable EntityLivingBase entity) {
        super.setAttackTarget(entity);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(TransformationHelper.getHunterWeaponForEntity(entity)));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // same damage and speed as players
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(ItemInit.HUNTER_HAT));
        this.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(ItemInit.HUNTER_COAT));
        this.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(ItemInit.HUNTER_PANTS));
        this.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(ItemInit.HUNTER_BOOTS));
        return super.onInitialSpawn(difficulty, livingdata);
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
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("transformationData"))
            this.setTransformationData((NBTTagCompound) compound.getTag("transformationData"));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        ItemStack heldStack = this.getHeldItemMainhand();
        if (heldStack.getItem() instanceof IGun) {
            IGun gun = ((IGun) heldStack.getItem());
            gun.setAmmunition(heldStack, TransformationHelper.getHunterAmmunitionForEntity(target, gun), true);
            gun.spawnBullet(this, heldStack);
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.swingArm(EnumHand.MAIN_HAND);
    }
}
