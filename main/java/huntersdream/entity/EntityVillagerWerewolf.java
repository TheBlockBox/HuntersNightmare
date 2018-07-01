package huntersdream.entity;

import java.util.Random;

import huntersdream.entity.renderer.RenderVillagerWerewolf;
import huntersdream.util.handlers.WerewolfEventHandler;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;

public class EntityVillagerWerewolf extends EntityVillager {
	/**
	 * used to determine the colour of the werewolf form
	 * {@link RenderVillagerWerewolf}
	 */
	public final int TEXTURE_INDEX;

	public EntityVillagerWerewolf(World worldIn) {
		super(worldIn);
		NBTTagCompound nbt = this.getEntityData();
		nbt.setBoolean("isWerewolf", true);
		nbt.setBoolean("isTransformed", false);
		this.TEXTURE_INDEX = (new Random()).nextInt(RenderVillagerWerewolf.TEXTURE_TRANSFORMED.length);
		setProfession(5);
	}

	// @Override
	// protected void initEntityAI() {}

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
			player.sendMessage(new TextComponentTranslation("entity.werewolfVillager.onClickMessage", new Object[0]));
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
		if (WerewolfEventHandler.isWerewolfTime(world)) {
			if (getEntityData().getBoolean("isWerewolf")) {
				getEntityData().setBoolean("isTransformed", true);
			}
		} else {
			if (getEntityData().getBoolean("isWerewolf") && getEntityData().getBoolean("isTransformed")
					&& (!WerewolfEventHandler.isWerewolfTime(world))) {
				getEntityData().setBoolean("isTransformed", false);
				System.out.println("This message shouldn't appear on full moon");
			}
		}
	}
}
