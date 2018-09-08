package theblockbox.huntersdream.world.gen.village.component;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import theblockbox.huntersdream.init.StructureInit;

public class StructureVillageHuntersCabin extends StructureVillageComponent {
	public static final int SIZE_X = 11;
	public static final int SIZE_Y = 8;
	public static final int SIZE_Z = 13;

	public StructureVillageHuntersCabin() {
		super(null, null, StructureInit.HUNTERS_CABIN, SIZE_X, SIZE_Y, SIZE_Z);
	}

	public StructureVillageHuntersCabin(StructureBoundingBox boundingBox, EnumFacing facing) {
		super(boundingBox, facing, StructureInit.HUNTERS_CABIN, SIZE_X, SIZE_Y, SIZE_Z);
	}

	public static StructureVillageHuntersCabin buildComponent(List<StructureComponent> pieces, int structureMinX,
			int structureMinY, int structureMinZ, EnumFacing facing) {
		StructureBoundingBox boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(structureMinX,
				structureMinY, structureMinZ, 0, 0, 0, SIZE_X, SIZE_Y, SIZE_Z, facing);
		return canVillageGoDeeper(boundingBox) && StructureComponent.findIntersecting(pieces, boundingBox) == null
				? new StructureVillageHuntersCabin(boundingBox, facing)
				: null;
	}
}
