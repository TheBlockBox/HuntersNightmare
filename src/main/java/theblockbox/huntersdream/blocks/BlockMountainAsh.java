package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

import javax.annotation.Nullable;
import java.util.List;

public class BlockMountainAsh extends Block {
    public static final AxisAlignedBB[] MOUNTAIN_ASH_AABBS = {
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.8125D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.1875D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.1875D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 0.8125D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D)};
    public static final PropertyEnum<BlockMountainAsh.EnumAttachPosition> NORTH =
            PropertyEnum.create("north", BlockMountainAsh.EnumAttachPosition.class);
    public static final PropertyEnum<BlockMountainAsh.EnumAttachPosition> SOUTH =
            PropertyEnum.create("south", BlockMountainAsh.EnumAttachPosition.class);
    public static final PropertyEnum<BlockMountainAsh.EnumAttachPosition> WEST =
            PropertyEnum.create("west", BlockMountainAsh.EnumAttachPosition.class);
    public static final PropertyEnum<BlockMountainAsh.EnumAttachPosition> EAST =
            PropertyEnum.create("east", BlockMountainAsh.EnumAttachPosition.class);
    public static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, -5.0D, 0.0D,
            1.0D, 5.0D, 1.0D);

    public BlockMountainAsh() {
        // TODO: Better material and soundtype?
        super(Material.PLANTS);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
        this.setDefaultState(this.getDefaultState().withProperty(BlockMountainAsh.NORTH, BlockMountainAsh.EnumAttachPosition.NONE)
                .withProperty(BlockMountainAsh.SOUTH, BlockMountainAsh.EnumAttachPosition.NONE)
                .withProperty(BlockMountainAsh.WEST, BlockMountainAsh.EnumAttachPosition.NONE)
                .withProperty(BlockMountainAsh.EAST, BlockMountainAsh.EnumAttachPosition.NONE));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockMountainAsh.NORTH, BlockMountainAsh.EAST,
                BlockMountainAsh.SOUTH, BlockMountainAsh.WEST);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        // we don't need any meta since all our properties are only used in #getActualState, so we'll just return 0 here
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(BlockMountainAsh.WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST))
                .withProperty(BlockMountainAsh.EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST))
                .withProperty(BlockMountainAsh.NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH))
                .withProperty(BlockMountainAsh.SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
    }

    private BlockMountainAsh.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
        BlockPos blockPos = pos.offset(facing);
        IBlockState state = worldIn.getBlockState(blockPos);
        if (!this.canConnectTo(worldIn, blockPos) && (state.isNormalCube() || !this.canConnectTo(worldIn, blockPos.down()))) {
            if (!worldIn.getBlockState(pos.up()).isNormalCube() && this.canConnectTo(worldIn, blockPos.up())
                    && (state.isSideSolid(worldIn, blockPos, EnumFacing.UP) || state.getBlock() == Blocks.GLOWSTONE)) {
                return state.isBlockNormalCube() ? BlockMountainAsh.EnumAttachPosition.UP
                        : BlockMountainAsh.EnumAttachPosition.SIDE;
            }
            return BlockMountainAsh.EnumAttachPosition.NONE;
        } else {
            return BlockMountainAsh.EnumAttachPosition.SIDE;
        }
    }

    private boolean canConnectTo(IBlockAccess blockAccess, BlockPos pos) {
        return blockAccess.getBlockState(pos).getBlock() == this;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180:
                return state.withProperty(BlockMountainAsh.NORTH, state.getValue(BlockMountainAsh.SOUTH))
                        .withProperty(BlockMountainAsh.EAST, state.getValue(BlockMountainAsh.WEST))
                        .withProperty(BlockMountainAsh.SOUTH, state.getValue(BlockMountainAsh.NORTH))
                        .withProperty(BlockMountainAsh.WEST, state.getValue(BlockMountainAsh.EAST));
            case COUNTERCLOCKWISE_90:
                return state.withProperty(BlockMountainAsh.NORTH, state.getValue(BlockMountainAsh.EAST))
                        .withProperty(BlockMountainAsh.EAST, state.getValue(BlockMountainAsh.SOUTH))
                        .withProperty(BlockMountainAsh.SOUTH, state.getValue(BlockMountainAsh.WEST))
                        .withProperty(BlockMountainAsh.WEST, state.getValue(BlockMountainAsh.NORTH));
            case CLOCKWISE_90:
                return state.withProperty(BlockMountainAsh.NORTH, state.getValue(BlockMountainAsh.WEST))
                        .withProperty(BlockMountainAsh.EAST, state.getValue(BlockMountainAsh.NORTH))
                        .withProperty(BlockMountainAsh.SOUTH, state.getValue(BlockMountainAsh.EAST))
                        .withProperty(BlockMountainAsh.WEST, state.getValue(BlockMountainAsh.SOUTH));
            default:
                return state;
        }
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT:
                return state.withProperty(BlockMountainAsh.NORTH, state.getValue(BlockMountainAsh.SOUTH))
                        .withProperty(BlockMountainAsh.SOUTH, state.getValue(BlockMountainAsh.NORTH));
            case FRONT_BACK:
                return state.withProperty(BlockMountainAsh.EAST, state.getValue(BlockMountainAsh.WEST))
                        .withProperty(BlockMountainAsh.WEST, state.getValue(BlockMountainAsh.EAST));
            default:
                return super.withMirror(state, mirrorIn);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        // TODO: Needed?
        if (!worldIn.isRemote) {
            for (EnumFacing enumfacing : EnumFacing.Plane.VERTICAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote && !this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int i = 0;
        boolean hasNorth = state.getValue(BlockMountainAsh.NORTH) != BlockMountainAsh.EnumAttachPosition.NONE;
        boolean hasSouth = state.getValue(BlockMountainAsh.SOUTH) != BlockMountainAsh.EnumAttachPosition.NONE;
        boolean hasWest = state.getValue(BlockMountainAsh.WEST) != BlockMountainAsh.EnumAttachPosition.NONE;
        boolean hasEast = state.getValue(BlockMountainAsh.EAST) != BlockMountainAsh.EnumAttachPosition.NONE;

        if (hasSouth || (hasNorth && !hasEast && !hasWest))
            i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
        if (hasNorth || (hasSouth && !hasEast && !hasWest))
            i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
        if (hasEast || (hasWest && !hasNorth && !hasSouth))
            i |= 1 << EnumFacing.EAST.getHorizontalIndex();
        if (hasWest || (hasEast && !hasNorth && !hasSouth))
            i |= 1 << EnumFacing.WEST.getHorizontalIndex();

        return BlockMountainAsh.MOUNTAIN_ASH_AABBS[i];
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        // TODO: Check if they're physical supernaturals and not only supernaturals
        if ((entityIn instanceof EntityLivingBase) && TransformationHelper.getTransformation((EntityLivingBase) entityIn)
                .isSupernatural()) {
            AxisAlignedBB boundingBox = BlockMountainAsh.COLLISION_AABB.offset(pos);
            if (entityBox.intersects(boundingBox)) {
                collidingBoxes.add(boundingBox);
            }
        } else {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox,
                    collidingBoxes, entityIn, isActualState);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public enum EnumAttachPosition implements IStringSerializable {
        /**
         * When the ash is on the ground
         */
        SIDE("side"),
        /**
         * When the ash is a line that goes up
         */
        UP("up"),
        /**
         * When there is no ash in that direction
         */
        NONE("none");
        private final String name;

        EnumAttachPosition(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.getName();
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
