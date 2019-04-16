package theblockbox.huntersdream.blocks;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

// TODO: test if #withRotation actually works
public abstract class BlockGarlandSixSided<T extends BlockGarlandSixSided<T>> extends BlockGarland {
    public static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0D, 0.05D, 0.0D, 1.0D, 0.05D, 1.0D);
    public static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.95D, 0.0D, 1.0D, 0.95D, 1.0D);

    private final boolean hasTopBlock;
    private final boolean hasBottomBlock;

    public BlockGarlandSixSided(boolean hasTopBlock, boolean hasBottomBlock) {
        this.hasTopBlock = hasTopBlock;
        this.hasBottomBlock = hasBottomBlock;
    }

    public abstract T getWithProperties(boolean hasTop, boolean hasBottom);

    @Override
    public T getDefault() {
        return this.getWithProperties(false, false);
    }

    public boolean hasTopBlock() {
        return this.hasTopBlock;
    }

    public boolean hasBottomBlock() {
        return this.hasBottomBlock;
    }

    public IBlockState copyStateWithProperties(boolean hasTop, boolean hasBottom, IBlockState toCopy) {
        return this.getWithProperties(hasTop, hasBottom).getStateFromMeta(toCopy.getBlock().getMetaFromState(toCopy));
    }

    @Override
    public Object2IntMap.Entry<IBlockState> checkSides(IBlockAccess blockAccess, BlockPos posIn) {
        Object2IntMap.Entry<IBlockState> superResult = super.checkSides(blockAccess, posIn);
        int removedGarlands = superResult.getIntValue();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(posIn);
        BlockGarlandSixSided<?> block = (BlockGarlandSixSided<?>) blockAccess.getBlockState(posIn).getBlock();
        boolean hasTop = false;
        boolean hasBottom = false;

        if (block.hasTopBlock()) {
            if (this.isAllowedNeighbor(blockAccess, pos.move(EnumFacing.UP), EnumFacing.DOWN))
                hasTop = true;
            else
                removedGarlands++;
            pos.move(EnumFacing.DOWN);
        }
        if (block.hasBottomBlock()) {
            if (this.isAllowedNeighbor(blockAccess, pos.move(EnumFacing.DOWN), EnumFacing.UP))
                hasBottom = true;
            else
                removedGarlands++;
            pos.move(EnumFacing.UP);
        }

        return new AbstractObject2IntMap.BasicEntry<>(this.copyStateWithProperties(hasTop, hasBottom,
                superResult.getKey()), removedGarlands);
    }

    @Override
    public boolean isAllowedState(IBlockState state) {
        if (super.isAllowedState(state)) {
            return true;
        }
        if (state.getBlock() instanceof BlockGarlandSixSided) {
            BlockGarlandSixSided<?> block = (BlockGarlandSixSided<?>) state.getBlock();
            return this.isTheSameAs(block) && (block.hasTopBlock() || block.hasBottomBlock());
        }
        return false;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        int toReturn = super.quantityDropped(state, fortune, random);
        BlockGarlandSixSided<?> block = (BlockGarlandSixSided<?>) state.getBlock();
        if (block.hasTopBlock())
            toReturn++;
        if (block.hasBottomBlock())
            toReturn++;
        return toReturn;
    }


    // final so that BlockGarlandSixSided#copyStateWithProperties will always work correctly
    @Override
    public final IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        // TODO: When updating to 1.13, check if the array still has the same ordering
        IBlockState oldState = world.getBlockState(pos);
        Block oldBlock = oldState.getBlock();
        if ((oldBlock instanceof BlockGarlandSixSided) && this.isTheSameAs((BlockGarlandSixSided<?>) oldBlock)) {
            BlockGarlandSixSided<?> block = (BlockGarlandSixSided<?>) oldBlock;
            switch (facing) {
                case UP:
                    return this.copyStateWithProperties(block.hasTopBlock(), true, oldState);
                case DOWN:
                    return this.copyStateWithProperties(true, block.hasBottomBlock(), oldState);
                default:
                    return oldState.withProperty(BlockGarland.PROPERTIES[facing.getOpposite().getIndex() - 2], true);
            }
        } else {
            switch (facing) {
                case UP:
                    return this.getWithProperties(false, true).getDefaultState();
                case DOWN:
                    return this.getWithProperties(true, false).getDefaultState();
                default:
                    return this.getWithProperties(false, false).getDefaultState()
                            .withProperty(BlockGarland.PROPERTIES[facing.getOpposite().getIndex() - 2], true);
            }
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB toReturn = super.getBoundingBox(state, source, pos);
        if ((toReturn != Block.FULL_BLOCK_AABB) && (state.getBlock() instanceof BlockGarlandSixSided)) {
            BlockGarlandSixSided<?> block = (BlockGarlandSixSided<?>) state.getBlock();
            int properties = 0;
            if (block.hasBottomBlock()) {
                toReturn = BlockGarlandSixSided.DOWN_AABB;
                properties++;
            }
            if (block.hasTopBlock()) {
                toReturn = BlockGarlandSixSided.UP_AABB;
                properties++;
            }
            if (properties != 1) {
                toReturn = Block.FULL_BLOCK_AABB;
            }
        }
        return toReturn;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        if (super.canPlaceBlockOnSide(worldIn, pos, side)) {
            return true;
        }
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() instanceof BlockGarlandSixSided) {
            BlockGarlandSixSided<?> block = (BlockGarlandSixSided<?>) state.getBlock();
            return this.isTheSameAs(block) && (((side == EnumFacing.UP) && !block.hasTopBlock())
                    || ((side == EnumFacing.DOWN) && !block.hasBottomBlock()));
        }
        return false;
    }
}
