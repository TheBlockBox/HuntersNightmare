package theblockbox.huntersdream.blocks;

import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * A {@link BlockVine} that you can't climb and that gives you effects when touched.
 */
public class BlockEffectVine extends BlockVine {
    private final PotionEffect potionEffect;

    public BlockEffectVine(PotionEffect potionEffect) {
        this.setHardness(0.2F);
        this.setSoundType(SoundType.PLANT);
        this.potionEffect = potionEffect;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(this.potionEffect));
        }
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return false;
    }
}
