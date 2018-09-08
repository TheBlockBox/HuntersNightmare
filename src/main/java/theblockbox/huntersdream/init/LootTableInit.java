package theblockbox.huntersdream.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class LootTableInit {
	public static final ResourceLocation HUNTERS_JOURNAL_PAGE = GeneralHelper.newResLoc("hunters_journal_page");
	public static final LootEntry HUNTERS_JOURNAL_PAGE_ENTRY = new LootEntryTable(HUNTERS_JOURNAL_PAGE, 1, 0,
			new LootCondition[0], Reference.MODID + "_inject_entry");
	public static final LootPool HUNTERS_JOURNAL_PAGE_POOL = new LootPool(
			new LootEntry[] { HUNTERS_JOURNAL_PAGE_ENTRY }, new LootCondition[0], new RandomValueRange(1F),
			new RandomValueRange(0), Reference.MODID + "_inject_pool");

	public static void register() {
		LootTableList.register(HUNTERS_JOURNAL_PAGE);
	}
}
