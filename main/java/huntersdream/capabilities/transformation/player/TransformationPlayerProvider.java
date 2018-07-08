package huntersdream.capabilities.transformation.player;

import huntersdream.util.interfaces.ITransformationPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TransformationPlayerProvider implements ICapabilitySerializable<NBTBase> {
	@CapabilityInject(ITransformationPlayer.class)
	public static final Capability<ITransformationPlayer> TRANSFORMATION_PLAYER_CAPABILITY = null;

	public ITransformationPlayer instance = TRANSFORMATION_PLAYER_CAPABILITY.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (capability == TRANSFORMATION_PLAYER_CAPABILITY);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return capability == TRANSFORMATION_PLAYER_CAPABILITY ? TRANSFORMATION_PLAYER_CAPABILITY.<T>cast(this.instance)
				: null;
	}

	@Override
	public NBTBase serializeNBT() {
		return TRANSFORMATION_PLAYER_CAPABILITY.getStorage().writeNBT(TRANSFORMATION_PLAYER_CAPABILITY, this.instance,
				null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		TRANSFORMATION_PLAYER_CAPABILITY.getStorage().readNBT(TRANSFORMATION_PLAYER_CAPABILITY, this.instance, null,
				nbt);
	}

}
