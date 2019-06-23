package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.init.CreativeTabInit;

import javax.annotation.Nullable;

public class BlockWolfsbanePetals extends Block {
    public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.05D, 1.0D);

    public BlockWolfsbanePetals() {
        super(Material.PLANTS);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    /**
     * Tests if all neighbors around the given block are allowed
     */
    public static boolean allNeighborsAllowed(BlockPos pos, IBlockAccess blockAccess) {
        return blockAccess.getBlockState(pos.down()).getBlockFaceShape(blockAccess, pos, EnumFacing.DOWN) == BlockFaceShape.SOLID;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockWolfsbanePetals.AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && BlockWolfsbanePetals.allNeighborsAllowed(pos, worldIn)
                && !(worldIn.getBlockState(pos).getBlock() instanceof BlockWolfsbanePetals);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote && !BlockWolfsbanePetals.allNeighborsAllowed(pos, worldIn)) {
            worldIn.setBlockToAir(pos);
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() - 0.5D, pos.getZ(), new ItemStack(this));
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) entityIn;
            if (TransformationHelper.getTransformation(entity) == Transformation.WEREWOLF) {
                entity.addPotionEffect(new PotionEffect(new PotionEffect(WerewolfHelper.isTransformed(entity)
                        ? MobEffects.WITHER : MobEffects.POISON, 200)));
            }
        }
    }
}
