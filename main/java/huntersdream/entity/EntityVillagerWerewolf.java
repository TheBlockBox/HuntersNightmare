package huntersdream.entity;

import java.util.Random;

import huntersdream.entity.model.ModelWolfman;
import huntersdream.entity.renderer.RenderVillagerWerewolf;
import huntersdream.util.helpers.TransformationHelper;
import huntersdream.util.helpers.TransformationHelper.Transformations;
import huntersdream.util.helpers.WerewolfHelper;
import huntersdream.util.interfaces.ITransformation;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityVillagerWerewolf extends EntityVillager implements ITransformation, IEntityAdditionalSpawnData {
	/**
	 * used to determine the colour of the werewolf form
	 * {@link RenderVillagerWerewolf}
	 */
	public static final DataParameter<Integer> TRANSFORMATION = EntityDataManager
			.<Integer>createKey(EntityVillagerWerewolf.class, DataSerializers.VARINT);
	public static final DataParameter<Boolean> TRANSFORMED = EntityDataManager
			.<Boolean>createKey(EntityVillagerWerewolf.class, DataSerializers.BOOLEAN);
	private int textureIndex;
	public final EntityAIAttackMelee ATTACK_MELEE = new EntityAIAttackMelee(this, 0.8D, false);

	public EntityVillagerWerewolf(World worldIn) {
		super(worldIn);
		setProfession(5);
		textureIndex = (new Random()).nextInt(ModelWolfman.TEXTURE_TRANSFORMED.length);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TRANSFORMATION, TransformationHelper.Transformations.WEREWOLF.ID);
		this.dataManager.register(TRANSFORMED, false);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAvoidEntity<EntityZombie>(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
		this.tasks.addTask(1, new EntityAIAvoidEntity<EntityEvoker>(this, EntityEvoker.class, 12.0F, 0.8D, 0.8D));
		this.tasks.addTask(1,
				new EntityAIAvoidEntity<EntityVindicator>(this, EntityVindicator.class, 8.0F, 0.8D, 0.8D));
		this.tasks.addTask(1, new EntityAIAvoidEntity<EntityVex>(this, EntityVex.class, 8.0F, 0.6D, 0.6D));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
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
	public EntityVillager createChild(EntityAgeable ageable) {
		return null;
	}

	@Override
	public int getProfession() {
		return 5;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (player.world.isRemote) {
			player.sendMessage(new TextComponentTranslation("entity.villagerWerewolf.onClickMessage", new Object[0]));
			world.playSound(player, getPos(), SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.NEUTRAL, 10000, 1);
			return true;
		}
		return false;
	}

	@Override
	public boolean isMating() {
		return false;
	}

	@Override
	public EntityPlayer getCustomer() {
		return null;
	}

	@Override
	public boolean isTrading() {
		return false;
	}

	@Override
	public boolean getIsWillingToMate(boolean updateFirst) {
		return false;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		return null;
	}

	@Override
	public boolean isChild() {
		return false;
	}

	@Override
	public InventoryBasic getVillagerInventory() {
		return null;
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (ticksExisted % 100 == 0) {
			if (!world.isRemote) {
				if (WerewolfHelper.isWerewolfTime(this)) {
					if (getTransformation() == Transformations.WEREWOLF) {
						setTransformed(true);
					}
				} else {
					if ((getTransformation() == Transformations.WEREWOLF) && transformed()
							&& (!WerewolfHelper.isWerewolfTime(this))) {
						setTransformed(false);
					}
				}
				if (!transformed()) {
					this.targetTasks.removeTask(ATTACK_MELEE);
				} else if (transformed()) {
					this.tasks.addTask(15, ATTACK_MELEE);

				}
			}
		}
	}

	// TODO: Add weakness for silver tools
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("transformed", dataManager.get(TRANSFORMED));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setTransformed(compound.getBoolean("transformed"));
	}

	@Override
	public boolean transformed() {
		return dataManager.<Boolean>get(TRANSFORMED);
	}

	@Override
	public int getTransformationInt() {
		return dataManager.<Integer>get(TRANSFORMATION);
	}

	@Override
	public void setTransformationID(int id) {
		// just does nothing
	}

	@Override
	public void setTransformed(boolean transformed) {
		this.dataManager.set(TRANSFORMED, transformed);
		dataManager.setDirty(TRANSFORMED);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(textureIndex);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.textureIndex = additionalData.readInt();
	}

	public int getTextureIndex() {
		return textureIndex;
	}

}
