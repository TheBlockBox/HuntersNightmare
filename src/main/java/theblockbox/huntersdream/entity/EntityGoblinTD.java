package theblockbox.huntersdream.entity;

import com.google.common.base.Predicate;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;

public class EntityGoblinTD extends EntityVillager
		implements ITransformationCreature, IEntityAdditionalSpawnData, IMob {
	/** The amount of textures available for the goblins */
	public static final int TEXTURES = 7;
	private int goblinTextureIndex;
	private static final DataParameter<Integer> TEXTURE_INDEX = EntityDataManager
			.<Integer>createKey(EntityGoblinTD.class, DataSerializers.VARINT);
	private static final DataParameter<String> TRANSFORMATION_NAME = EntityDataManager
			.<String>createKey(EntityGoblinTD.class, DataSerializers.STRING);

	public EntityGoblinTD(World worldIn, int textureIndex, Transformations transformation) {
		super(worldIn);
		this.setSize(0.5F, 1.4F);
		this.goblinTextureIndex = ChanceHelper.randomInt(TEXTURES);
		this.dataManager.set(TRANSFORMATION_NAME, transformation.toString());
		this.dataManager.set(TEXTURE_INDEX, transformation.getRandomTextureIndex());
	}

	public EntityGoblinTD(World worldIn) {
		this(worldIn, 0, ChanceHelper.chanceOf(25) ? Transformations.WEREWOLF : Transformations.HUMAN);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TEXTURE_INDEX, 0);
		this.dataManager.register(TRANSFORMATION_NAME, Transformations.HUMAN.toString());
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 0.7D, false));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.tasks.addTask(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
		this.applyEntityAI();
	}

	protected void applyEntityAI() {
		Predicate<EntityCreature> predicateMob = input -> {
			return !(input instanceof EntityGoblinTD);
		};
		Predicate<EntityPlayer> predicatePlayer = WerewolfHelper::transformedWerewolf;
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityCreature>(this, EntityCreature.class, 10,
				true, false, predicateMob));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, 10,
				true, false, predicatePlayer));
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
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5);
	}

	@Override
	public float getEyeHeight() {
		return 1.2F;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.goblinTextureIndex);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.goblinTextureIndex = buffer.readInt();
	}

	public int getTexture() {
		return this.goblinTextureIndex;
	}

	@Override
	public EntityVillager createChild(EntityAgeable ageable) {
		return new EntityGoblinTD(world);
	}

	@Override
	public int getTextureIndex() {
		int textureIndex = this.dataManager.get(TEXTURE_INDEX);
		try {
			this.getTransformation().getTextures()[textureIndex].getClass();
			return textureIndex;
		} catch (ArrayIndexOutOfBoundsException e) {
			setTextureIndex(this.getTransformation().getRandomTextureIndex());
			return this.getTextureIndex();
		}
	}

	@Override
	public Transformations[] getTransformationsNotImmuneTo() {
		return new Transformations[] { Transformations.VAMPIRE, Transformations.WEREWOLF };
	}

	@Override
	public Transformations getTransformation() {
		return Transformations.fromName(this.dataManager.get(TRANSFORMATION_NAME));
	}

	@Override
	public void setTransformation(Transformations transformation) {
		this.dataManager.set(TRANSFORMATION_NAME, transformation.toString());
	}

	@Override
	public void setTextureIndex(int index) {
		this.dataManager.set(TEXTURE_INDEX, index);
	}

	@Override
	public String getName() {
		if (world.isRemote) {
			return I18n.format("entity.goblintd.name");
		} else {
			Main.LOGGER.warn("The method EntityGoblinTD#getName has been called on server side.\nPath: "
					+ (new ExecutionPath()).getAll());
			return "Goblin";
		}
	}
}
