package theblockbox.huntersdream.util.compat;

import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.ItemInit;

public class OreDictionaryCompat {
	public static void registerOres() {
		// Silver
		OreDictionary.registerOre("blockSilver", BlockInit.BLOCK_SILVER);
		OreDictionary.registerOre("oreSilver", BlockInit.ORE_SILVER);

		OreDictionary.registerOre("ingotSilver", ItemInit.INGOT_SILVER);
		OreDictionary.registerOre("helmetSilver", ItemInit.HELMET_SILVER);
		OreDictionary.registerOre("chestplateSilver", ItemInit.CHESTPLATE_SILVER);
		OreDictionary.registerOre("leggingsSilver", ItemInit.LEGGINGS_SILVER);
		OreDictionary.registerOre("bootsSilver", ItemInit.BOOTS_SILVER);

		OreDictionary.registerOre("axeSilver", ItemInit.AXE_SILVER);
		OreDictionary.registerOre("pickaxeSilver", ItemInit.PICKAXE_SILVER);
		OreDictionary.registerOre("hoeSilver", ItemInit.HOE_SILVER);
		OreDictionary.registerOre("swordSilver", ItemInit.SWORD_SILVER);
		OreDictionary.registerOre("shovelSilver", ItemInit.SHOVEL_SILVER);
	}
}
