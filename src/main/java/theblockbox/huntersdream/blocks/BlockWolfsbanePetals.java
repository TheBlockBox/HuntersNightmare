package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.BooleanUtils;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.helpers.WerewolfHelper;
import theblockbox.huntersdream.api.init.CreativeTabInit;

public class BlockWolfsbanePetals extends Block {
    public static final PropertyBool ON_UP_SIDE = PropertyBool.create("on_up_side");
    public static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.95D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.05D, 1.0D);

    public BlockWolfsbanePetals() {
        super(Material.PLANTS);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.getDefaultState().withProperty(BlockWolfsbanePetals.ON_UP_SIDE, false));
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    /**
     * Tests if all neighbors around the given block are allowed
     */
    public static boolean allNeighborsAllowed(EnumFacing facing, BlockPos pos, IBlockAccess blockAccess) {
        return blockAccess.getBlockState(pos.offset(facing)).getBlockFaceShape(blockAccess, pos, facing) == BlockFaceShape.SOLID;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockWolfsbanePetals.ON_UP_SIDE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockWolfsbanePetals.ON_UP_SIDE, BooleanUtils.toBoolean(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return BooleanUtils.toInteger(state.getValue(BlockWolfsbanePetals.ON_UP_SIDE));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(BlockWolfsbanePetals.ON_UP_SIDE, facing == EnumFacing.DOWN);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(BlockWolfsbanePetals.ON_UP_SIDE) ? BlockWolfsbanePetals.UP_AABB : BlockWolfsbanePetals.DOWN_AABB;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote && !BlockWolfsbanePetals.allNeighborsAllowed(state.getValue(BlockWolfsbanePetals.ON_UP_SIDE)
                ? EnumFacing.UP : EnumFacing.DOWN, pos, worldIn)) {
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
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return (side.getAxis() == EnumFacing.Axis.Y) && BlockWolfsbanePetals.allNeighborsAllowed(side.getOpposite(), pos, worldIn);
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
