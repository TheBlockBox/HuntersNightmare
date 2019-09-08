package theblockbox.huntersdream.api.init;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import theblockbox.huntersdream.blocks.BlockTent;

/**
 * Init class for {@link net.minecraft.block.properties.IProperty}s so that these are accessible in the api.
 */
public class PropertyInit {
    /**
     * Used in multiple classes
     */
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockCampfire}
     */
    public static final PropertyBool CAMPFIRE_BURNING = PropertyBool.create("burning");

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockCotton}
     */
    public static final PropertyInteger COTTON_AGE = PropertyInteger.create("age", 0, 3);

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockGarland}
     */
    public static final PropertyBool GARLAND_NORTH = PropertyBool.create("north");

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockGarland}
     */
    public static final PropertyBool GARLAND_SOUTH = PropertyBool.create("south");

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockGarland}
     */
    public static final PropertyBool GARLAND_WEST = PropertyBool.create("west");

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockGarland}
     */
    public static final PropertyBool GARLAND_EAST = PropertyBool.create("east");

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockGarland}
     */
    public static final PropertyBool[] GARLAND_PROPERTIES = {PropertyInit.GARLAND_NORTH, PropertyInit.GARLAND_SOUTH, PropertyInit.GARLAND_WEST, PropertyInit.GARLAND_EAST};

    /**
     * Used in {@link theblockbox.huntersdream.blocks.BlockHealingHerb}
     */
    public static final PropertyInteger HEALING_HERB_AGE = PropertyInteger.create("age", 0, 3);

    /**
     * Used in {@link BlockTent}
     */
    public static final PropertyBool TENT_OCCUPIED = PropertyBool.create("occupied");

    /**
     * Used in {@link BlockTent}
     */
    public static final PropertyEnum<BlockTent.EnumPartType> TENT_PART = PropertyEnum.create("part", BlockTent.EnumPartType.class);
}
