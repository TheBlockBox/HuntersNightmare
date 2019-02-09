package theblockbox.huntersdream.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

public class BlockAconiteGarland extends BlockGarland<BlockAconiteGarland> {
    public BlockAconiteGarland(boolean hasTopBlock, boolean hasBottomBlock) {
        super(hasTopBlock, hasBottomBlock);
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    @Override
    public BlockAconiteGarland getWithProperties(boolean hasTop, boolean hasBottom) {
        return hasTop ? (hasBottom ? BlockInit.ACONITE_GARLAND_TOP_AND_BOTTOM : BlockInit.ACONITE_GARLAND_TOP)
                : (hasBottom ? BlockInit.ACONITE_GARLAND_BOTTOM : BlockInit.ACONITE_GARLAND);
    }

    @Override
    public boolean isTheSameAs(BlockGarland<?> otherBlock) {
        return (otherBlock == BlockInit.ACONITE_GARLAND) || (otherBlock == BlockInit.ACONITE_GARLAND_TOP)
                || (otherBlock == BlockInit.ACONITE_GARLAND_BOTTOM) || (otherBlock == BlockInit.ACONITE_GARLAND_TOP_AND_BOTTOM);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            if (WerewolfHelper.applyAconiteEffects((EntityLivingBase) entityIn, false)) {
                entityIn.motionX = Math.ceil(entityIn.posX) - Math.round(entityIn.posX) - 0.5D;
                entityIn.motionY = 0.5D;
                entityIn.motionZ = Math.ceil(entityIn.posZ) - Math.round(entityIn.posZ) - 0.5D;
            }
        }
    }
}