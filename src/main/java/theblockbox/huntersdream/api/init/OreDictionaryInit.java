package theblockbox.huntersdream.api.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import theblockbox.huntersdream.util.Reference;

public class OreDictionaryInit {
    public static final String[] SILVER_NAMES = {"blockSilver", "oreSilver", "ingotSilver", "helmetSilver",
            "chestplateSilver", "leggingsSilver", "bootsSilver", "axeSilver", "pickaxeSilver", "hoeSilver",
            "swordSilver", "shovelSilver"};
    public static final String[] ACONITE_NAMES = {"aconite", "wolfsbane", "monkshood"};

    public static void registerOres() {
        // Silver
        OreDictionary.registerOre("blockSilver", Item.getByNameOrId(Reference.MODID + ":silver_block"));
        OreDictionary.registerOre("oreSilver", Item.getByNameOrId(Reference.MODID + ":silver_ore"));
        OreDictionary.registerOre("ingotSilver", Item.getByNameOrId(Reference.MODID + ":silver_ingot"));

        OreDictionaryInit.registerOreWithoutDamage("helmetSilver", "silver_helmet");
        OreDictionaryInit.registerOreWithoutDamage("chestplateSilver", "silver_chestplate");
        OreDictionaryInit.registerOreWithoutDamage("leggingsSilver", "silver_leggings");
        OreDictionaryInit.registerOreWithoutDamage("bootsSilver", "silver_boots");

        OreDictionaryInit.registerOreWithoutDamage("axeSilver", "silver_axe");
        OreDictionaryInit.registerOreWithoutDamage("pickaxeSilver", "silver_pickaxe");
        OreDictionaryInit.registerOreWithoutDamage("hoeSilver", "silver_hoe");
        OreDictionaryInit.registerOreWithoutDamage("swordSilver", "silver_sword");
        OreDictionaryInit.registerOreWithoutDamage("shovelSilver", "silver_shovel");

        OreDictionaryInit.registerOreWithoutDamage("hunterHat", "hunter_hat");
        OreDictionaryInit.registerOreWithoutDamage("hunterTrenchcoat", "hunter_coat");
        OreDictionaryInit.registerOreWithoutDamage("hunterPants", "hunter_pants");
        OreDictionaryInit.registerOreWithoutDamage("hunterBoots", "hunter_boots");

        // wolfsbane
        OreDictionary.registerOre("aconite", BlockInit.ACONITE_FLOWER);
        OreDictionary.registerOre("wolfsbane", BlockInit.ACONITE_FLOWER);
        OreDictionary.registerOre("monkshood", BlockInit.MONKSHOOD_FLOWER);
        OreDictionary.registerOre("wolfsbane", BlockInit.MONKSHOOD_FLOWER);

        // misc
        OreDictionary.registerOre("cotton", Item.getByNameOrId(Reference.MODID + ":cotton"));
        OreDictionary.registerOre("cropCotton", Item.getByNameOrId(Reference.MODID + ":cotton"));
        OreDictionary.registerOre("seedCotton", Item.getByNameOrId(Reference.MODID + ":cotton"));
        OreDictionary.registerOre("fabric", Item.getByNameOrId(Reference.MODID + ":fabric"));
    }

    private static void registerOreWithoutDamage(String name, String item) {
        OreDictionary.registerOre(name, new ItemStack(Item.getByNameOrId(Reference.MODID + ":" + item), 1, OreDictionary.WILDCARD_VALUE));
    }
}
