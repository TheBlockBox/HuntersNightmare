package theblockbox.huntersdream.world.gen.village;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import theblockbox.huntersdream.api.init.BlockInit;

import java.util.List;
import java.util.Random;

public class StructureVillageModifiedField extends StructureVillagePieces.Field1 {
    public StructureVillageModifiedField() {
    }

    public StructureVillageModifiedField(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox boundingBox, EnumFacing facing) {
        super(start, type, rand, boundingBox, facing);
        this.setCoordBaseMode(facing);
        this.boundingBox = boundingBox;
        if (rand.nextInt(5) == 0) {
            this.cropTypeA = BlockInit.HEALING_HERB;
            this.cropTypeB = BlockInit.HEALING_HERB;
        }
    }

    public static class VillageModifiedFieldHandler extends VillageCreationHandler {
        public VillageModifiedFieldHandler() {
            super(StructureVillageModifiedField.class, 3, 1);
        }

        @Override
        public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces,
                                                             Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int p5) {
            StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX, structureMinY, structureMinZ,
                    0, 0, 0, 13, 4, 9, facing);
            return StructureVillagePieces.Village.canVillageGoDeeper(boundingBox) && StructureComponent.findIntersecting(pieces, boundingBox) == null ?
                    new StructureVillageModifiedField(startPiece, p5, random, boundingBox, facing) : null;
        }
    }
}
