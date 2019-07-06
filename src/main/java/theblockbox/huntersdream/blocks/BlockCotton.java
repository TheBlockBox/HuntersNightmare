package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.CreativeTabInit;
import theblockbox.huntersdream.api.init.PropertyInit;

import java.util.Random;

public class BlockCotton extends BlockBush implements IGrowable {
    public static final AxisAlignedBB[] COTTON_AABB = {new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

    public BlockCotton() {
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.getDefaultState().withProperty(PropertyInit.COTTON_AGE, 0));
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    public static boolean isFullyGrown(IBlockState state) {
        return state.getValue(PropertyInit.COTTON_AGE) == 3;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PropertyInit.COTTON_AGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PropertyInit.COTTON_AGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PropertyInit.COTTON_AGE);
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return (state == null) ? 1 : Math.max(1, state.getValue(PropertyInit.COTTON_AGE));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockCotton.COTTON_AABB[Math.min(1, state.getValue(PropertyInit.COTTON_AGE))];
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        // grow plant
        if (worldIn.isAreaLoaded(pos, 1) && (worldIn.getLightFromNeighbors(pos.up()) >= 9) && !BlockCotton
                .isFullyGrown(state) && ForgeHooks.onCropsGrowPre(worldIn, pos, state, (rand.nextInt(5) == 0))) {
            worldIn.setBlockState(pos, this.getDefaultState().withProperty(PropertyInit.COTTON_AGE, state.getValue(PropertyInit.COTTON_AGE) + 1), 2);
            ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        int age = state.getValue(PropertyInit.COTTON_AGE);
        if (!BlockCotton.isFullyGrown(state) && GeneralHelper.isBonemeal(playerIn.getHeldItem(hand))) {
            // if it isn't fully grown and the player is trying to use bone meal, return false
            return false;
        } else if (age > 1) {
            // if its age is 2 or 3, drop the cotton and reset to age 1
            worldIn.setBlockState(pos, state.withProperty(PropertyInit.COTTON_AGE, 1), 2);
            Block.spawnAsEntity(worldIn, pos, new ItemStack(this.getItemDropped(state, worldIn.rand, 0), age - 1));
            worldIn.playSound(null, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.8F + (worldIn.rand.nextFloat() * 0.4F));
            return true;
        } else {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !BlockCotton.isFullyGrown(state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, state.withProperty(PropertyInit.COTTON_AGE, Math.min(3, state.getValue(PropertyInit.COTTON_AGE) + 1)), 2);
    }
}