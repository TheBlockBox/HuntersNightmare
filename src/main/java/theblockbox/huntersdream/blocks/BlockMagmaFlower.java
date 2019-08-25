package theblockbox.huntersdream.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.CreativeTabInit;

import java.util.Random;

public class BlockMagmaFlower extends BlockFlowerBase {
    public BlockMagmaFlower() {
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && this.canSustainBush(worldIn.getBlockState(pos.down()));
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return this.canSustainBush(worldIn.getBlockState(pos.down()));
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return GeneralHelper.itemStackHasOreDict(new ItemStack(state.getBlock()), "netherrack");
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random random) {
        Blocks.FIRE.randomDisplayTick(stateIn, worldIn, pos, random);
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos) {
        return true;
    }
}