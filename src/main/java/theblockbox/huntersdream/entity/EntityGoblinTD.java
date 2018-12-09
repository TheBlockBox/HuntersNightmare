package theblockbox.huntersdream.entity;

import com.google.common.base.Predicate;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
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
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.util.ExecutionPath;
import theblockbox.huntersdream.util.helpers.ChanceHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationCreature;

public class EntityGoblinTD extends EntityVillager implements ITransformationCreature, IMob {
	/** The amount of textures available for the goblins */
	public static final byte TEXTURES = 7;
	private static final DataParameter<Integer> TEXTURE_INDEX = EntityDataManager
			.<Integer>createKey(EntityGoblinTD.class, DataSerializers.VARINT);
	private static final DataParameter<String> TRANSFORMATION_NAME = EntityDataManager
			.<String>createKey(EntityGoblinTD.class, DataSerializers.STRING);
	private static final DataParameter<Byte> GOBLIN_TEXTURE_INDEX = EntityDataManager
			.<Byte>createKey(EntityGoblinTD.class, DataSerializers.BYTE);
	private static final DataParameter<NBTTagCompound> TRANSFORMATION_DATA = EntityDataManager
			.createKey(EntityGoblinTD.class, DataSerializers.COMPOUND_TAG);

	public EntityGoblinTD(World worldIn, int textureIndex, Transformation transformation) {
		super(worldIn);
		transformation.validateIsTransformation();
		this.setSize(0.5F, 1.4F);
		this.dataManager.set(TRANSFORMATION_NAME, transformation.toString());
		this.dataManager.set(TEXTURE_INDEX, textureIndex);
		// set the texture index if the given one is invalid
		if (!this.textureIndexInBounds())
			this.setTextureIndex(this);

		NBTTagCompound transformationData = new NBTTagCompound();
		transformationData.setString("transformation", this.dataManager.get(TRANSFORMATION_NAME));
		this.setTransformationData(transformationData);
	}

	public EntityGoblinTD(World worldIn) {
		// texture index of 0 because humans don't have special textures
		this(worldIn, 0, Transformation.HUMAN);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(TEXTURE_INDEX, 0);
		this.dataManager.register(TRANSFORMATION_NAME, Transformation.HUMAN.toString());
		this.dataManager.register(GOBLIN_TEXTURE_INDEX, ChanceHelper.randomByte(this.rand, TEXTURES));

		NBTTagCompound transformationData = new NBTTagCompound();
		transformationData.setString("transformation", this.getTransformation().toString());
		this.dataManager.register(TRANSFORMATION_DATA, transformationData);

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
		Predicate<EntityCreature> predicateMob = input -> !(input instanceof EntityGoblinTD);
		this.targetTasks.addTask(3,
				new EntityAINearestAttackableTarget<>(this, EntityCreature.class, 10, true, false, predicateMob));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false,
				WerewolfHelper::isTransformed));
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

	public byte getTexture() {
		return this.dataManager.get(GOBLIN_TEXTURE_INDEX);
	}

	public void setTexture(byte texture) {
		this.dataManager.set(GOBLIN_TEXTURE_INDEX, texture);
	}

	@Override
	public EntityVillager createChild(EntityAgeable ageable) {
		return new EntityGoblinTD(this.world);
	}

	@Override
	public int getTextureIndex() {
		int textureIndex = this.dataManager.get(TEXTURE_INDEX);
		int length = this.getTransformation().getTextures().length;

		if (length > 0 && !(textureIndex >= 0 && textureIndex < length)) {
			Main.getLogger()
					.error("Tried to get a goblin's texture index and failed. Texture index: " + textureIndex
							+ " Bounds: " + this.getTransformation().getTextures().length
							+ "\nAttempting to retry...\nStacktrace: " + ExecutionPath.getAll());
			this.setTextureIndex(this);
			return this.getTextureIndex();
		} else {
			// return texture index if either length is 0 or texture index is in bounds
			return textureIndex;
		}
	}

	@Override
	public Transformation[] getTransformationsNotImmuneTo() {
		return new Transformation[] { Transformation.VAMPIRE, Transformation.WEREWOLF };
	}

	@Override
	public boolean notImmuneToTransformation(Transformation transformation) {
		return (transformation == Transformation.VAMPIRE) || (transformation == Transformation.WEREWOLF);
	}

	@Override
	public Transformation getTransformation() {
		return Transformation.fromName(this.dataManager.get(TRANSFORMATION_NAME));
	}

	@Override
	public void setTransformation(Transformation transformation) {
		transformation.validateIsTransformation();
		this.dataManager.set(TRANSFORMATION_NAME, transformation.toString());
	}

	@Override
	public void setTextureIndex(int index) {
		this.dataManager.set(TEXTURE_INDEX, index);
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
	public String getName() {
		if (this.hasCustomName()) {
			return this.getCustomNameTag();
		} else {
			if (this.world.isRemote) {
				return I18n.format("entity.goblintd.name");
			} else {
				return "Goblin";
			}
		}
	}

	// now shows "Goblin" instead of the profession
	@Override
	public ITextComponent getDisplayName() {
		Team team = this.getTeam();
		String nameTag = this.getCustomNameTag();
		if (nameTag != null && !nameTag.isEmpty()) {
			TextComponentString textcomponentstring = new TextComponentString(
					ScorePlayerTeam.formatPlayerName(team, nameTag));
			textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent())
					.setInsertion(this.getCachedUniqueIdString());
			return textcomponentstring;
		} else {
			ITextComponent tc = new TextComponentTranslation("entity.goblintd.name");
			tc.getStyle().setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString());
			if (team != null) {
				tc.getStyle().setColor(team.getColor());
			}
			return tc;
		}
	}

	/**
	 * Attacks a given entity (needed so that goblin can attack). Method copied from
	 * {@link net.minecraft.entity.monster.EntityMob#attackEntityAsMob(Entity)}
	 * 
	 * @param entityIn The entity to be attacked
	 */
	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int knockback = 0;
		if (entityIn instanceof EntityLivingBase) {
			damage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
					((EntityLivingBase) entityIn).getCreatureAttribute());
			knockback += EnchantmentHelper.getKnockbackModifier(this);
		}
		if (entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage)) {
			if (knockback > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).knockBack(this, knockback * 0.5F,
						MathHelper.sin(this.rotationYaw * 0.017453292F),
						(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int fireaspect = EnchantmentHelper.getFireAspectModifier(this);
			if (fireaspect > 0) {
				entityIn.setFire(fireaspect * 4);
			}
			if (entityIn instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entityIn;
				ItemStack heldItemStack = this.getHeldItemMainhand();

				if (!heldItemStack.isEmpty() && !this.activeItemStack.isEmpty()
						&& heldItemStack.getItem().canDisableShield(heldItemStack, this.activeItemStack, player, this)
						&& this.activeItemStack.getItem().isShield(this.activeItemStack, player)) {
					float f1 = 0.25F + EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1) {
						player.getCooldownTracker().setCooldown(this.activeItemStack.getItem(), 100);
						this.world.setEntityState(player, (byte) 30);
					}
				}
			}
			this.applyEnchantments(this, entityIn);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setByte("goblinTextureIndex", this.getTexture());
		compound.setInteger("transformedTextureIndex", this.getTextureIndex());
		compound.setString("transformationName", this.dataManager.get(TRANSFORMATION_NAME));
		compound.setTag("transformationData", this.getTransformationData());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("goblinTextureIndex"))
			this.setTexture(compound.getByte("goblinTextureIndex"));
		if (compound.hasKey("transformedTextureIndex"))
			this.setTextureIndex(compound.getInteger("transformedTextureIndex"));
		if (compound.hasKey("transformationName"))
			this.dataManager.set(TRANSFORMATION_NAME, compound.getString("transformationName"));
		if (compound.hasKey("transformationData"))
			this.setTransformationData((NBTTagCompound) compound.getTag("transformationData"));
	}
}
