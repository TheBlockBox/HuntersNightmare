package huntersdream.util.compat;

import huntersdream.init.BlockInit;
import huntersdream.init.ItemInit;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryCompat {
	public static void registerOres() {
		// Silver
		OreDictionary.registerOre("oreSilver", BlockInit.OVERWORLD_ORE_SILVER);
		OreDictionary.registerOre("blockSilver", BlockInit.BLOCK_SILVER);

		OreDictionary.registerOre("ingotSilver", ItemInit.INGOT_SILVER);
		OreDictionary.registerOre("helmetSilver", ItemInit.HELMET_SILVER);
		OreDictionary.registerOre("chestplateSilver", ItemInit.CHESTPLATE_SILVER);
		OreDictionary.registerOre("leggingsSilver", ItemInit.LEGGINGS_SILVER);
		OreDictionary.registerOre("bootsSilver", ItemInit.BOOTS_SILVER);

		// To-Do
		OreDictionary.registerOre("axeSilver", ItemInit.INGOT_SILVER);
		OreDictionary.registerOre("pickaxeSilver", ItemInit.HELMET_SILVER);
		OreDictionary.registerOre("hoeSilver", ItemInit.CHESTPLATE_SILVER);
		OreDictionary.registerOre("swordSilver", ItemInit.LEGGINGS_SILVER);
		OreDictionary.registerOre("shovelSilver", ItemInit.BOOTS_SILVER);
	}
}
