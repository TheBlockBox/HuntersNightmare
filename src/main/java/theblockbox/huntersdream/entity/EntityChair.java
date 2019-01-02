package theblockbox.huntersdream.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityChair extends Entity {
	public int blockPosX;
	public int blockPosY;
	public int blockPosZ;

	public EntityChair(World world) {
		super(world);
		this.noClip = true;
		this.height = 0.01F;
		this.width = 0.01F;
	}

	public EntityChair(World world, double x, double y, double z, double y0ffset) {
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
        this.setPosition(x + 0.5D, y + y0ffset, z + 0.5D);
	}

	public EntityChair(World world, double x, double y, double z, double y0ffset, int rotation, double rotationOffset) {
		this(world);
		this.blockPosX = (int) x;
		this.blockPosY = (int) y;
		this.blockPosZ = (int) z;
        this.setPostionConsideringRotation(x + 0.5D, y + y0ffset, z + 0.5D, rotation, rotationOffset);
	}

	public void setPostionConsideringRotation(double x, double y, double z, int rotation, double rotationOffset) {
		switch (rotation) {
		case 2:
			z += rotationOffset;
			break;
		case 0:
			z -= rotationOffset;
			break;
		case 3:
			x -= rotationOffset;
			break;
		case 1:
			x += rotationOffset;
			break;
		}
        this.setPosition(x, y, z);
	}

	@Override
	protected boolean shouldSetPosAfterLoading() {
		return false;
	}

	@Override
	public void onEntityUpdate() {
		if (!this.world.isRemote) {
			if (!this.isBeingRidden()
					|| this.world.isAirBlock(new BlockPos(this.blockPosX, this.blockPosY, this.blockPosZ))) {
				this.setDead();
				this.world.updateComparatorOutputLevel(this.getPosition(),
						this.world.getBlockState(this.getPosition()).getBlock());
			}
		}
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("blockPosX"))
			this.blockPosX = compound.getInteger("blockPosX");
		if (compound.hasKey("blockPosY"))
			this.blockPosY = compound.getInteger("blockPosY");
		if (compound.hasKey("blockPosZ"))
			this.blockPosZ = compound.getInteger("blockPosZ");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setInteger("blockPosX", this.blockPosX);
		compound.setInteger("blockPosY", this.blockPosY);
		compound.setInteger("blockPosZ", this.blockPosZ);
	}
}