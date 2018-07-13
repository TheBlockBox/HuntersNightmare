package pixeleyestudios.huntersdream.entity;

import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import pixeleyestudios.huntersdream.entity.model.ModelWolfman;
import pixeleyestudios.huntersdream.entity.renderer.RenderWerewolf;
import pixeleyestudios.huntersdream.util.helpers.TransformationHelper.Transformations;
import pixeleyestudios.huntersdream.util.helpers.WerewolfHelper;
import pixeleyestudios.huntersdream.util.interfaces.ITransformation;

/**
 * The villager that is a werewolf but not transformed
 */
public class EntityWerewolfVillager extends EntityVillager implements ITransformation, IEntityAdditionalSpawnData {
	/**
	 * used to determine the colour of the werewolf form {@link RenderWerewolf}
	 */
	private int textureIndex;

	public EntityWerewolfVillager(World worldIn) {
		super(worldIn);
		setProfession(5);
		textureIndex = (new Random()).nextInt(ModelWolfman.TEXTURE_TRANSFORMED.length);
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
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setTransformed(compound.getBoolean("transformed"));
	}

	@Override
	public boolean transformed() {
		return false;
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
