package theblockbox.huntersdream.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theblockbox.huntersdream.api.init.CreativeTabInit;
import theblockbox.huntersdream.api.init.ItemInit;
import theblockbox.huntersdream.api.init.PropertyInit;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockTent extends BlockHorizontal {
    protected static final AxisAlignedBB TENT_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9D, 1.0D);

    public BlockTent() {
        super(Material.WOOD);
        this.setHardness(1.0f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(PropertyInit.TENT_PART, BlockTent.EnumPartType.FOOT).withProperty(PropertyInit.TENT_OCCUPIED, Boolean.FALSE));
        this.setCreativeTab(CreativeTabInit.HUNTERSDREAM_MISC);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        } else {
            if (state.getValue(PropertyInit.TENT_PART) != BlockTent.EnumPartType.HEAD) {
                pos = pos.offset(state.getValue(BlockHorizontal.FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this) {
                    return true;
                }
            }

            if (worldIn.provider.canRespawnHere() && worldIn.getBiome(pos) != Biomes.HELL) {
                if (state.getValue(PropertyInit.TENT_OCCUPIED)) {
                    EntityPlayer entityplayer = this.getPlayerInTent(worldIn, pos);

                    if (entityplayer != null) {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.occupied"), true);
                        return true;
                    }

                    state = state.withProperty(PropertyInit.TENT_OCCUPIED, Boolean.FALSE);
                    worldIn.setBlockState(pos, state, 4);
                }

                EntityPlayer.SleepResult sleepResult = playerIn.trySleep(pos);

                if (sleepResult == EntityPlayer.SleepResult.OK) {
                    state = state.withProperty(PropertyInit.TENT_OCCUPIED, Boolean.TRUE);
                    worldIn.setBlockState(pos, state, 4);
                } else {
                    if (sleepResult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW) {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.noSleep"), true);
                    } else if (sleepResult == EntityPlayer.SleepResult.NOT_SAFE) {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.notSafe"), true);
                    } else if (sleepResult == EntityPlayer.SleepResult.TOO_FAR_AWAY) {
                        playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.tooFarAway"), true);
                    }

                }
                return true;
            } else {
                worldIn.setBlockToAir(pos);
                BlockPos blockpos = pos.offset(state.getValue(BlockHorizontal.FACING).getOpposite());

                if (worldIn.getBlockState(blockpos).getBlock() == this) {
                    worldIn.setBlockToAir(blockpos);
                }

                worldIn.newExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 5.0F, true, true);
                return true;
            }
        }
    }

    @Nullable
    private EntityPlayer getPlayerInTent(World worldIn, BlockPos pos) {
        for (EntityPlayer entityplayer : worldIn.playerEntities) {
            if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos)) {
                return entityplayer;
            }
        }

        return null;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumFacing enumfacing = state.getValue(BlockHorizontal.FACING);

        if (state.getValue(PropertyInit.TENT_PART) == BlockTent.EnumPartType.FOOT) {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this) {
                worldIn.setBlockToAir(pos);
            }
        } else if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this) {
            if (!worldIn.isRemote) {
                this.dropBlockAsItem(worldIn, fromPos, state, 0);
            }

            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(PropertyInit.TENT_PART) == BlockTent.EnumPartType.FOOT ? Items.AIR : ItemInit.TENT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockTent.TENT_AABB;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Nullable
    public static BlockPos getSafeExitLocation(World worldIn, BlockPos pos, int tries) {
        EnumFacing enumfacing = worldIn.getBlockState(pos).getValue(BlockHorizontal.FACING);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for (int l = 0; l <= 1; ++l) {
            int i1 = i - enumfacing.getXOffset() * l - 1;
            int j1 = k - enumfacing.getZOffset() * l - 1;
            int k1 = i1 + 2;
            int l1 = j1 + 2;

            for (int i2 = i1; i2 <= k1; ++i2) {
                for (int j2 = j1; j2 <= l1; ++j2) {
                    BlockPos blockpos = new BlockPos(i2, j, j2);

                    if (BlockTent.hasRoomForPlayer(worldIn, blockpos)) {
                        if (tries <= 0) {
                            return blockpos;
                        }

                        --tries;
                    }
                }
            }
        }

        return null;
    }

    protected static boolean hasRoomForPlayer(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isTopSolid() && !worldIn.getBlockState(pos).getMaterial().isSolid() && !worldIn.getBlockState(pos.up()).getMaterial().isSolid();
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && state.getValue(PropertyInit.TENT_PART) == BlockTent.EnumPartType.FOOT) {
            BlockPos blockpos = pos.offset(state.getValue(BlockHorizontal.FACING));

            if (worldIn.getBlockState(blockpos).getBlock() == this) {
                worldIn.setBlockToAir(blockpos);
            }
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (state.getValue(PropertyInit.TENT_PART) == BlockTent.EnumPartType.HEAD) {
            Block.spawnAsEntity(worldIn, pos, this.getItem(worldIn, pos, state));
        } else {
            super.harvestBlock(worldIn, player, pos, state, null, stack);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(PropertyInit.TENT_PART, BlockTent.EnumPartType.HEAD).withProperty(BlockHorizontal.FACING, enumfacing).withProperty(PropertyInit.TENT_OCCUPIED, (meta & 4) > 0) : this.getDefaultState().withProperty(PropertyInit.TENT_PART, BlockTent.EnumPartType.FOOT).withProperty(BlockHorizontal.FACING, enumfacing);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(PropertyInit.TENT_PART) == BlockTent.EnumPartType.FOOT) {
            IBlockState iblockstate = worldIn.getBlockState(pos.offset(state.getValue(BlockHorizontal.FACING)));

            if (iblockstate.getBlock() == this) {
                state = state.withProperty(PropertyInit.TENT_OCCUPIED, iblockstate.getValue(PropertyInit.TENT_OCCUPIED));
            }
        }

        return state;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(BlockHorizontal.FACING, rot.rotate(state.getValue(BlockHorizontal.FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(BlockHorizontal.FACING)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i |= state.getValue(BlockHorizontal.FACING).getHorizontalIndex();

        if (state.getValue(PropertyInit.TENT_PART) == BlockTent.EnumPartType.HEAD) {
            i |= 8;

            if (state.getValue(PropertyInit.TENT_OCCUPIED)) {
                i |= 4;
            }
        }

        return i;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(ItemInit.TENT);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockHorizontal.FACING, PropertyInit.TENT_PART, PropertyInit.TENT_OCCUPIED);
    }

    @Override
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, Entity player) {
        return true;
    }

    public enum EnumPartType implements IStringSerializable {

        HEAD("head"),
        FOOT("foot");

        private final String name;

        EnumPartType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

}