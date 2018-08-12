package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

/** A utility class for all things that don't fit into the other helpers */
public class GeneralHelper {
	public static Side getSideFromWorld(World world) {
		return (world.isRemote ? Side.CLIENT : Side.SERVER);
	}

	public static Side getSideFromEntity(Entity entity) {
		return getSideFromWorld(entity.world);
	}

	public static Side getOtherSide(Side side) {
		if (side != null) {
			return side == Side.CLIENT ? Side.SERVER : Side.CLIENT;
		} else {
			throw new NullPointerException("The given side is null");
		}
	}
}
