package theblockbox.huntersdream.util.compat;

import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.ItemInit;

public class OreDictionaryCompat {
	public static void registerOres() {
		// Silver
		OreDictionary.registerOre("blockPureSilver", BlockInit.BLOCK_PURE_SILVER);

		OreDictionary.registerOre("ingotPureSilver", ItemInit.INGOT_PURE_SILVER);
		OreDictionary.registerOre("helmetPureSilver", ItemInit.HELMET_PURE_SILVER);
		OreDictionary.registerOre("chestplatePureSilver", ItemInit.CHESTPLATE_PURE_SILVER);
		OreDictionary.registerOre("leggingsPureSilver", ItemInit.LEGGINGS_PURE_SILVER);
		OreDictionary.registerOre("bootsPureSilver", ItemInit.BOOTS_PURE_SILVER);

		OreDictionary.registerOre("axePureSilver", ItemInit.AXE_PURE_SILVER);
		OreDictionary.registerOre("pickaxePureSilver", ItemInit.HELMET_PURE_SILVER);
		OreDictionary.registerOre("hoePureSilver", ItemInit.CHESTPLATE_PURE_SILVER);
		OreDictionary.registerOre("swordPureSilver", ItemInit.LEGGINGS_PURE_SILVER);
		OreDictionary.registerOre("shovelPurelSilver", ItemInit.BOOTS_PURE_SILVER);
	}
}
