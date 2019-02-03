package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CreativeTabInit;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.helpers.WerewolfHelper;

import javax.annotation.Nullable;
import java.util.List;

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
        if(entityIn instanceof EntityLivingBase) {
            WerewolfHelper.applyAconiteEffects((EntityLivingBase) entityIn);
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if ((entityIn instanceof EntityLivingBase) && (TransformationHelper.getTransformation((EntityLivingBase) entityIn) == Transformation.WEREWOLF)) {
            // TODO: Use normal bounding box instead of full one?
            collidingBoxes.add(Block.FULL_BLOCK_AABB.offset(pos));
            if(!worldIn.isRemote) {
                WerewolfHelper.applyAconiteEffects((EntityLivingBase) entityIn);
            }
        } else {
            super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
        }
    }
}
