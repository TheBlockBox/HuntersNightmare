package theblockbox.huntersdream.entity;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.model.ModelLycanthropeBiped;
import theblockbox.huntersdream.entity.model.ModelLycanthropeQuadruped;
import theblockbox.huntersdream.init.TransformationInit;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationEntityTransformed;

/**
 * A werewolf
 */
public class EntityWerewolf extends EntityMob implements ITransformationEntityTransformed, IEntityAdditionalSpawnData {
	public static final double SPEED = 0.5D;
	public static final Transformation TRANSFORMATION = TransformationInit.WEREWOLF;
	/** the werewolf texture to be used */
	private int textureIndex;
	/** name of the entity the werewolf was before transformation */
	private String untransformedEntityName;
	private NBTTagCompound extraData;

	public EntityWerewolf(World worldIn, int textureIndex, String entityName, @Nonnull NBTTagCompound extraData) {
		super(worldIn);
		if (textureIndex >= 0 && textureIndex < this.getTransformation().getTextures().length) {
			this.setTextureIndex(textureIndex);
		} else {
			Main.getLogger().warn("A werewolf has been created with a texture index (" + textureIndex
					+ ") that is out of bounds. This shouldn't happen.\nPath: " + (new ExecutionPath()).get(0, 3));
			this.setTextureIndexWhenNeeded();
		}
		this.untransformedEntityName = entityName;
		this.setSize(1F, ModelLycanthropeBiped.HEIGHT);
		this.setExtraData(extraData);
		if (extraData == null) {
			throw new NullPointerException("Can't spawn werewolf with null extra data");
		}
	}

	public EntityWerewolf(World worldIn, int textureIndex, EntityCreature entity, NBTTagCompound extraData) {
		this(worldIn, textureIndex, ((TransformationHelper.getITransformationCreature(entity) != null) ? "$bycap" : "")
				+ entity.getClass().getName(), extraData);
	}

	public EntityWerewolf(World worldIn) {
		this(worldIn, TRANSFORMATION.getRandomTextureIndex(), "$bycap" + EntityVillager.class.getName(),
				GeneralHelper.EMPTY_COMPOUND);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, SPEED + 0.2D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(4, new EntityAIBreakDoor(this));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.applyEntityAI();
	}

	protected void applyEntityAI() {
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		Predicate<EntityCreature> predicateMob = input -> !((input instanceof EntityWerewolf)
				|| TransformationHelper.isInfected(input));
		Predicate<EntityPlayer> predicatePlayer = input -> {
			ITransformation transformation = TransformationHelper.getITransformation(input);
			IInfectOnNextMoon ionm = WerewolfHelper.getIInfectOnNextMoon(input);
			return !((transformation.getTransformation() == TransformationInit.WEREWOLF)
					|| ((ionm != null) && ionm.isInfected()));
		};
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 10,
				true, false, predicateMob));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 10,
				true, false, predicatePlayer));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
				.setBaseValue(TRANSFORMATION.getGeneralDamage(this));
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(SPEED);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_WOLF_GROWL;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource sound) {
		return SoundEvents.ENTITY_WOLF_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WOLF_DEATH;
	}

	@Override
	public float getEyeHeight() {
		return ModelLycanthropeBiped.EYE_HEIGHT;
	}

	@Override
	public boolean isChild() {
		return false;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() - 1.0F;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (ticksExisted % 80 == 0) {
			if (!world.isRemote) {
				if (!WerewolfHelper.isWerewolfTime(this)) {
					ITransformationEntityTransformed.transformBack(this);
				}
			}
		}
		if (isSprinting() || isSneaking())
			setSize(ModelLycanthropeQuadruped.WIDTH, ModelLycanthropeQuadruped.HEIGHT);
		else
			setSize(1F, ModelLycanthropeBiped.HEIGHT);
	}

	@Override
	public Transformation getTransformation() {
		return TRANSFORMATION;
	}

	@Override
	public int getTextureIndex() {
		return textureIndex;
	}

	@Override
	public void setTextureIndex(int index) {
		this.textureIndex = index;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.getUntransformedEntityName());
		buffer.writeInt(this.getTextureIndex());
		// ByteBufUtils.writeTag(buffer, this.getEntityData());
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.untransformedEntityName = ByteBufUtils.readUTF8String(buffer);
		this.setTextureIndex(buffer.readInt());
		// this.setExtraData(ByteBufUtils.readTag(buffer));
	}

	@Override
	public String getUntransformedEntityName() {
		return this.untransformedEntityName;
	}

	@Override
	public NBTTagCompound getExtraData() {
		return this.extraData;
	}

	@Override
	public void setExtraData(NBTTagCompound extraData) {
		this.extraData = extraData;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("textureIndex", this.getTextureIndex());
		compound.setString("untransformedEntityName", this.getUntransformedEntityName());
		compound.setTag("untransformedEntityExtraData", this.getExtraData());
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
	}
}
