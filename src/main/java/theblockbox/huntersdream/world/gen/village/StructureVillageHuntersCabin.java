package theblockbox.huntersdream.world.gen.village;

import java.util.List;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import theblockbox.huntersdream.init.StructureInit;

public class StructureVillageHuntersCabin extends StructureVillageComponent {
	// TODO: Use real dimensions
	public static final int SIZE_X = 14;
	public static final int SIZE_Y = 8;
	public static final int SIZE_Z = 11;

	public StructureVillageHuntersCabin() {
		super(null, null, StructureInit.HUNTERS_CABIN, StructureVillageHuntersCabin.SIZE_X, StructureVillageHuntersCabin.SIZE_Y, StructureVillageHuntersCabin.SIZE_Z);
	}

	public StructureVillageHuntersCabin(StructureBoundingBox boundingBox, EnumFacing facing) {
		super(boundingBox, facing, StructureInit.HUNTERS_CABIN, StructureVillageHuntersCabin.SIZE_X, StructureVillageHuntersCabin.SIZE_Y, StructureVillageHuntersCabin.SIZE_Z);
	}

	public static StructureVillageHuntersCabin buildComponent(List<StructureComponent> pieces, int structureMinX,
															  int structureMinY, int structureMinZ, EnumFacing facing) {
		StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX,
				structureMinY, structureMinZ, 0, 0, 0, StructureVillageHuntersCabin.SIZE_X,
				StructureVillageHuntersCabin.SIZE_Y, StructureVillageHuntersCabin.SIZE_Z, facing);
		return StructureVillagePieces.Village.canVillageGoDeeper(boundingBox) && StructureComponent.findIntersecting(pieces, boundingBox) == null
				? new StructureVillageHuntersCabin(boundingBox, facing)
				: null;
	}

	public static class VillageHuntersCabinHandler extends VillageCreationHandler {

		public VillageHuntersCabinHandler() {
			super(StructureVillageHuntersCabin.class, 10, 1);
		}

		@Override
		public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces,
															 Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int p5) {
			return StructureVillageHuntersCabin.buildComponent(pieces, structureMinX, structureMinY, structureMinZ, facing);
		}
	}
}
