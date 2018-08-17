package theblockbox.huntersdream.util.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.Reference;

/** A utility class for all things that don't fit into the other helpers */
public class GeneralHelper {
	public static final float STANDARD_PLAYER_WIDTH = 0.6F;
	public static final float STANDARD_PLAYER_HEIGHT = 1.8F;

	public static Side getSideFromWorld(World world) {
		return (world.isRemote ? Side.CLIENT : Side.SERVER);
	}

	public static Side getSideFromEntity(Entity entity) {
		return getSideFromWorld(entity.world);
	}

	public static Side getOppositeSide(Side side) {
		if (side != null) {
			return side == Side.CLIENT ? Side.SERVER : Side.CLIENT;
		} else {
			throw new NullPointerException("The given side is null");
		}
	}

	public static EntityPlayer getNearestPlayer(World world, Entity entity, double range) {
		double d0 = -1.0D;
		EntityPlayer entityplayer = null;

		for (int i = 0; i < world.playerEntities.size(); ++i) {
			EntityPlayer entityplayer1 = world.playerEntities.get(i);

			double distance = entityplayer1.getDistanceSq(entity.posX, entity.posY, entity.posZ);

			if ((range < 0.0D || distance < range * range) && (d0 == -1.0D || distance < d0) && (distance > 1.1)) {
				d0 = distance;
				entityplayer = entityplayer1;
			}
		}

		return entityplayer;
	}

	public static void changePlayerSize(EntityPlayer player, float width, float height) {
		if (width != player.width || height != player.height) {
			float f = player.width;
			player.width = width;
			player.height = height;
			if (player.width < f) {
				double d0 = (double) width / 2.0D;
				player.setEntityBoundingBox(new AxisAlignedBB(player.posX - d0, player.posY, player.posZ - d0,
						player.posX + d0, player.posY + (double) player.height, player.posZ + d0));
				return;
			}
			AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
			player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
					axisalignedbb.minX + (double) player.width, axisalignedbb.minY + (double) player.height,
					axisalignedbb.minZ + (double) player.width));
			if (player.width > f && !player.world.isRemote) {
				player.move(MoverType.SELF, (double) (f - player.width), 0.0D, (double) (f - player.width));
			}
		}
	}

	public static int convertFromBaseToBase(String toConvert, int fromBase, int toBase) {
		return Integer.valueOf(Integer.toString(Integer.valueOf(toConvert, fromBase), toBase));
	}

	public static int convertFromBaseToBase(int toConvert, int fromBase, int toBase) {
		return convertFromBaseToBase(String.valueOf(toConvert), fromBase, toBase);
	}

	/**
	 * Shortcut method for creating a resource location. If a mod id is not given,
	 * {@link Reference#MODID} will be used
	 */
	public static ResourceLocation newResLoc(String resourcePath) {
		if (resourcePath.contains(":"))
			return new ResourceLocation(resourcePath);
		else
			return new ResourceLocation(Reference.MODID, resourcePath);
	}
}
