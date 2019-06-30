package theblockbox.huntersdream.api.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.Reference;

public class LootTableInit {
    public static final ResourceLocation LYCANTHROPY_BOOK = GeneralHelper.newResLoc("lycanthropy_book");
    public static final LootEntry LYCANTHROPY_BOOK_ENTRY = new LootEntryTable(LootTableInit.LYCANTHROPY_BOOK, 1, 0,
            new LootCondition[0], Reference.MODID + "_inject_entry");
    public static final LootPool LYCANTHROPY_BOOK_POOL = new LootPool(
            new LootEntry[]{LootTableInit.LYCANTHROPY_BOOK_ENTRY}, new LootCondition[0], new RandomValueRange(1.0F),
            new RandomValueRange(0), Reference.MODID + "_inject_pool");

    public static void register() {
        LootTableList.register(LootTableInit.LYCANTHROPY_BOOK);
    }
}
