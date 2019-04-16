package theblockbox.huntersdream.world.gen.village;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import theblockbox.huntersdream.api.init.StructureInit;

import java.util.List;
import java.util.Random;

public class StructureVillageVillagerCastle extends StructureVillageComponent {
    public static final int SIZE_X = 16;
    public static final int SIZE_Y = 14;
    public static final int SIZE_Z = 16;

    public StructureVillageVillagerCastle() {
        super(null, null, StructureInit.VILLAGER_CASTLE, StructureVillageVillagerCastle.SIZE_X, StructureVillageVillagerCastle.SIZE_Y, StructureVillageVillagerCastle.SIZE_Z);
    }

    public StructureVillageVillagerCastle(StructureBoundingBox boundingBox, EnumFacing facing) {
        super(boundingBox, facing, StructureInit.VILLAGER_CASTLE, StructureVillageVillagerCastle.SIZE_X, StructureVillageVillagerCastle.SIZE_Y, StructureVillageVillagerCastle.SIZE_Z);
    }

    public static StructureVillageVillagerCastle buildComponent(List<StructureComponent> pieces, int structureMinX,
                                                                int structureMinY, int structureMinZ, EnumFacing facing) {
        StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX,
                structureMinY, structureMinZ, 0, -1, 0, StructureVillageVillagerCastle.SIZE_X, StructureVillageVillagerCastle.SIZE_Y - 1, StructureVillageVillagerCastle.SIZE_Z, facing);
        return StructureVillagePieces.Village.canVillageGoDeeper(boundingBox) && StructureComponent.findIntersecting(pieces, boundingBox) == null
                ? new StructureVillageVillagerCastle(boundingBox, facing.getOpposite())
                : null;
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
