package theblockbox.huntersdream.util.helpers;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.util.Reference;

/** A utility class for all things that don't fit into the other helpers */
public class GeneralHelper {
	public static final float STANDARD_PLAYER_WIDTH = 0.6F;
	public static final float STANDARD_PLAYER_HEIGHT = 1.8F;
	/**
	 * A normal NBTTagCompound that is used if an empty non null NBTTagCompound is
	 * needed, so there won't be twenty empty instances
	 */
	public static final NBTTagCompound EMPTY_COMPOUND = new NBTTagCompound();
	public static final IntPredicate FALSE_PREDICATE = i -> false;
	// currently not used
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
			return new DataParameter<>(id, this);
		}

		@Override
		public byte[] copyValue(byte[] value) {
			return value.clone();
		}
	};
	// A mutable AxisAlignedBB that is used in #canEntityExpandHeight(Entity, float)
	// to test if the entity can change its size. Here so that we don't have to
	// create a new one every tick
	private static final AxisAlignedBB AABB = new AxisAlignedBB(BlockPos.ORIGIN);

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
			float oldWidth = player.width;
			player.width = width;
			player.height = height;

			if (player.width < oldWidth) {
				double halfWidth = width / 2.0D;
				player.setEntityBoundingBox(
						new AxisAlignedBB(player.posX - halfWidth, player.posY, player.posZ - halfWidth,
								player.posX + halfWidth, player.posY + player.height, player.posZ + halfWidth));
				return;
			}

			AxisAlignedBB aabb = player.getEntityBoundingBox();
			player.setEntityBoundingBox(new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.minX + player.width,
					aabb.minY + player.height, aabb.minZ + player.width));

			if (player.width > oldWidth && (player.ticksExisted > 1) && !player.world.isRemote) {
				player.move(MoverType.SELF, (oldWidth - player.width), 0.0D, (oldWidth - player.width));
			}
		}
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

	/**
	 * Returns true if the given entity can expand its height to the given new
	 * height without glitching through blocks
	 */
	public static boolean canEntityExpandHeight(Entity entity, float newHeight) {
		if (entity.height >= newHeight) {
			return true;
		}
		AxisAlignedBB entityAABB = entity.getEntityBoundingBox();
		AABB.minX = entityAABB.minX;
		AABB.minY = entityAABB.minY;
		AABB.minZ = entityAABB.minZ;
		AABB.maxX = entityAABB.maxX;
		AABB.maxY = AABB.minY + newHeight;
		AABB.maxZ = entityAABB.maxZ;
		return !entity.world.collidesWithAnyBlock(AABB);
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
			}
			return objects;
		} else {
			Main.getLogger().error("Couldn't load array, returning array with size 0");
			return createEmptyArrayWithSize.apply(0);
		}
	}

	public static NBTTagCompound writeEntityToNBT(EntityLivingBase toBeWritten) {
		NBTTagCompound compound = new NBTTagCompound();
		toBeWritten.writeEntityToNBT(compound);
		return compound;
	}

	/**
	 * Returns a predicate that returns true if the given itemstack matches one of
	 * the given oreDictNames (while not accounting for damage)
	 */
	public static Predicate<ItemStack> getPredicateMatchesOreDict(String... oreDictNames) {
		return stack -> {
			if (!stack.isEmpty()) {
				// need to set damage to make the oredict not care about it
				// (because some people purposely don't use OreDictionary#WILDCARD_VALUE because
				// of crafting)
				int damage = stack.getItemDamage();
				int[] ids = OreDictionary.getOreIDs(stack);
				stack.setItemDamage(damage);
				if (oreDictNames.length > 0) {
					return Stream.of(oreDictNames).mapToInt(OreDictionary::getOreID).anyMatch(i -> {
						for (int j : ids)
							if (j == i)
								return true;
						return false;
					});
				}
			}
			return false;
		};
	}

	public static boolean itemStackHasOreDict(ItemStack stack, String oreDictName) {
		if (!stack.isEmpty()) {
			// need to set damage to make the oredict not care about it
			// (because some people purposely don't use OreDictionary#WILDCARD_VALUE because
			// of crafting)
			int damage = stack.getItemDamage();
			int[] stackIDs = OreDictionary.getOreIDs(stack);
			stack.setItemDamage(damage);
			return ArrayUtils.contains(stackIDs, OreDictionary.getOreID(oreDictName));
		}
		return false;
	}

	public static boolean itemStackHasOreDicts(ItemStack stack, String[] oreDictNames) {
		if (!stack.isEmpty()) {
			// need to set damage to make the oredict not care about it
			// (because some people purposely don't use OreDictionary#WILDCARD_VALUE because
			// of crafting)
			int damage = stack.getItemDamage();
			int[] stackIDs = OreDictionary.getOreIDs(stack);
			stack.setItemDamage(damage);

			// we want to know if stackIDs has something in common with oreDictNames
			int[] oreDictIDs = Stream.of(oreDictNames).mapToInt(OreDictionary::getOreID).toArray();
			for (int i = 0; i < stackIDs.length; i++)
				if (ArrayUtils.contains(oreDictIDs, stackIDs[i]))
					return true;
		}
		return false;
	}

	public static boolean canBlockSeeSky(World world, BlockPos pos) {
		for (MutableBlockPos mbp = new MutableBlockPos(pos); mbp.getY() < world.getHeight(); mbp.setY(mbp.getY() + 1))
			if (world.getBlockState(mbp).getMaterial().isOpaque())
				return false;
		return true;
	}

	public static String firstCharToUppercase(String string) {
		if (string.isEmpty())
			return string;
		char[] chars = string.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return String.valueOf(chars);
	}

	/**
	 * Creates a new thread and then executes the given runnable in the given milli
	 * seconds
	 */
	public static void executeIn(Runnable toBeExecuted, long millis) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				toBeExecuted.run();
			}
		}.start();
	}

	public static void executeOnMainThreadIn(Runnable toBeExecuted, long millis, MinecraftServer server, String name) {
		new Thread(name) {
			@Override
			public void run() {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				server.addScheduledTask(toBeExecuted);
			}
		}.start();
	}

	public static ItemStack[] newEmptyItemStackArray(int length) {
		ItemStack[] stacks = new ItemStack[length];
		for (int i = 0; i < stacks.length; i++) {
			stacks[i] = ItemStack.EMPTY;
		}
		return stacks;
	}

	public static void drawCenteredString(FontRenderer fontRenderer, String string, int x, int width, int y,
			int color) {
		fontRenderer.drawString(string, ((width - x) - fontRenderer.getStringWidth(string)) / 2, y, color);
	}

	/** Spawns experience orbs like a vanilla furnace would do */
	public static void spawnXP(World world, BlockPos pos, int items, float xpPerItem) {
		if (!world.isRemote) {
			if (xpPerItem == 0.0F) {
				return;
			} else if (xpPerItem < 1.0F) {
				float fullXP = items * xpPerItem;
				int fullXPFloor = MathHelper.floor(fullXP);
				if (fullXPFloor < MathHelper.ceil(fullXP) && Math.random() < (fullXP - fullXPFloor)) {
					++fullXPFloor;
				}
				items = fullXPFloor;
			}
			for (int splitXP = EntityXPOrb.getXPSplit(items); items > 0; splitXP = EntityXPOrb.getXPSplit(items)) {
				items -= splitXP;
				world.spawnEntity(new EntityXPOrb(world, pos.getX(), pos.getY() + 0.5D, pos.getZ() + 0.5D, splitXP));
			}
		}
	}

	public static <T> boolean containsAll(Collection<? super T> c, T[] elements) {
		for (T element : elements)
			if (!c.contains(element))
				return false;
		return true;
	}
}
