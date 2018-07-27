package pixeleyestudios.huntersdream.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import pixeleyestudios.huntersdream.entity.renderer.RenderWerewolf;
import pixeleyestudios.huntersdream.util.exceptions.WrongTransformationException;
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
		textureIndex = rand.nextInt(Transformations.WEREWOLF.TEXTURES.length);
	}

	public EntityWerewolfVillager(World worldIn, int textureIndex, Transformations transformation) {
		super(worldIn);
		setProfession(5);
		this.textureIndex = textureIndex;
		if (transformation != Transformations.WEREWOLF) {
			throw new WrongTransformationException("EntityWerewolfVillager only accepts WEREWOLF transformation",
					transformation);
		}
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
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation("entity.werewolf.name");
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (player.world.isRemote) {
			player.sendMessage(new TextComponentTranslation("entity.werewolfvillager.onClickMessage", new Object[0]));
			playSound(SoundEvents.ENTITY_VILLAGER_NO, 10000, 1);
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
		if (ticksExisted % 40 == 0) {
			if (!world.isRemote) {
				if (WerewolfHelper.isWerewolfTime(this)) {
					EntityWerewolf werewolf = new EntityWerewolf(world, textureIndex, getClass().getName());
					werewolf.setPosition(posX, posY, posZ);
					world.removeEntity(this);
					world.spawnEntity(werewolf);
				}
			}
		}
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
		throw new UnsupportedOperationException("Entity is always not transformed");
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(textureIndex);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.textureIndex = additionalData.readInt();
	}

	@Override
	public int getTextureIndex() {
		return textureIndex;
	}

}
