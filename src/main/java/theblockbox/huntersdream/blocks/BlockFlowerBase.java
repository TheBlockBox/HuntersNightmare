package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import theblockbox.huntersdream.util.BlockStateWrapper;

public class BlockFlowerBase extends BlockFlower {
    public BlockFlowerBase() {
        this.setSoundType(SoundType.PLANT);
    }

    @Override
    public BlockFlower.EnumFlowerColor getBlockType() {
        return null;
    }

    @Override
    public IProperty<BlockFlower.EnumFlowerType> getTypeProperty() {
        return null;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        // literally created all of this just to be able
        // to extend BlockFlower
        return new BlockFlowerBase.CustomBlockStateContainer(this);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    private static class CustomBlockStateContainer extends BlockStateContainer {
        private int baseStateCall = 0;

        private CustomBlockStateContainer(Block block) {
            super(block);
        }

        @Override
        public IBlockState getBaseState() {
            IBlockState state = super.getBaseState();
            return (this.baseStateCall++ == 1) ? new BlockStateWrapper(state) {
                @Override
                public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
                    return state;
                }
            } : state;
        }
    }
}
