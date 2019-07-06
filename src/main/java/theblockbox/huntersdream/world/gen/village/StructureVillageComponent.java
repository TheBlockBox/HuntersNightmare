package theblockbox.huntersdream.world.gen.village;

import net.minecraft.block.BlockChest;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nonnull;
import java.util.Random;

public class StructureVillageComponent extends StructureVillagePieces.House1 {
    public final ITemplateProcessor biomeBlockChanger = (world, pos, blockInfo) -> {
        if (blockInfo.blockState.getBlock() == Blocks.COBBLESTONE) {
            return blockInfo;
        }
        if (blockInfo.blockState.getBlock() instanceof BlockChest) {
            TileEntityChest tileEntity = new TileEntityChest();
            world.rand.setSeed(world.rand.nextLong());
            tileEntity.setLootTable(LootTableList.CHESTS_VILLAGE_BLACKSMITH, world.rand.nextLong());
            return new Template.BlockInfo(pos, blockInfo.blockState, tileEntity.writeToNBT(new NBTTagCompound()));
        }

        Biome biome = world.getBiome(pos);
        if (biome instanceof BiomeDesert) {
            this.setStructureType(1);
        } else if (biome instanceof BiomeSavanna) {
            this.setStructureType(2);
        } else if (biome instanceof BiomeTaiga) {
            this.setStructureType(3);
        }
        return new Template.BlockInfo(pos, this.getBiomeSpecificBlockState(blockInfo.blockState), null);
    };
    protected ResourceLocation structureLocation;
    protected int avgGroundLevel = -1;
    protected int xSize;
    protected int ySize;
    protected int zSize;

    public StructureVillageComponent(StructureBoundingBox boundingBox, EnumFacing facing,
                                     @Nonnull ResourceLocation structureLocation, int xSize, int ySize, int zSize) {
        this.setCoordBaseMode(facing);
        this.boundingBox = boundingBox;
        this.structureLocation = structureLocation;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox boundingBoxIn) {
        if (this.avgGroundLevel < 0) {
            this.avgGroundLevel = this.getAverageGroundLevel(worldIn, boundingBoxIn) - 1;
            if (this.avgGroundLevel < 0) {
                return true;
            } else {
                this.boundingBox.offset(0, this.avgGroundLevel - this.boundingBox.maxY + this.ySize - 1, 0);
            }
        }

        this.fillWithBlocks(worldIn, boundingBoxIn, 0, 0, 0, this.xSize - 1, this.ySize - 1, this.zSize - 1,
                Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.spawnStructure(worldIn, randomIn, boundingBoxIn);

        for (int i = 0; i < this.xSize; i++) {
            for (int j = 0; j < this.zSize; j++) {
                this.clearCurrentPositionBlocksUpwards(worldIn, i, this.ySize, j, boundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, worldIn.getBiome(new BlockPos(this.getXWithOffset(0, 0),
                        this.getYWithOffset(0), this.getZWithOffset(0, 0))).fillerBlock, i, -1, j, boundingBoxIn);
            }
        }
        return true;
    }

    public void spawnStructure(World worldIn, Random randomIn, StructureBoundingBox boundingBoxIn) {
        Template template = worldIn.getSaveHandler().getStructureTemplateManager()
                .getTemplate(worldIn.getMinecraftServer(), this.structureLocation);
        BlockPos pos = new BlockPos(this.getXWithOffset(0, 0), this.getYWithOffset(0), this.getZWithOffset(0, 0));
        template.addBlocksToWorld(worldIn, pos, this.biomeBlockChanger, this.getPlacementSettings(worldIn, randomIn, boundingBoxIn, pos), 2);
    }

    public PlacementSettings getPlacementSettings(World worldIn, Random randomIn, StructureBoundingBox boundingBoxIn,
                                                  BlockPos pos) {
        EnumFacing facing = this.getCoordBaseMode();
        Mirror mirror = (facing == EnumFacing.SOUTH || facing == EnumFacing.WEST) ? Mirror.NONE : Mirror.LEFT_RIGHT;
        Rotation rotation = (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH) ? Rotation.NONE
                : Rotation.CLOCKWISE_90;
        return new PlacementSettings().setRotation(rotation).setMirror(mirror).setBoundingBox(boundingBoxIn);
    }
}
