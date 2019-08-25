package theblockbox.huntersdream.api.init;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import theblockbox.huntersdream.api.helpers.ChanceHelper;
import theblockbox.huntersdream.api.helpers.GeneralHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is used to generate smaller things like plants.
 */
public class WorldGenInit {
    private static final List<WorldGenInit.WorldGenEntry> ENTRIES = new ArrayList<>();

    static {
        new WorldGenInit.WorldGenEntry(null, DecorateBiomeEvent.Decorate.EventType.FLOWERS, 1.5D) {
            @Override
            public IBlockState getState(World world, BlockPos pos, Random random) {
                return (random.nextBoolean() ? BlockInit.ACONITE_FLOWER : BlockInit.MONKSHOOD_FLOWER).getDefaultState();
            }

            @Override
            public int getAmountOfBlocks(Random random) {
                return random.nextInt(5);
            }
        };
        new WorldGenInit.WorldGenEntry(BlockInit.COTTON.getDefaultState().withProperty(PropertyInit.COTTON_AGE, 3),
                DecorateBiomeEvent.Decorate.EventType.FLOWERS, 0.5D).add();
        new WorldGenInit.WorldGenEntry(BlockInit.GLOW_FERN.getDefaultState(), DecorateBiomeEvent.Decorate.EventType.FLOWERS, 0.7D) {
            @Override
            public boolean canGenerateHere(World world, BlockPos pos) {
                return pos.getY() < world.getHeight(pos.getX(), pos.getZ());
            }
        }.add();
        // Use DecorateBiomeEvent.Decorate.EventType.SHROOM because mushrooms are the only plants that generate in the nether
        new WorldGenInit.WorldGenEntry(BlockInit.MAGMA_FLOWER.getDefaultState(), DecorateBiomeEvent.Decorate.EventType.SHROOM, 10.0D) {
            @Override
            public boolean canGenerateHere(World world, BlockPos pos) {
                return BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.NETHER);
            }
        }.add();
        new WorldGenInit.WorldGenEntry(null, DecorateBiomeEvent.Decorate.EventType.FLOWERS, 25.0D) {
            @Override
            public IBlockState getState(World world, BlockPos pos, Random random) {
                IBlockState state = BlockInit.POISON_IVY.getDefaultState();
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (GeneralHelper.itemStackHasOreDict(new ItemStack(world.getBlockState(pos.offset(facing)).getBlock()), "logWood")) {
                        state = state.withProperty(BlockVine.getPropertyFor(facing), true);
                    }
                }
                return state;
            }

            @Override
            public boolean canGenerateHere(World world, BlockPos pos) {
                if (BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.SWAMP)) {
                    for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                        if (GeneralHelper.itemStackHasOreDict(new ItemStack(world.getBlockState(pos.offset(facing)).getBlock()), "logWood")) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public int getAmountOfBlocks(Random random) {
                return random.nextInt(3) + 2;
            }
        }.add();
        // Use DecorateBiomeEvent.Decorate.EventType.SHROOM because mushrooms are the only plants that generate in the nether
        new WorldGenInit.WorldGenEntry(null, DecorateBiomeEvent.Decorate.EventType.SHROOM, 10.0D) {
            @Override
            public IBlockState getState(World world, BlockPos pos, Random random) {
                IBlockState state = BlockInit.WITHER_MOSS.getDefaultState();
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    if (world.getBlockState(pos.offset(facing)).getBlock() == Blocks.NETHER_BRICK) {
                        state = state.withProperty(BlockVine.getPropertyFor(facing), true);
                    }
                }
                return state;
            }

            @Override
            public boolean canGenerateHere(World world, BlockPos pos) {
                if (((WorldServer) world).getChunkProvider().isInsideStructure(world, "Fortress", pos)
                        && BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.NETHER)) {
                    for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                        if (world.getBlockState(pos.offset(facing)).getBlock() == Blocks.NETHER_BRICK) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public int getAmountOfBlocks(Random random) {
                return random.nextInt(3) + 1;
            }
        }.add();
    }

    public static void generate(DecorateBiomeEvent.Decorate event) {
        Event.Result result = event.getResult();
        if (result == Event.Result.ALLOW || result == Event.Result.DEFAULT) {
            World world = event.getWorld();
            Random random = event.getRand();
            BlockPos initalPos = new BlockPos(event.getChunkPos().getBlock(15, 0, 15));
            int xOffset = initalPos.getX();
            int zOffset = initalPos.getZ();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (WorldGenInit.WorldGenEntry entry : WorldGenInit.ENTRIES) {
                if ((event.getType() == entry.getType()) && ChanceHelper.chanceOf(random, entry.getChance(random))) {
                    final int maxBlocks = entry.getAmountOfBlocks(random);
                    int currentBlocks = 0;
                    pos.setPos(initalPos);
                    label:
                    for (int y = 256; y > 0; y--) {
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                IBlockState state = entry.getState(world, pos.setPos(x + xOffset, y, z + zOffset), random);
                                if (!world.getBlockState(pos).getMaterial().isLiquid()
                                        && state.getBlock().canPlaceBlockAt(world, pos) && entry.canGenerateHere(world, pos)) {
                                    world.setBlockState(pos, state, 2);
                                    if (++currentBlocks >= maxBlocks)
                                        break label;
                                    x = Math.min(15, x + random.nextInt(2));
                                    z = Math.min(15, z + random.nextInt(2));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Is used to add new blocks to the world generation.
     */
    public static class WorldGenEntry {
        private final IBlockState state;
        private final DecorateBiomeEvent.Decorate.EventType type;
        private final double chance;

        /**
         * Creates a new instance
         *
         * @param stateToGenerate The block state to generate
         * @param chance          The chance of generation (between 0 and 100)
         */
        public WorldGenEntry(IBlockState stateToGenerate, DecorateBiomeEvent.Decorate.EventType type, double chance) {
            this.state = stateToGenerate;
            this.type = type;
            this.chance = chance;
        }

        public IBlockState getState(World world, BlockPos pos, Random random) {
            return this.state;
        }

        public DecorateBiomeEvent.Decorate.EventType getType() {
            return this.type;
        }

        public double getChance(Random random) {
            return this.chance;
        }

        public int getAmountOfBlocks(Random random) {
            return 1;
        }

        public boolean canGenerateHere(World world, BlockPos pos) {
            return true;
        }

        /**
         * Adds the entry so that it'll be generated.
         */
        public void add() {
            WorldGenInit.ENTRIES.add(this);
        }
    }
}
