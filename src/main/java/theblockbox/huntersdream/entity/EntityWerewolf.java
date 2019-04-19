package theblockbox.huntersdream.entity;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.event.ExtraDataEvent;
import theblockbox.huntersdream.api.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.entity.ai.EntityAIWerewolfAttack;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * A werewolf
 */
public class EntityWerewolf extends EntityMob implements ITransformation, IEntityAdditionalSpawnData {
    /**
     * The speed of every werewolf (exactly two times faster than a normal player)
     */
    public static final double SPEED = 0.5D;
    public static final float ATTACK_DAMAGE = 8.0F;
    public static final Transformation TRANSFORMATION = Transformation.WEREWOLF;
    private static final DataParameter<NBTTagCompound> TRANSFORMATION_DATA = EntityDataManager
            .createKey(EntityWerewolf.class, DataSerializers.COMPOUND_TAG);
    private static final DataParameter<Integer> TEXTURE_INDEX = EntityDataManager.createKey(EntityWerewolf.class,
            DataSerializers.VARINT);
    private static final String CLASS_NAME = EntityWerewolf.class.getName();
    /**
     * name of the entity the werewolf was before transformation
     */
    private String untransformedEntityName;
    private NBTTagCompound extraData;
    private boolean usesAlexSkin;

    public EntityWerewolf(World worldIn, int textureIndex, String entityName, @Nonnull NBTTagCompound extraData) {
        super(worldIn);
        if (entityName == null || EntityWerewolf.CLASS_NAME.equals(entityName))
            throw new IllegalArgumentException(
                    "Can't transform already transformed werewolf (tried to spawn werewolf with its untransformed entity being werewolf)\nExtra data: ");

        // if the texture index is not a valid texture index, it'll be set in
        // TransformationEventHandler#onEntityJoin(EntityJoinWorldEvent)
        this.setTextureIndex(textureIndex);

        this.untransformedEntityName = entityName;
        this.usesAlexSkin = this.rand.nextBoolean();
        this.setSize(0.6F, 1.85F);
        this.setExtraData(extraData);
        Validate.notNull(extraData, "Can't spawn werewolf with null extra data");
    }

    public EntityWerewolf(World worldIn, int textureIndex, EntityCreature entity, NBTTagCompound extraData) {
        this(worldIn, textureIndex, (TransformationHelper.getITransformationCreature(entity).isPresent() ? "$useCap" : "")
                + entity.getClass().getName(), extraData);
    }

    public EntityWerewolf(World worldIn) {
        this(worldIn, -1, "$useCap" + EntityVillager.class.getName(), GeneralHelper.EMPTY_COMPOUND);
    }

    public boolean usesAlexSkin() {
        return this.usesAlexSkin;
    }

    public void setUseAlexSkin(boolean useAlexSkin) {
        this.usesAlexSkin = useAlexSkin;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        NBTTagCompound transformationData = new NBTTagCompound();
        transformationData.setString("transformation", EntityWerewolf.TRANSFORMATION.toString());
        transformationData.setBoolean("transformed", true);
        this.dataManager.register(EntityWerewolf.TRANSFORMATION_DATA, transformationData);
        this.dataManager.register(EntityWerewolf.TEXTURE_INDEX, 0);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIBreakDoor(this) {
            @Override
            public void updateTask() {
                super.updateTask();
                // if the door should be destroyed but it couldn't be since the difficulty was not hard, destroy it
                if (this.breakingTime == 240 && this.entity.world.getDifficulty() == EnumDifficulty.HARD) {
                    this.entity.world.setBlockToAir(this.doorPosition);
                    this.entity.world.playEvent(1021, this.doorPosition, 0);
                    this.entity.world.playEvent(2001, this.doorPosition, Block.getIdFromBlock(this.doorBlock));
                }
            }
        });
        this.tasks.addTask(2, new EntityAIWerewolfAttack(this, EntityWerewolf.SPEED + 0.2D, false));

        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));

        this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
        Predicate<EntityCreature> predicateMob = input -> !(TransformationHelper.isInfected(input)
                || (TransformationHelper.getTransformation(input) == Transformation.WEREWOLF));
        Predicate<EntityPlayer> predicatePlayer = input -> {
            ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(input);
            Optional<IInfectOnNextMoon> ionm = WerewolfHelper.getIInfectOnNextMoon(input);
            return !((transformation.getTransformation() == Transformation.WEREWOLF)
                    || ((ionm.isPresent()) && ionm.get().isInfected()));
        };
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(3,
                new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 10, true, false, predicateMob));
        this.targetTasks.addTask(3,
                new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, predicatePlayer));
        ((PathNavigateGround) this.navigator).setBreakDoors(ConfigHandler.server.werewolvesBreakDoors);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(EntityWerewolf.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(EntityWerewolf.SPEED);
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WOLF_GROWL;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource sound) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    @Override
    public void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, this.getSoundPitch());
    }

    @Override
    public float getSoundPitch() {
        return super.getSoundPitch() - 1.0F;
    }

    @Override
    public float getEyeHeight() {
        return 1.62F;
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.ticksExisted % 80 == 0) {
            this.tryToTransformBack();
        }
    }

    @Override
    public Transformation getTransformation() {
        return EntityWerewolf.TRANSFORMATION;
    }

    @Override
    public void setTransformation(Transformation transformation) {
        throw new UnsupportedOperationException("This creature's transformation is already determined");
    }

    @Override
    public int getTextureIndex() {
        return this.dataManager.get(EntityWerewolf.TEXTURE_INDEX);
    }

    @Override
    public void setTextureIndex(int index) {
        this.dataManager.set(EntityWerewolf.TEXTURE_INDEX, index);
    }

    @Override
    public NBTTagCompound getTransformationData() {
        return this.dataManager.get(EntityWerewolf.TRANSFORMATION_DATA);
    }

    @Override
    public void setTransformationData(NBTTagCompound transformationData) {
        this.dataManager.set(EntityWerewolf.TRANSFORMATION_DATA, transformationData);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.getUntransformedEntityName());
        buffer.writeBoolean(this.usesAlexSkin());
        ByteBufUtils.writeTag(buffer, this.getEntityData());
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.untransformedEntityName = ByteBufUtils.readUTF8String(buffer);
        this.setUseAlexSkin(buffer.readBoolean());
        this.setExtraData(ObjectUtils.defaultIfNull(ByteBufUtils.readTag(buffer), GeneralHelper.EMPTY_COMPOUND));
    }

    public String getUntransformedEntityName() {
        return this.untransformedEntityName;
    }

    public NBTTagCompound getExtraData() {
        return this.extraData;
    }

    public void setExtraData(NBTTagCompound extraData) {
        this.extraData = extraData;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("textureIndex", this.getTextureIndex());
        compound.setString("untransformedEntityName", this.getUntransformedEntityName());
        compound.setTag("untransformedEntityExtraData", this.getExtraData());
        compound.setBoolean("useAlexSkin", this.usesAlexSkin());
        compound.setTag("transformationData", this.getTransformationData());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("textureIndex"))
            this.setTextureIndex(compound.getInteger("textureIndex"));
        if (compound.hasKey("untransformedEntityName"))
            this.untransformedEntityName = compound.getString("untransformedEntityName");
        if (compound.hasKey("untransformedEntityExtraData"))
            this.setExtraData((NBTTagCompound) compound.getTag("untransformedEntityExtraData"));
        if (compound.hasKey("useAlexSkin"))
            this.setUseAlexSkin(compound.getBoolean("useAlexSkin"));
        if (compound.hasKey("transformationData"))
            this.setTransformationData((NBTTagCompound) compound.getTag("transformationData"));
    }

    @Override
    public boolean isValidLightLevel() {
        // TODO: Test if this works
        System.out.println("spawn");
        // make werewolves only spawn on full moon
        return WerewolfHelper.isWerewolfTime(this.world);
    }

    public boolean tryToTransformBack() {
        if (!this.world.isRemote) {
            if (!WerewolfHelper.isWerewolfTime(this.world)) {
                if (!MinecraftForge.EVENT_BUS.post(
                        new WerewolfTransformingEvent(this, true, WerewolfTransformingEvent.WerewolfTransformingReason.FULL_MOON_END))) {
                    EntityCreature e;
                    String entityName = this.getUntransformedEntityName();
                    if (entityName.startsWith("$useCap")) {
                        String eName = entityName.substring(7);
                        try {
                            @SuppressWarnings("unchecked")
                            Class<? extends Entity> entityClass = (Class<? extends Entity>) Class
                                    .forName(eName);
                            Constructor<?> constructor = entityClass.getConstructor(World.class);
                            e = (EntityCreature) constructor.newInstance(this.world);
                            // remember: this is only server side and the client doesn't actually need to
                            // know about this
                            ITransformationCreature transformation = TransformationHelper
                                    .getITransformationCreature(e).get();
                            transformation.setTextureIndex(this.getTextureIndex());
                            transformation.setTransformation(this.getTransformation());
                        } catch (NullPointerException ex) {
                            NullPointerException exception = new NullPointerException(
                                    "Either the entity's capability or something else was null");
                            exception.setStackTrace(ex.getStackTrace());
                            throw exception;
                        } catch (ClassNotFoundException ex) {
                            throw new NullPointerException("Can't find class " + eName);
                        } catch (ClassCastException ex) {
                            throw new IllegalArgumentException("Given class " + eName + " is not an entity");
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                                | InstantiationException | InvocationTargetException ex) {
                            throw new IllegalArgumentException(
                                    "This entity does not have an accessible constructor and is therefore not registered");
                        }
                    } else {
                        try {
                            @SuppressWarnings("unchecked")
                            Class<? extends Entity> entityClass = (Class<? extends Entity>) Class
                                    .forName(entityName);
                            Constructor<?> constructor = entityClass.getConstructor(World.class, int.class,
                                    Transformation.class);
                            e = (EntityCreature) constructor.newInstance(this.world, this.getTextureIndex(),
                                    this.getTransformation());
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        } catch (ClassCastException ex) {
                            throw new RuntimeException("Given class " + entityName + " is not an entity", ex);
                        } catch (NoSuchMethodException | InvocationTargetException | SecurityException
                                | IllegalAccessException ex) {
                            throw new RuntimeException("Class " + entityName
                                    + " does not have an accessible constructor with parameters World, int, Transformation",
                                    ex);
                        } catch (InstantiationException ex) {
                            throw new RuntimeException("Can't instantiate class " + entityName, ex);
                        }
                    }

                    // set health
                    this.extraData.setFloat("Health",
                            e.getMaxHealth() / (this.getMaxHealth() / this.getHealth()));
                    ExtraDataEvent extraDataEvent = new ExtraDataEvent(e, this.extraData, false);
                    MinecraftForge.EVENT_BUS.post(extraDataEvent);
                    e.readEntityFromNBT(extraDataEvent.getExtraData());

                    e.setPosition(this.posX, this.posY, this.posZ);
                    this.world.spawnEntity(e);
                    this.world.removeEntity(this);
                    return true;
                }
            }
        }
        return false;
    }
}
