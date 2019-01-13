package theblockbox.huntersdream.world.gen.village;

import java.util.Random;

import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public abstract class VillageCreationHandler implements VillagerRegistry.IVillageCreationHandler {
	private final Class<? extends StructureVillagePieces.Village> componentClass;
	private final int pieceWeight;
	private final int maxStructuresInVillage;

	public VillageCreationHandler(Class<? extends StructureVillagePieces.Village> componentClass, int pieceWeight,
			int maxStructuresInVillage) {
		this.componentClass = componentClass;
		this.pieceWeight = pieceWeight;
		this.maxStructuresInVillage = maxStructuresInVillage;
	}

	@Override
	public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
		return new StructureVillagePieces.PieceWeight(this.componentClass, this.pieceWeight,
				this.maxStructuresInVillage);
	}

	@Override
	public Class<?> getComponentClass() {
		return this.componentClass;
	}
}
