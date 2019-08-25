package theblockbox.huntersdream.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.CreativeTabInit;

public class BlockGlowFern extends BlockFlowerBase {
    public BlockGlowFern() {
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
        this.setLightLevel(1.0F);
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
        return GeneralHelper.itemStackHasOreDict(new ItemStack(state.getBlock()), "stone");
    }
}
