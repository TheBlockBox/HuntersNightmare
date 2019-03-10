package theblockbox.huntersdream.world.gen.village;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.BiomeSavanna;
import net.minecraft.world.biome.BiomeTaiga;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.Template;
import theblockbox.huntersdream.api.init.StructureInit;

import java.util.List;
import java.util.Random;

public class StructureVillageVillagerCastle extends StructureVillageComponent {
    public static final int SIZE_X = 16;
    public static final int SIZE_Y = 14;
    public static final int SIZE_Z = 16;

    public StructureVillageVillagerCastle() {
        super(null, null, null, StructureVillageVillagerCastle.SIZE_X, StructureVillageVillagerCastle.SIZE_Y, StructureVillageVillagerCastle.SIZE_Z);
    }

    public StructureVillageVillagerCastle(StructureBoundingBox boundingBox, EnumFacing facing) {
        super(boundingBox, facing, null, StructureVillageVillagerCastle.SIZE_X, StructureVillageVillagerCastle.SIZE_Y, StructureVillageVillagerCastle.SIZE_Z);
    }

    public static StructureVillageVillagerCastle buildComponent(List<StructureComponent> pieces, int structureMinX,
                                                                int structureMinY, int structureMinZ, EnumFacing facing) {
        StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX,
                structureMinY, structureMinZ, 0, -1, 0, StructureVillageVillagerCastle.SIZE_X, StructureVillageVillagerCastle.SIZE_Y - 1, StructureVillageVillagerCastle.SIZE_Z, facing);
        return StructureVillagePieces.Village.canVillageGoDeeper(boundingBox) && StructureComponent.findIntersecting(pieces, boundingBox) == null
                ? new StructureVillageVillagerCastle(boundingBox, facing.getOpposite())
                : null;
    }

    // TODO: Use other way to decide which structure to use
    @Override
    public void spawnStructure(World worldIn, Random randomIn, StructureBoundingBox boundingBoxIn) {
        BlockPos pos = new BlockPos(this.getXWithOffset(0, 0), this.getYWithOffset(0), this.getZWithOffset(0, 0));
        Biome biome = worldIn.getBiome(pos);
        if (biome instanceof BiomeDesert) {
            this.structureLocation = StructureInit.VILLAGER_CASTLE_SANDSTONE;
        } else if (biome instanceof BiomeSavanna) {
            this.structureLocation = StructureInit.VILLAGER_CASTLE_ACACIA;
        } else if (biome instanceof BiomeTaiga) {
            this.structureLocation = StructureInit.VILLAGER_CASTLE_SPRUCE;
        } else {
            this.structureLocation = StructureInit.VILLAGER_CASTLE_OAK;
        }
        Template template = worldIn.getSaveHandler().getStructureTemplateManager()
                .getTemplate(worldIn.getMinecraftServer(), this.structureLocation);
        template.addBlocksToWorld(worldIn, pos, this.getPlacementSettings(worldIn, randomIn, boundingBoxIn, pos));
    }

    public static class VillageVillagerCastleHandler extends VillageCreationHandler {

        public VillageVillagerCastleHandler() {
            super(StructureVillageVillagerCastle.class, 10, 1);
        }

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces,
                                                             Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int p5) {
            return StructureVillageVillagerCastle.buildComponent(pieces, structureMinX, structureMinY, structureMinZ, facing);
        }
    }
}
