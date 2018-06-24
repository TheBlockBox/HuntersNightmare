package huntersdream.entity;

import java.util.Random;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityGoblinTD extends EntityVillager implements IEntityAdditionalSpawnData {
	private int textureIndex;
	/** The amount of textures available for the goblins */
	public static final int TEXTURES = 2;

	public EntityGoblinTD(World worldIn) {
		super(worldIn);
		this.setSize(0.5F, 1.4F);
		this.textureIndex = (new Random()).nextInt(TEXTURES);
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
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.textureIndex);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		this.textureIndex = buffer.readInt();
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	@Override
	public EntityVillager createChild(EntityAgeable ageable) {
		return new EntityGoblinTD(world);
	}
}
