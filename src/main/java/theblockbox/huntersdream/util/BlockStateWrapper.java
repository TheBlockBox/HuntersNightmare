package theblockbox.huntersdream.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * A wrapper around the {@link IBlockState} class that delegates all
 * method calls to a pre-defined {@link IBlockState}.
 */
public class BlockStateWrapper implements IBlockState {
    private final IBlockState delegate;

    public BlockStateWrapper(IBlockState delegate) {
        this.delegate = delegate;
    }

    @Override
    public Collection<IProperty<?>> getPropertyKeys() {
        return this.delegate.getPropertyKeys();
    }

    @Override
    public <T extends Comparable<T>> T getValue(IProperty<T> property) {
        return this.delegate.getValue(property);
    }

    @Override
    public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
        return this.delegate.withProperty(property, value);
    }

    @Override
    public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
        return this.delegate.cycleProperty(property);
    }

    @Override
    public ImmutableMap<IProperty<?>, Comparable<?>> getProperties() {
        return this.delegate.getProperties();
    }

    @Override
    public Block getBlock() {
        return this.delegate.getBlock();
    }

    @Override
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, int id, int param) {
        return this.delegate.onBlockEventReceived(worldIn, pos, id, param);
    }

    @Override
    public void neighborChanged(World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.delegate.neighborChanged(worldIn, pos, blockIn, fromPos);
    }

    @Override
    public Material getMaterial() {
        return this.delegate.getMaterial();
    }

    @Override
    public boolean isFullBlock() {
        return this.delegate.isFullBlock();
    }

    @Override
    public boolean canEntitySpawn(Entity entityIn) {
        return this.delegate.canEntitySpawn(entityIn);
    }

    @Override
    @Deprecated
    public int getLightOpacity() {
        return this.delegate.getLightOpacity();
    }

    @Override
    public int getLightOpacity(IBlockAccess world, BlockPos pos) {
        return this.delegate.getLightOpacity(world, pos);
    }

    @Override
    @Deprecated
    public int getLightValue() {
        return this.delegate.getLightValue();
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        return this.delegate.getLightValue(world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return this.delegate.isTranslucent();
    }

    @Override
    public boolean useNeighborBrightness() {
        return this.delegate.useNeighborBrightness();
    }

    @Override
    public MapColor getMapColor(IBlockAccess p_185909_1_, BlockPos p_185909_2_) {
        return this.delegate.getMapColor(p_185909_1_, p_185909_2_);
    }

    @Override
    public IBlockState withRotation(Rotation rot) {
        return this.delegate.withRotation(rot);
    }

    @Override
    public IBlockState withMirror(Mirror mirrorIn) {
        return this.delegate.withMirror(mirrorIn);
    }

    @Override
    public boolean isFullCube() {
        return this.delegate.isFullCube();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasCustomBreakingProgress() {
        return this.delegate.hasCustomBreakingProgress();
    }

    @Override
    public EnumBlockRenderType getRenderType() {
        return this.delegate.getRenderType();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getPackedLightmapCoords(IBlockAccess source, BlockPos pos) {
        return this.delegate.getPackedLightmapCoords(source, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue() {
        return this.delegate.getAmbientOcclusionLightValue();
    }

    @Override
    public boolean isBlockNormalCube() {
        return this.delegate.isBlockNormalCube();
    }

    @Override
    public boolean isNormalCube() {
        return this.delegate.isNormalCube();
    }

    @Override
    public boolean canProvidePower() {
        return this.delegate.canProvidePower();
    }

    @Override
    public int getWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return this.delegate.getWeakPower(blockAccess, pos, side);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return this.delegate.hasComparatorInputOverride();
    }

    @Override
    public int getComparatorInputOverride(World worldIn, BlockPos pos) {
        return this.delegate.getComparatorInputOverride(worldIn, pos);
    }

    @Override
    public float getBlockHardness(World worldIn, BlockPos pos) {
        return this.delegate.getBlockHardness(worldIn, pos);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World worldIn, BlockPos pos) {
        return this.delegate.getPlayerRelativeBlockHardness(player, worldIn, pos);
    }

    @Override
    public int getStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return this.delegate.getStrongPower(blockAccess, pos, side);
    }

    @Override
    public EnumPushReaction getPushReaction() {
        return this.delegate.getPushReaction();
    }

    @Override
    public IBlockState getActualState(IBlockAccess blockAccess, BlockPos pos) {
        return this.delegate.getActualState(blockAccess, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
        return this.delegate.getSelectedBoundingBox(worldIn, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing facing) {
        return this.delegate.shouldSideBeRendered(blockAccess, pos, facing);
    }

    @Override
    public boolean isOpaqueCube() {
        return this.delegate.isOpaqueCube();
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockAccess worldIn, BlockPos pos) {
        return this.delegate.getCollisionBoundingBox(worldIn, pos);
    }

    @Override
    public void addCollisionBoxToList(World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185908_6_) {
        this.delegate.addCollisionBoxToList(worldIn, pos, entityBox, collidingBoxes, entityIn, p_185908_6_);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockAccess blockAccess, BlockPos pos) {
        return this.delegate.getBoundingBox(blockAccess, pos);
    }

    @Override
    public RayTraceResult collisionRayTrace(World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return this.delegate.collisionRayTrace(worldIn, pos, start, end);
    }

    @Override
    @Deprecated
    public boolean isTopSolid() {
        return this.delegate.isTopSolid();
    }

    @Override
    public boolean doesSideBlockRendering(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.delegate.doesSideBlockRendering(world, pos, side);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.delegate.isSideSolid(world, pos, side);
    }

    @Override
    public boolean doesSideBlockChestOpening(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return this.delegate.doesSideBlockChestOpening(world, pos, side);
    }

    @Override
    public Vec3d getOffset(IBlockAccess access, BlockPos pos) {
        return this.delegate.getOffset(access, pos);
    }

    @Override
    public boolean causesSuffocation() {
        return this.delegate.causesSuffocation();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
        return this.delegate.getBlockFaceShape(worldIn, pos, facing);
    }
}
