package theblockbox.huntersdream.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.common.base.Predicate;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper.Transformations;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.ITransformation;

/**
 * A werewolf
 */
public class EntityWerewolf extends EntityMob implements ITransformation, IEntityAdditionalSpawnData {
	/** the werewolf texture to be used */
	private int textureIndex;
	/** name of the entity the werewolf was before transformation */
	private String entityName;
	public static final double SPEED = 0.5D;
	public static final Transformations TRANSFORMATION = Transformations.WEREWOLF;

	public EntityWerewolf(World worldIn, int textureIndex, String entityName) {
		super(worldIn);
		this.textureIndex = textureIndex;
		this.entityName = entityName;
	}

	public EntityWerewolf(World worldIn, int textureIndex, EntityLivingBase entity) {
		this(worldIn, textureIndex, EntityList.getEntityString(entity));
	}

	public EntityWerewolf(World worldIn) {
		this(worldIn, 0, EntityWerewolfVillager.class.getName());
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, SPEED + 0.3D, false));
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
			return !((transformation.getTransformation() == Transformations.WEREWOLF) && transformation.transformed());
		};
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 10,
				true, false, predicateMob));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 10,
				true, false, predicatePlayer));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(TRANSFORMATION.GENERAL_DAMAGE);
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
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (ticksExisted % 40 == 0) {
			if (!world.isRemote) {
				if (!WerewolfHelper.isWerewolfTime(this)) {
					EntityLiving entity = null;

					if (!entityName.startsWith("player")) {

						try {
							@SuppressWarnings("unchecked")
							Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName(entityName);
							Constructor<?> constructor = entityClass.getConstructor(World.class, int.class,
									Transformations.class);
							entity = (EntityLiving) constructor.newInstance(this.world, this.getTextureIndex(),
									this.getTransformation());
						} catch (ClassNotFoundException e) {
							throw new NullPointerException("Can't find class " + entityName);
						} catch (ClassCastException e) {
							throw new IllegalArgumentException("Given class " + entityName + " is not an entity");
						} catch (NoSuchMethodException | InvocationTargetException | SecurityException
								| IllegalAccessException e) {
							throw new NullPointerException("Class " + entityName
									+ " does not have an accessible constructor with parameters World, int, Transformations");
						} catch (InstantiationException e) {
							throw new IllegalArgumentException("Can't instantiate class " + entityName);
						}

						entity.setPosition(posX, posY, posZ);
						world.spawnEntity(entity);
					}

					world.removeEntity(this);
				}
			}
		}
	}

	@Override
	public boolean transformed() {
		return true;
	}

	@Override
	public int getTransformationInt() {
		return TRANSFORMATION.ID;
	}

	@Override
	public void setTransformationID(int id) {
		throw new UnsupportedOperationException("Transformation already determined");
	}

	@Override
	public void setTransformed(boolean transformed) {
		throw new UnsupportedOperationException("Entity is always transformed");
	}

	@Override
	public int getTextureIndex() {
		return textureIndex;
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

	/**
	 * Returns the name of the werewolf's normal form
	 */
	public String getEntityName() {
		return entityName;
	}
}
