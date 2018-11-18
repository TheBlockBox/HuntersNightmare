package theblockbox.huntersdream.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.base.Predicate;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.entity.ai.EntityAIBreakAllDoors;
import theblockbox.huntersdream.entity.ai.EntityAIWerewolfAttack;
import theblockbox.huntersdream.event.ExtraDataEvent;
import theblockbox.huntersdream.event.WerewolfTransformingEvent;
import theblockbox.huntersdream.event.WerewolfTransformingEvent.WerewolfTransformingReason;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.Transformation;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.IInfectOnNextMoon;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformation;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

/**
 * A werewolf
 */
public class EntityWerewolf extends EntityMob implements ITransformation, IEntityAdditionalSpawnData {
	/**
	 * The speed of every werewolf (exactly two times faster than a normal player)
	 */
	public static final double SPEED = 0.5D;
	public static final float ATTACK_DAMAGE = 8F;
	public static final Transformation TRANSFORMATION = Transformation.WEREWOLF;
	private static final DataParameter<NBTTagCompound> TRANSFORMATION_DATA = EntityDataManager
			.createKey(EntityWerewolf.class, DataSerializers.COMPOUND_TAG);
	/** the werewolf texture to be used */
	private int textureIndex;
	/** name of the entity the werewolf was before transformation */
	private String untransformedEntityName;
	private NBTTagCompound extraData;
	private boolean usesAlexSkin;

	public EntityWerewolf(World worldIn, int textureIndex, String entityName, @Nonnull NBTTagCompound extraData) {
		super(worldIn);
		if (textureIndex >= 0 && textureIndex < this.getTransformation().getTextures().length) {
			this.setTextureIndex(textureIndex);
		} else {
			Main.getLogger().warn("A werewolf has been created with a texture index (" + textureIndex
					+ ") that is out of bounds. This shouldn't happen.\nPath: " + ExecutionPath.get(0, 5));
			this.setTextureIndexWhenNeeded();
		}
		this.untransformedEntityName = entityName;
		this.usesAlexSkin = ChanceHelper.randomBoolean();
		this.setSize(0.6F, WerewolfHelper.getWerewolfHeight(this));
		this.setExtraData(extraData);
		Validate.notNull(extraData, "Can't spawn werewolf with null extra data");
	}

	public boolean usesAlexSkin() {
		return this.usesAlexSkin;
	}

	public void setUseAlexSkin(boolean useAlexSkin) {
		this.usesAlexSkin = useAlexSkin;
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
		NBTTagCompound transformationData = new NBTTagCompound();
		transformationData.setString("transformation", TRANSFORMATION.toString());
		transformationData.setBoolean("transformed", true);
		this.dataManager.register(TRANSFORMATION_DATA, GeneralHelper.EMPTY_COMPOUND);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.tasks.addTask(1, new EntityAIBreakAllDoors(this));
		this.tasks.addTask(2, new EntityAIWerewolfAttack(this, SPEED + 0.2D, false));

		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));

		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		Predicate<EntityCreature> predicateMob = input -> !((input instanceof EntityWerewolf)
				|| TransformationHelper.isInfected(input));
		Predicate<EntityPlayer> predicatePlayer = input -> {
			ITransformationPlayer transformation = TransformationHelper.getITransformationPlayer(input);
			Optional<IInfectOnNextMoon> ionm = WerewolfHelper.getIInfectOnNextMoon(input);
			return !((transformation.getTransformation() == Transformation.WEREWOLF)
					|| ((ionm.isPresent()) && ionm.get().isInfected()));
		};
		this.targetTasks.addTask(2,
				new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 10, true, false, predicateMob));
		this.targetTasks.addTask(2,
				new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false, predicatePlayer));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);
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
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, this.getSoundPitch());
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() - 1.0F;
	}

	@Override
	public float getEyeHeight() {
		return WerewolfHelper.getWerewolfEyeHeight(this);
	}

	@Override
	public void setSneaking(boolean sneaking) {
		super.setSneaking(sneaking);
	}

	@Override
	public boolean isChild() {
		return false;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.ticksExisted % 80 == 0) {
			if (!this.world.isRemote) {
				if (!WerewolfHelper.isWerewolfTime(this.world)) {
					if (!MinecraftForge.EVENT_BUS.post(
							new WerewolfTransformingEvent(this, true, WerewolfTransformingReason.FULL_MOON_END))) {
						EntityCreature e = null;
						String entityName = this.getUntransformedEntityName();

						// TODO: Remove this when no control is done (only the line below and its
						// bracket)
						if (!entityName.startsWith("player")) {

							if (!entityName.startsWith("$bycap")) {
								try {
									@SuppressWarnings("unchecked")
									Class<? extends Entity> entityClass = (Class<? extends Entity>) Class
											.forName(entityName);
									Constructor<?> constructor = entityClass.getConstructor(World.class, int.class,
											Transformation.class);
									e = (EntityCreature) constructor.newInstance(this.world, this.getTextureIndex(),
											this.getTransformation());
								} catch (ClassNotFoundException ex) {
									throw new NullPointerException("Can't find class " + entityName);
								} catch (ClassCastException ex) {
									throw new IllegalArgumentException(
											"Given class " + entityName + " is not an entity");
								} catch (NoSuchMethodException | InvocationTargetException | SecurityException
										| IllegalAccessException ex) {
									throw new NullPointerException("Class " + entityName
											+ " does not have an accessible constructor with parameters World, int, Transformations");
								} catch (InstantiationException ex) {
									throw new IllegalArgumentException("Can't instantiate class " + entityName);
								}
							} else {
								String eName = entityName.substring(6);
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
							}

							// set health
							this.extraData.setFloat("Health",
									e.getMaxHealth() / (this.getMaxHealth() / this.getHealth()));
							ExtraDataEvent extraDataEvent = new ExtraDataEvent(e, this.extraData, false);
							MinecraftForge.EVENT_BUS.post(extraDataEvent);
							e.readEntityFromNBT(extraDataEvent.getExtraData());

							e.setPosition(this.posX, this.posY, this.posZ);
							this.world.spawnEntity(e);
						}

						this.world.removeEntity(this);
					}
				}

				// set size
				float newHeight = WerewolfHelper.getWerewolfHeight(this);
				if (this.height != newHeight) {
					this.setSize(0.6F, newHeight);
				}
			}
		}
	}

	@Override
	public Transformation getTransformation() {
		return TRANSFORMATION;
	}

	@Override
	public void setTransformation(Transformation transformation) {
		throw new UnsupportedOperationException("This creature's transformation is already determined");
	}

	@Override
	public int getTextureIndex() {
		return this.textureIndex;
	}

	@Override
	public void setTextureIndex(int index) {
		this.textureIndex = index;
	}

	@Override
	public NBTTagCompound getTransformationData() {
		return this.dataManager.get(TRANSFORMATION_DATA);
	}

	@Override
	public void setTransformationData(NBTTagCompound transformationData) {
		this.dataManager.set(TRANSFORMATION_DATA, transformationData);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.getUntransformedEntityName());
		buffer.writeInt(this.getTextureIndex());
		buffer.writeBoolean(this.usesAlexSkin());
		ByteBufUtils.writeTag(buffer, this.getEntityData());
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.untransformedEntityName = ByteBufUtils.readUTF8String(buffer);
		this.setTextureIndex(buffer.readInt());
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
		// make werewolves only spawn on full moon
		return WerewolfHelper.isWerewolfTime(this.world);
	}
}
