package theblockbox.huntersdream.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.init.CreativeTabInit;

public class BlockWolfsbaneFlower extends BlockFlowerBase {
    public BlockWolfsbaneFlower() {
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollision(worldIn, pos, state, entityIn);
        if (!worldIn.isRemote && entityIn instanceof EntityLivingBase) {
            WerewolfHelper.applyWolfsbaneEffects((EntityLivingBase) entityIn, false, false);
        }
    }
}
