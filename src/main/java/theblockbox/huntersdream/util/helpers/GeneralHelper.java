package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
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

	public static void setEntitySize(Entity entity, float width, float height) {

		if (width != entity.width || height != entity.height) {
			float f = entity.width;
			entity.width = width;
			entity.height = height;

			if (entity.width < f) {
				double d0 = (double) width / 2.0D;
				entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0,
						entity.posX + d0, entity.posY + (double) entity.height, entity.posZ + d0));
				return;
			}

			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
			entity.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
					axisalignedbb.minX + (double) entity.width, axisalignedbb.minY + (double) entity.height,
					axisalignedbb.minZ + (double) entity.width));

			if (entity.width > f && !entityFirstUpdate(entity) && !entity.world.isRemote) {
				entity.move(MoverType.SELF, (double) (f - entity.width), 0.0D, (double) (f - entity.width));
			}
		}
	}

	public static boolean entityFirstUpdate(Entity entity) {
		return entity.ticksExisted > 1;
	}
}
