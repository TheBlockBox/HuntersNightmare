package theblockbox.huntersdream.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.handlers.ConfigHandler;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.world.gen.village.StructureVillageVillagerCastle;

public class StructureInit {
    // in villages
    public static final ResourceLocation VILLAGER_CASTLE_OAK = GeneralHelper.newResLoc("villagercastle_oak");
    public static final ResourceLocation VILLAGER_CASTLE_SPRUCE = GeneralHelper.newResLoc("villagercastle_spruce");
    public static final ResourceLocation VILLAGER_CASTLE_ACACIA = GeneralHelper.newResLoc("villagercastle_acacia");
    public static final ResourceLocation VILLAGER_CASTLE_SANDSTONE = GeneralHelper.newResLoc("villagercastle_sandstone");
    public static final ResourceLocation HUNTERS_CABIN = GeneralHelper.newResLoc("hunters_cabin");
    // somewhere else
    public static final ResourceLocation HUNTERS_CAMP = GeneralHelper.newResLoc("hunters_camp");
    public static final ResourceLocation WEREWOLF_CABIN = GeneralHelper.newResLoc("werewolf_cabin");

    public static void register() {
        if (ConfigHandler.server.generateVillagerCastle) {
            VillagerRegistry.instance().registerVillageCreationHandler(new StructureVillageVillagerCastle.VillageVillagerCastleHandler());
            MapGenStructureIO.registerStructureComponent(StructureVillageVillagerCastle.class,
                    Reference.MODID + ":villager_castle");
        }
// TODO: Add structure and uncomment
//		if (ConfigHandler.server.generateHuntersCabin) {
//			VillagerRegistry.instance().registerVillageCreationHandler(new StructureVillageHuntersCabin.VillageHuntersCabinHandler());
//			MapGenStructureIO.registerStructureComponent(StructureVillageHuntersCabin.class,
//					Reference.MODID + ":hunters_cabin");
//		}
    }
}