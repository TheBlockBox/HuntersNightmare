package theblockbox.huntersdream.api.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.world.gen.village.StructureVillageModifiedField;
import theblockbox.huntersdream.world.gen.village.StructureVillageVillagerCastle;

public class StructureInit {
    // only in villages
    public static final ResourceLocation VILLAGER_CASTLE = GeneralHelper.newResLoc("villager_castle");
    public static final ResourceLocation HUNTERS_CABIN = GeneralHelper.newResLoc("hunters_cabin");
    // in the whole world
    public static final ResourceLocation HUNTERS_CAMP = GeneralHelper.newResLoc("hunters_camp");
    public static final ResourceLocation WEREWOLF_CABIN = GeneralHelper.newResLoc("werewolf_cabin");

    public static void register() {
        if (ConfigHandler.server.generateVillagerCastle) {
            VillagerRegistry.instance().registerVillageCreationHandler(new StructureVillageVillagerCastle.VillageVillagerCastleHandler());
            MapGenStructureIO.registerStructureComponent(StructureVillageVillagerCastle.class,
                    Reference.MODID + ":villager_castle");
        }
        if (ConfigHandler.server.generateHealingHerb) {
            VillagerRegistry.instance().registerVillageCreationHandler(new StructureVillageModifiedField.VillageModifiedFieldHandler());
            MapGenStructureIO.registerStructureComponent(StructureVillageModifiedField.class,
                    Reference.MODID + ":modified_field");
        }
        // TODO: Add structure and uncomment
//		if (ConfigHandler.server.generateHuntersCabin) {
//			VillagerRegistry.instance().registerVillageCreationHandler(new StructureVillageHuntersCabin.VillageHuntersCabinHandler());
//			MapGenStructureIO.registerStructureComponent(StructureVillageHuntersCabin.class,
//					Reference.MODID + ":hunters_cabin");
//		}
    }
}