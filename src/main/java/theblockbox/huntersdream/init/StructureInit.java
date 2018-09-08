package theblockbox.huntersdream.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.world.gen.village.component.StructureVillageCastle;
import theblockbox.huntersdream.world.gen.village.component.StructureVillageHuntersCabin;
import theblockbox.huntersdream.world.gen.village.handler.VillageCastleHandler;
import theblockbox.huntersdream.world.gen.village.handler.VillageHuntersCabinHandler;

public class StructureInit {
	public static final ResourceLocation CASTLE = GeneralHelper.newResLoc("castle");
	public static final ResourceLocation HUNTERS_CABIN = GeneralHelper.newResLoc("hunters_cabin");

	public static void register() {
		if (ConfigHandler.server.generateCastle) {
			VillagerRegistry.instance().registerVillageCreationHandler(new VillageCastleHandler());
			MapGenStructureIO.registerStructureComponent(StructureVillageCastle.class, Reference.MODID + ":castle");
		}
		if (ConfigHandler.server.generateHuntersCabin) {
			VillagerRegistry.instance().registerVillageCreationHandler(new VillageHuntersCabinHandler());
			MapGenStructureIO.registerStructureComponent(StructureVillageHuntersCabin.class,
					Reference.MODID + ":hunter_scabin");
		}
	}
}
