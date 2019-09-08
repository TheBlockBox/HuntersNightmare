package theblockbox.huntersdream.blocks;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.init.PropertyInit;

public class BlockHealingHerb extends BlockCrops {
    private static final AxisAlignedBB[] HEALING_HERB_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)};

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, this.getAgeProperty());
    }

    @Override
    protected PropertyInteger getAgeProperty() {
        return PropertyInit.HEALING_HERB_AGE;
    }

    @Override
    public int getMaxAge() {
        return 3;
    }

    @Override
    protected Item getSeed() {
        return ItemInit.HEALING_HERB;
    }

    @Override
    protected Item getCrop() {
        return ItemInit.HEALING_HERB;
    }

    @Override
    protected int getBonemealAgeIncrease(World worldIn) {
        return super.getBonemealAgeIncrease(worldIn) / 3;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockHealingHerb.HEALING_HERB_AABB[(state.getValue(this.getAgeProperty()))];
    }
}
