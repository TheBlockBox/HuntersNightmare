package theblockbox.huntersdream.util.compat;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryCompat {
	public static final String[] SILVER_NAMES = { "blockSilver", "oreSilver", "ingotSilver", "helmetSilver",
			"chestplateSilver", "leggingsSilver", "bootsSilver", "axeSilver", "pickaxeSilver", "hoeSilver",
			"swordSilver", "shovelSilver" };

	public static void registerOres() {
		// Silver
		OreDictionary.registerOre("blockSilver", Item.getByNameOrId("huntersdream:block_silver"));
		OreDictionary.registerOre("oreSilver", Item.getByNameOrId("huntersdream:ingot_silver"));
		OreDictionary.registerOre("ingotSilver", Item.getByNameOrId("huntersdream:ingot_silver"));

		registerOreWithoutDamage("helmetSilver", "huntersdream:helmet_silver");
		registerOreWithoutDamage("chestplateSilver", "huntersdream:chestplate_silver");
		registerOreWithoutDamage("leggingsSilver", "huntersdream:leggings_silver");
		registerOreWithoutDamage("bootsSilver", "huntersdream:boots_silver");

		registerOreWithoutDamage("axeSilver", "huntersdream:axe_silver");
		registerOreWithoutDamage("pickaxeSilver", "huntersdream:pickaxe_silver");
		registerOreWithoutDamage("hoeSilver", "huntersdream:hoe_silver");
		registerOreWithoutDamage("swordSilver", "huntersdream:sword_silver");
		registerOreWithoutDamage("shovelSilver", "huntersdream:shovel_silver");
	}

	private static void registerOreWithoutDamage(String name, String item) {
		OreDictionary.registerOre(name, new ItemStack(Item.getByNameOrId(item), 1, OreDictionary.WILDCARD_VALUE));
	}
}
