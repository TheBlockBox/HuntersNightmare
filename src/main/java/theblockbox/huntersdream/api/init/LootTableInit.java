package theblockbox.huntersdream.api.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.Reference;

public class LootTableInit {
    public static final ResourceLocation VILLAGE_CHESTS = GeneralHelper.newResLoc("village_chests");
    public static final LootEntry VILLAGE_CHESTS_ENTRY = new LootEntryTable(LootTableInit.VILLAGE_CHESTS, 1, 0,
            new LootCondition[0], Reference.MODID + "_inject_entry");
    public static final LootPool VILLAGE_CHESTS_POOL = new LootPool(
            new LootEntry[]{LootTableInit.VILLAGE_CHESTS_ENTRY}, new LootCondition[0], new RandomValueRange(1.0F),
            new RandomValueRange(0), Reference.MODID + "_inject_pool");

    public static void register() {
        LootTableList.register(LootTableInit.VILLAGE_CHESTS);
    }
}
