package theblockbox.huntersdream.api.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.util.Reference;

public class LootTableInit {
	public static final ResourceLocation HUNTERS_JOURNAL_PAGE = GeneralHelper.newResLoc("hunters_journal_page");
	public static final LootEntry HUNTERS_JOURNAL_PAGE_ENTRY = new LootEntryTable(LootTableInit.HUNTERS_JOURNAL_PAGE, 1, 0,
			new LootCondition[0], Reference.MODID + "_inject_entry");
	public static final LootPool HUNTERS_JOURNAL_PAGE_POOL = new LootPool(
			new LootEntry[] {LootTableInit.HUNTERS_JOURNAL_PAGE_ENTRY}, new LootCondition[0], new RandomValueRange(1.0F),
			new RandomValueRange(0), Reference.MODID + "_inject_pool");

	public static void register() {
		LootTableList.register(LootTableInit.HUNTERS_JOURNAL_PAGE);
	}
}
