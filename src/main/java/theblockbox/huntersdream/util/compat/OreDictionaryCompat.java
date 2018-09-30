package theblockbox.huntersdream.util.compat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.ItemInit;

public class OreDictionaryCompat {
	/**
	 * A string array of four strings for the conventional ore dictionary names for
	 * armor parts (helmet, chestplate, leggings and boots)
	 */
	public static final String[] ARMOR_PART_NAMES = { "helmet", "chestplate", "leggings", "boots" };
	public static final String[] SILVER_NAMES = { "blockSilver", "oreSilver", "ingotSilver", "helmetSilver",
			"chestplateSilver", "leggingsSilver", "bootsSilver", "axeSilver", "pickaxeSilver", "hoeSilver",
			"swordSilver", "shovelSilver" };

	public static void registerOres() {
		// Silver
		OreDictionary.registerOre("blockSilver", BlockInit.BLOCK_SILVER);
		OreDictionary.registerOre("oreSilver", BlockInit.ORE_SILVER);
		OreDictionary.registerOre("ingotSilver", ItemInit.INGOT_SILVER);

		registerOreWithoutDamage("helmetSilver", ItemInit.HELMET_SILVER);
		registerOreWithoutDamage("chestplateSilver", ItemInit.CHESTPLATE_SILVER);
		registerOreWithoutDamage("leggingsSilver", ItemInit.LEGGINGS_SILVER);
		registerOreWithoutDamage("bootsSilver", ItemInit.BOOTS_SILVER);

		registerOreWithoutDamage("axeSilver", ItemInit.AXE_SILVER);
		registerOreWithoutDamage("pickaxeSilver", ItemInit.PICKAXE_SILVER);
		registerOreWithoutDamage("hoeSilver", ItemInit.HOE_SILVER);
		registerOreWithoutDamage("swordSilver", ItemInit.SWORD_SILVER);
		registerOreWithoutDamage("shovelSilver", ItemInit.SHOVEL_SILVER);
	}

	private static void registerOreWithoutDamage(String name, Item item) {
		OreDictionary.registerOre(name, new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE));
	}
}
