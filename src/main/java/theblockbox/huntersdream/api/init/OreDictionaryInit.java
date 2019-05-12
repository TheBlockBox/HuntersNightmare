package theblockbox.huntersdream.api.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryInit {
    public static final String[] SILVER_NAMES = {"blockSilver", "oreSilver", "ingotSilver", "helmetSilver",
            "chestplateSilver", "leggingsSilver", "bootsSilver", "axeSilver", "pickaxeSilver", "hoeSilver",
            "swordSilver", "shovelSilver"};

    public static void registerOres() {
        // Silver
        OreDictionary.registerOre("blockSilver", Item.getByNameOrId("huntersdream:silver_block"));
        OreDictionary.registerOre("oreSilver", Item.getByNameOrId("huntersdream:silver_ore"));
        OreDictionary.registerOre("ingotSilver", Item.getByNameOrId("huntersdream:silver_ingot"));

        OreDictionaryInit.registerOreWithoutDamage("helmetSilver", "huntersdream:silver_helmet");
        OreDictionaryInit.registerOreWithoutDamage("chestplateSilver", "huntersdream:silver_chestplate");
        OreDictionaryInit.registerOreWithoutDamage("leggingsSilver", "huntersdream:silver_leggings");
        OreDictionaryInit.registerOreWithoutDamage("bootsSilver", "huntersdream:silver_boots");

        OreDictionaryInit.registerOreWithoutDamage("axeSilver", "huntersdream:silver_axe");
        OreDictionaryInit.registerOreWithoutDamage("pickaxeSilver", "huntersdream:silver_pickaxe");
        OreDictionaryInit.registerOreWithoutDamage("hoeSilver", "huntersdream:silver_hoe");
        OreDictionaryInit.registerOreWithoutDamage("swordSilver", "huntersdream:silver_sword");
        OreDictionaryInit.registerOreWithoutDamage("shovelSilver", "huntersdream:silver_shovel");

        OreDictionary.registerOre("aconite", BlockInit.ACONITE_FLOWER);
        OreDictionary.registerOre("wolfsbane", BlockInit.ACONITE_FLOWER);
    }

    private static void registerOreWithoutDamage(String name, String item) {
        OreDictionary.registerOre(name, new ItemStack(Item.getByNameOrId(item), 1, OreDictionary.WILDCARD_VALUE));
    }
}
