package theblockbox.huntersdream.api.helpers;

import com.google.common.base.CaseFormat;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.init.StructureInit;
import theblockbox.huntersdream.api.interfaces.IAmmunition;
import theblockbox.huntersdream.api.interfaces.IGun;
import theblockbox.huntersdream.entity.EntityWerewolf;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.exceptions.UnexpectedBehaviorException;
import theblockbox.huntersdream.util.handlers.ConfigHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A utility class for all things that don't fit into the other helpers
 */
public class GeneralHelper {
    public static final float STANDARD_PLAYER_WIDTH = 0.6F;
    public static final float STANDARD_PLAYER_HEIGHT = 1.8F;
    /**
     * A normal NBTTagCompound that is used if an empty non null NBTTagCompound is
     * needed, so there won't be twenty empty instances
     */
    public static final NBTTagCompound EMPTY_COMPOUND = new NBTTagCompound();
    public static final IntPredicate FALSE_PREDICATE = i -> false;
    /**
     * An array of all possible hands (currently {@link EnumHand#MAIN_HAND} and {@link EnumHand#OFF_HAND}).
     */
    public static final EnumHand[] HANDS = {EnumHand.MAIN_HAND, EnumHand.OFF_HAND};
    public static final ITemplateProcessor CHEST_TEMPLATE_PROCESSOR = (world, pos, blockInfo) -> {
        if (blockInfo.blockState.getBlock() instanceof BlockChest) {
            TileEntityChest tileEntity = new TileEntityChest();
            world.rand.setSeed(world.rand.nextLong());
            tileEntity.setLootTable(LootTableList.CHESTS_VILLAGE_BLACKSMITH, world.rand.nextLong());
            return new Template.BlockInfo(pos, blockInfo.blockState, tileEntity.writeToNBT(new NBTTagCompound()));
        } else {
            return blockInfo;
        }
    };
    // A mutable AxisAlignedBB that is used in #canEntityExpandHeight(Entity, float)
    // to test if the entity can change its size. Here so that we don't have to
    // create a new one every tick
    private static final AxisAlignedBB AABB = new AxisAlignedBB(BlockPos.ORIGIN);

    /**
     * Returns the logical side from the given world
     */
    public static Side getSideFromWorld(World world) {
        return (world.isRemote ? Side.CLIENT : Side.SERVER);
    }

    /**
     * Returns the logical side from the given entity
     */
    public static Side getSideFromEntity(Entity entity) {
        return GeneralHelper.getSideFromWorld(entity.world);
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

    /**
     * Returns the nearest player that is not the given entity
     */
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

    /**
     * Changes the player's size
     */
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
        GeneralHelper.AABB.minX = entityAABB.minX;
        GeneralHelper.AABB.minY = entityAABB.minY;
        GeneralHelper.AABB.minZ = entityAABB.minZ;
        GeneralHelper.AABB.maxX = entityAABB.maxX;
        GeneralHelper.AABB.maxY = GeneralHelper.AABB.minY + newHeight;
        GeneralHelper.AABB.maxZ = entityAABB.maxZ;
        return !entity.world.collidesWithAnyBlock(GeneralHelper.AABB);
    }

    /**
     * Returns true if the given player is not under a block
     */
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
                // (since some people purposely don't use OreDictionary#WILDCARD_VALUE because
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
            for (int stackID : stackIDs)
                if (ArrayUtils.contains(oreDictIDs, stackID))
                    return true;
        }
        return false;
    }

    public static boolean canBlockSeeSky(World world, BlockPos pos) {
        for (BlockPos.MutableBlockPos mbp = new BlockPos.MutableBlockPos(pos); mbp.getY() < world.getHeight(); mbp.setY(mbp.getY() + 1))
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

    /**
     * Spawns experience orbs like a vanilla furnace would do
     */
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

    /**
     * Out of the two numbers a and b, the method returns the closest number to numberToCheck
     */
    public static double getClosest(double numberToCheck, double a, double b) {
        return (Math.abs(numberToCheck - a) < Math.abs(numberToCheck - b)) ? a : b;
    }

    /**
     * Out of the two numbers a and b, the method returns the closest number to numberToCheck
     */
    public static int getClosest(int numberToCheck, int a, int b) {
        return (Math.abs(numberToCheck - a) < Math.abs(numberToCheck - b)) ? a : b;
    }

    /**
     * Returns true when a is closer to numberToCheck than b, otherwise returns false.
     */
    public static boolean isACloserThanB(double numberToCheck, double a, double b) {
        return Math.abs(numberToCheck - a) < Math.abs(numberToCheck - b);
    }

    /**
     * Returns true when a is closer to numberToCheck than b, otherwise returns false.
     */
    public static boolean isACloserThanB(int numberToCheck, int a, int b) {
        return Math.abs(numberToCheck - a) < Math.abs(numberToCheck - b);
    }

    /**
     * Tries to get the element at the given index in the given list
     * without throwing an {@link ArrayIndexOutOfBoundsException}.
     * If no element can be found at the given index, null will
     * be returned.
     */
    @Nullable
    public static <E> E safeGet(List<E> list, int index) {
        return (list.size() > index) ? list.get(index) : null;
    }

    /**
     * Tries to safely set the given element at the given index in the given
     * list without causing an ArrayIndexOutOfBoundsException. Only works
     * with automatically resizing lists (like {@link java.util.ArrayList})
     */
    public static <E> void safeSet(List<E> list, int index, E element) {
        int listSize = list.size();
        if (listSize > index) {
            list.set(index, element);
        } else {
            int neededGrowing = index - listSize;
            for (int i = 0; i < neededGrowing; i++) {
                list.add(null);
            }
            list.add(element);
        }
    }

    public static EnumParticleTypes addParticle(ResourceLocation registryName, boolean shouldIgnoreRange) {
        String name = registryName.toString();
        EnumParticleTypes particle = EnumHelper.addEnum(EnumParticleTypes.class, CaseFormat.LOWER_UNDERSCORE
                        .to(CaseFormat.UPPER_UNDERSCORE, name), new Class[]{String.class, int.class, boolean.class},
                name, EnumParticleTypes.values().length, false);
        if (particle == null) {
            throw new UnexpectedBehaviorException("Couldn't register particle ");
        } else {
            // using AT to access private EnumParticleTypes#PARTICLES and EnumParticleTypes#BY_NAME
            EnumParticleTypes.PARTICLES.put(particle.getParticleID(), particle);
            EnumParticleTypes.BY_NAME.put(particle.getParticleName(), particle);
            return particle;
        }
    }

    public static void addHeartsToPlayer(EntityPlayer player, double extraHalfHearts) {
        IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        attribute.setBaseValue(attribute.getBaseValue() + extraHalfHearts);
    }

    /**
     * Gets a y coordinate for where the structure should be spawned. Returns -1 if none one was found.
     */
    public static int getYForStructure(World world, int x, int z, int structureSizeX, int structureSizeZ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, 60, z);
        IBlockState topBlockState = world.getBiome(pos).topBlock;
        for (int y = 100; y >= 40; --y) {
            if (GeneralHelper.doBlocksFit(world, pos.setPos(x, y, z), structureSizeX, structureSizeZ, topBlockState)) {
                return y + 1;
            }
        }
        return -1;
    }

    private static boolean doBlocksFit(World world, BlockPos pos, int sizeX, int sizeZ, IBlockState state) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = pos.getX(); x <= pos.getX() + sizeX; ++x) {
            for (int z = pos.getZ(); z <= pos.getZ() + sizeZ; ++z) {
                if (world.getBlockState(mutablePos.setPos(x, pos.getY(), z)) != state) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean trySpawnStructure(ResourceLocation location, World world, int chunkX, int chunkZ, Random random) {
        Template template = world.getSaveHandler().getStructureTemplateManager().getTemplate(world.getMinecraftServer(), location);
        int sizeX = template.getSize().getX();
        int sizeZ = template.getSize().getZ();
        int x = (chunkX * 16) - sizeX + 15;
        int z = (chunkZ * 16) - sizeZ + 15;
        int y = GeneralHelper.getYForStructure(world, x, z, sizeX, sizeZ);
        if (y != -1) {
            BlockPos pos = new BlockPos(x, y, z);
            if (ConfigHandler.server.logStructureSpawns) {
                Main.getLogger().info("Spawned structure {} at the coordinates [{}, {}, {}]", location, pos.getX(), pos.getY(), pos.getZ());
            }
            if (location == StructureInit.WEREWOLF_CABIN) {
                EntityWerewolf werewolf = new EntityWerewolf(world);
                werewolf.setPosition(pos.getX() + (sizeX / 2), pos.getY() + 1, pos.getZ() + (sizeZ / 2));
                world.spawnEntity(werewolf);
            }
            template.addBlocksToWorld(world, pos, GeneralHelper.CHEST_TEMPLATE_PROCESSOR, new PlacementSettings(), 2);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if it's currently an "actual night" in the given world. Does not return true when it's raining
     * but not night. Only returns true if the world time is greater than 12540 and less than 23459.
     */
    public static boolean isNight(World world) {
        long worldTime = world.getWorldTime();
        // 23459 and 12540 are the exact times between which it is day (according to World#isDaytime)
        return WerewolfHelper.isFullmoon(world) && ((worldTime > 12540L) && (worldTime < 23459L));
    }

    /**
     * Returns true if the first given object equals any of the objects in the array via comparison by
     * {@link Object#equals(Object)}. Does not throw any exception when the object is null, though it'll throw one
     * when the array is null.
     */
    public static boolean equalsAny(Object object, Object... objects) {
        if (object == null) {
            for (Object o : objects)
                if (o == null)
                    return true;
        } else {
            for (Object o : objects)
                if (object.equals(o))
                    return true;
        }
        return false;
    }

    /**
     * Returns a stack of ammunition working for the given gun by going through the offhand, the hotbar from left to
     * right, the first inventory row, the second one, the third one and finally the armor slots until any working
     * ammunition is found. If so, the (uncopied) stack is returned, otherwise, {@link ItemStack#EMPTY} will be returned.
     */
    public static ItemStack getAmmunitionStackForWeapon(EntityPlayer player, ItemStack gun, boolean allowsArrows) {
        if (gun.getItem() instanceof IGun) {
            IAmmunition.AmmunitionType[] ammunitionTypes = ((IGun) gun.getItem()).getAllowedAmmunitionTypes();
            return Stream.of(player.inventory.offHandInventory, player.inventory.mainInventory, player.inventory.armorInventory)
                    .flatMap(Collection::stream).filter(stack -> {
                        if (allowsArrows && (GeneralHelper.equalsAny(stack.getItem(), Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW)))
                            return true;
                        if (stack.getItem() instanceof IAmmunition)
                            for (IAmmunition.AmmunitionType gunType : ammunitionTypes)
                                for (IAmmunition.AmmunitionType itemType : ((IAmmunition) stack.getItem()).getAmmunitionTypes())
                                    if (gunType == itemType)
                                        return true;
                        return false;
                    }).findFirst().orElse(ItemStack.EMPTY);
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Tries to get the tag compound from the given {@link ItemStack}. If it doesn't have a tag compound, a new one is
     * created and returned.
     */
    public static NBTTagCompound getTagCompoundFromItemStack(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    /**
     * Converts the given {@link EnumHandSide} into an {@link EnumHand} via the method {@link EntityLivingBase#getPrimaryHand()}
     * of the given entity.
     */
    public static EnumHand convertEnumHandSide(EnumHandSide handSide, EntityLivingBase entity) {
        return (entity.getPrimaryHand() == handSide) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
    }

    /**
     * Converts the given {@link EnumHand} into an {@link EnumHandSide} via the method {@link EntityLivingBase#getPrimaryHand()}
     * of the given entity.
     */
    public static EnumHandSide convertEnumHand(EnumHand hand, EntityLivingBase entity) {
        return (hand == EnumHand.MAIN_HAND) ? entity.getPrimaryHand() : entity.getPrimaryHand().opposite();
    }

    public static boolean isBonemeal(ItemStack stack) {
        return (stack.getItem() instanceof ItemDye) && (EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.WHITE);
    }

    public static boolean isCotton(ItemStack stack) {
        return GeneralHelper.itemStackHasOreDict(stack, "cotton") || GeneralHelper.itemStackHasOreDict(stack, "cropCotton");
    }
}
