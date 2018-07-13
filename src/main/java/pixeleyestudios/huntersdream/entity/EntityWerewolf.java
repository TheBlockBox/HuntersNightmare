package pixeleyestudios.huntersdream.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.interfaces.ITransformation;

/**
 * A werewolf
 */
public class EntityWerewolf extends EntityZombie implements ITransformation, IEntityAdditionalSpawnData {

	private int textureIndex;
	private String entityName;
	public static final double SPEED = 0.20000000298023224D;

	public EntityWerewolf(World worldIn, int textureIndex, String entityName) {
		super(worldIn);
		this.textureIndex = textureIndex;
		this.entityName = entityName;
	}

	public EntityWerewolf(World worldIn, int textureIndex, EntityLivingBase entity) {
		this(worldIn, textureIndex, EntityList.getEntityString(entity));
	}

	public EntityWerewolf(World worldIn) {
		this(worldIn, 0, "werewolf");
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, SPEED + 0.02D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.applyEntityAI();
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(SPEED);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return super.getAmbientSound();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource sound) {
		return super.getHurtSound(sound);
	}

	@Override
	protected SoundEvent getDeathSound() {
		return super.getDeathSound();
	}

	@Override
	public float getEyeHeight() {
		return super.getEyeHeight();
	}

	@Override
	public boolean isChild() {
		return false;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (ticksExisted % 100 == 0) {
			if (!world.isRemote) {

			}
		}
	}

	// TODO: Add weakness for silver tools
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean transformed() {
		return true;
	}

	@Override
	public int getTransformationInt() {
		return Transformations.WEREWOLF.ID;
	}

	@Override
	public void setTransformationID(int id) {
		throw new IllegalArgumentException("Transformation already determined");
	}

	@Override
	public void setTransformed(boolean transformed) {
		// just does nothing
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = super.attackEntityAsMob(entityIn);

		if (flag && this.getHeldItemMainhand().isEmpty() && entityIn instanceof EntityLivingBase) {
			float f = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
			((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 140 * (int) f));
			// TODO: Replace poison with lupum vocant
		}

		return flag;
	}

	@Override
	protected boolean shouldBurnInDay() {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, entityName);
		buffer.writeInt(textureIndex);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.entityName = ByteBufUtils.readUTF8String(buffer);
		this.textureIndex = buffer.readInt();
	}

}
