package theblockbox.huntersdream.world.gen.village.handler;

import java.util.List;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import theblockbox.huntersdream.world.gen.village.component.StructureVillageCastle;

public class VillageCastleHandler extends VillageCreationHandler {

	public VillageCastleHandler() {
		super(StructureVillageCastle.class, 10, 1);
	}

	@Override
	public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces,
			Random random, int structureMinX, int structureMinY, int structureMinZ, EnumFacing facing, int p5) {
		return StructureVillageCastle.buildComponent(pieces, structureMinX, structureMinY, structureMinZ, facing);
	}
}
