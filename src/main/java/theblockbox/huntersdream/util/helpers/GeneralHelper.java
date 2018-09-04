package theblockbox.huntersdream.util.helpers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.Reference;

/** A utility class for all things that don't fit into the other helpers */
public class GeneralHelper {
	public static final float STANDARD_PLAYER_WIDTH = 0.6F;
	public static final float STANDARD_PLAYER_HEIGHT = 1.8F;
	public static final DataSerializer<byte[]> BYTE_ARRAY_DATA_SERIALIZER = new DataSerializer<byte[]>() {

		@Override
		public void write(PacketBuffer buf, byte[] value) {
			buf.writeByteArray(value);
		}

		@Override
		public byte[] read(PacketBuffer buf) throws IOException {
			return buf.readByteArray();
		}

		@Override
		public DataParameter<byte[]> createKey(int id) {
			return new DataParameter<byte[]>(id, this);
		}

		@Override
		public byte[] copyValue(byte[] value) {
			return value.clone();
		}
	};

	static {
		DataSerializers.registerSerializer(BYTE_ARRAY_DATA_SERIALIZER);
	}

	/** Returns the logical side from the given world */
	public static Side getSideFromWorld(World world) {
		return (world.isRemote ? Side.CLIENT : Side.SERVER);
	}

	/** Returns the logical side from the given entity */
	public static Side getSideFromEntity(Entity entity) {
		return getSideFromWorld(entity.world);
	}

	/**
	 * Returns the opposite side (client becomes server and server becomes client)
	 */
	public static Side getOppositeSide(Side side) {
		if (side != null) {
			return side == Side.CLIENT ? Side.SERVER : Side.CLIENT;
		} else {
			throw new NullPointerException("The given side is null");
		}
	}

	/** Returns the nearest player that is not the given entity */
	public static EntityPlayer getNearestPlayer(World world, Entity entity, double range) {
		double d0 = -1.0D;
		EntityPlayer entityplayer = null;

		for (int i = 0; i < world.playerEntities.size(); ++i) {
			EntityPlayer entityplayer1 = world.playerEntities.get(i);

			double distance = entityplayer1.getDistanceSq(entity.posX, entity.posY, entity.posZ);

			if ((range < 0.0D || distance < range * range) && (d0 == -1.0D || distance < d0) && (distance > 1.1D)) {
				d0 = distance;
				entityplayer = entityplayer1;
			}
		}

		return entityplayer;
	}

	/** Changes the player's size */
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

	/** Converts from a base (like 10) to another base (like 4) */
	public static int convertFromBaseToBase(String toConvert, int fromBase, int toBase) {
		return Integer.valueOf(Integer.toString(Integer.valueOf(toConvert, fromBase), toBase));
	}

	/**
	 * Does the same as {@link #convertFromBaseToBase(String, int, int)}, except
	 * that the first argument is an int
	 */
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

	/** Shortcut for {@code numerator / 16.0D} */
	public static double getSixteenth(double numerator) {
		return numerator / 16.0D;
	}

	/**
	 * Returns true if the given entity can expand its height to the given new
	 * height without glitching through blocks
	 */
	public static boolean canEntityExpandHeight(Entity entity, float newHeight) {
		int currentY = MathHelper.ceil(entity.posY);
		int newY = MathHelper.ceil(newHeight - entity.height) + currentY + 1;
		if (currentY >= newY)
			return true;
		else
			for (int i = currentY; i <= newY; i++)
				if (entity.world.getBlockState(new BlockPos(entity.posX, i, entity.posZ)).getMaterial().isSolid())
					return false;
		return true;
	}

	/** Combines to arrays to one array */
	public static <T> List<T> combineArraysToList(T[] array1, T[] array2) {
		List<T> arrayList = new LinkedList<>();
		for (T t : array1)
			arrayList.add(t);
		for (T t : array2)
			arrayList.add(t);
		return arrayList;
	}

	/** Returns true if the given player is not under a block */
	public static boolean playerNotUnderBlock(EntityPlayer player) {
		return player.world.canBlockSeeSky(new BlockPos(player.posX, player.posY + 1, player.posZ));
	}

	/**
	 * Returns a Predicate that returns true when the given itemstack's (see
	 * parameter toCompare) item equals the item of the itemstack that is being
	 * tested with the predicate
	 */
	public static Predicate<ItemStack> getItemStackItemsEqual(ItemStack toCompare) {
		return stack -> stack.getItem() == toCompare.getItem();
	}

	/**
	 * Writes an array to an NBTTagCompound
	 * 
	 * @param writeTo      The NBTTagCompound to which to write the array
	 * @param array        The array that should be written
	 * @param nestedNBTKey A key that hasn't been used in the NBTTagCompound yet to
	 *                     store the array
	 * @param tToString    A function that converts the given object of type T to a
	 *                     string
	 */
	public static <T> void writeArrayToNBT(NBTTagCompound writeTo, T[] array, String nestedNBTKey,
			Function<T, String> tToString) {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("length", array.length);
		for (int i = 0; i < array.length; i++) {
			compound.setString("val" + i, tToString.apply(array[i]));
		}
		writeTo.setTag(nestedNBTKey, compound);
	}

	/**
	 * Reads an array from an NBTTagCompound
	 * 
	 * @param readFrom                 The NBTTagCompound from where to read the
	 *                                 array
	 * @param nestedNBTKey             The key that has been used to save the array
	 * @param stringToT                A function that accepts a string and returns
	 *                                 an object of type T (converts string to t)
	 * @param createEmptyArrayWithSize A function to create an empty array with a
	 *                                 specific size (because you can't make generic
	 *                                 arrays in java). Just use
	 *                                 {@code ClassName[]::new} here
	 * @return Returns the read array
	 */
	public static <T> T[] readArrayFromNBT(NBTTagCompound readFrom, String nestedNBTKey, Function<String, T> stringToT,
			IntFunction<T[]> createEmptyArrayWithSize) {
		NBTTagCompound compound = (NBTTagCompound) readFrom.getTag(nestedNBTKey);
		if (compound != null) {
			T[] objects = createEmptyArrayWithSize.apply(compound.getInteger("length"));
			for (int i = 0; i < objects.length; i++) {
				objects[i] = stringToT.apply(compound.getString("val" + i));
				System.out.println(objects[i]);
			}
			return objects;
		} else {
			return createEmptyArrayWithSize.apply(0);
		}
	}
}
