package theblockbox.huntersdream.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryInit {
	public static final String[] SILVER_NAMES = { "blockSilver", "oreSilver", "ingotSilver", "helmetSilver",
			"chestplateSilver", "leggingsSilver", "bootsSilver", "axeSilver", "pickaxeSilver", "hoeSilver",
			"swordSilver", "shovelSilver" };

	public static void registerOres() {
		// Silver
		OreDictionary.registerOre("blockSilver", Item.getByNameOrId("huntersdream:block_silver"));
		OreDictionary.registerOre("oreSilver", Item.getByNameOrId("huntersdream:ingot_silver"));
		OreDictionary.registerOre("ingotSilver", Item.getByNameOrId("huntersdream:ingot_silver"));

		OreDictionaryInit.registerOreWithoutDamage("helmetSilver", "huntersdream:helmet_silver");
		OreDictionaryInit.registerOreWithoutDamage("chestplateSilver", "huntersdream:chestplate_silver");
		OreDictionaryInit.registerOreWithoutDamage("leggingsSilver", "huntersdream:leggings_silver");
		OreDictionaryInit.registerOreWithoutDamage("bootsSilver", "huntersdream:boots_silver");

		OreDictionaryInit.registerOreWithoutDamage("axeSilver", "huntersdream:axe_silver");
		OreDictionaryInit.registerOreWithoutDamage("pickaxeSilver", "huntersdream:pickaxe_silver");
		OreDictionaryInit.registerOreWithoutDamage("hoeSilver", "huntersdream:hoe_silver");
		OreDictionaryInit.registerOreWithoutDamage("swordSilver", "huntersdream:sword_silver");
		OreDictionaryInit.registerOreWithoutDamage("shovelSilver", "huntersdream:shovel_silver");
	}

	private static void registerOreWithoutDamage(String name, String item) {
		OreDictionary.registerOre(name, new ItemStack(Item.getByNameOrId(item), 1, OreDictionary.WILDCARD_VALUE));
	}
}
