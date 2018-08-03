package theblockbox.huntersdream.entity;

import com.google.common.base.Predicate;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;

public class EntityGoblinTD extends EntityVillager implements ITransformationCreature, IEntityAdditionalSpawnData {
	private int textureIndex;
	/** The amount of textures available for the goblins */
	public static final int TEXTURES = 6;
	private static final DataParameter<Integer> TRANSFORMATION_INT = EntityDataManager
			.<Integer>createKey(EntityGoblinTD.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TEXTURE_INDEX = EntityDataManager
			.<Integer>createKey(EntityGoblinTD.class, DataSerializers.VARINT);

	public EntityGoblinTD(World worldIn, int textureIndex, Transformations transformation) {
		super(worldIn);
		this.setSize(0.5F, 1.4F);
		this.textureIndex = ChanceHelper.randomInt(TEXTURES);
		this.dataManager.set(TRANSFORMATION_INT, transformation.getID());
		this.dataManager.set(TEXTURE_INDEX, transformation.getRandomTextureIndex());
	}

	public EntityGoblinTD(World worldIn) {
		this(worldIn, 0, ChanceHelper.chanceOf(25) ? Transformations.WEREWOLF : Transformations.HUMAN);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TRANSFORMATION_INT, 0);
		this.dataManager.register(TEXTURE_INDEX, 0);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 0.7D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.applyEntityAI();
	}

	protected void applyEntityAI() {
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		Predicate<EntityCreature> predicateMob = input -> {
			return !(input instanceof EntityWerewolf);
		};
		Predicate<EntityPlayer> predicatePlayer = input -> {
			ITransformation transformation = TransformationHelper.getITransformation(input);
			// TODO: Edit this line here
			return !((transformation.getTransformation() == Transformations.WEREWOLF) && transformation.transformed());
		};
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 10,
				true, false, predicateMob));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 10,
				true, false, predicatePlayer));
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.getCurrentTransformation().transformCreatureWhenPossible(this);
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
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
	}

	@Override
	public float getEyeHeight() {
		return 1.2F;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.textureIndex);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.textureIndex = buffer.readInt();
	}

	public int getTexture() {
		return this.textureIndex;
	}

	@Override
	public EntityVillager createChild(EntityAgeable ageable) {
		return new EntityGoblinTD(world);
	}

	@Override
	public int getTextureIndex() {
		return this.dataManager.get(TEXTURE_INDEX);
	}

	@Override
	public Transformations[] getTransformationsNotImmuneTo() {
		return new Transformations[] { Transformations.VAMPIRE, Transformations.WEREWOLF };
	}

	@Override
	public int getCurrentTransformationID() {
		return this.dataManager.get(TRANSFORMATION_INT);
	}

	@Override
	public void setCurrentTransformationID(int transformation) {
		this.dataManager.set(TRANSFORMATION_INT, transformation);
	}

	@Override
	public void setTextureIndex(int index) {
		this.dataManager.set(TEXTURE_INDEX, index);
	}
}
