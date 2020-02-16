package theblockbox.huntersdream.api.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import theblockbox.huntersdream.util.Reference;

/**
 * @author Ms. Random (discord: "Ms. Random#5794")
 */

public class CreativeTabInit {

    public static final CreativeTabs HUNTERSDREAM_TOOLS_AND_WEAPONS = new CreativeTabs(
            Reference.MODID + ".toolsandcombat") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.SILVER_SWORD);
        }
    };

    public static final CreativeTabs HUNTERSDREAM_MISC = new CreativeTabs(Reference.MODID + ".misc") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemInit.SILVER_INGOT);
        }
    };
}