package theblockbox.huntersdream.world.gen.village.handler;

import java.util.List;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import theblockbox.huntersdream.world.gen.village.component.StructureVillageHuntersCabin;

public class VillageHuntersCabinHandler extends VillageCreationHandler {

	public VillageHuntersCabinHandler() {
		super(StructureVillageHuntersCabin.class, 10, 1);
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces,
			Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int p5) {
		return StructureVillageHuntersCabin.buildComponent(pieces, structureMinX, structureMinY, structureMinZ, facing);
	}
}
