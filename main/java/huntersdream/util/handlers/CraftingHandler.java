package huntersdream.util.handlers;

import huntersdream.init.BlockInit;
import huntersdream.init.ItemInit;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class is for recipes that (apparently) can't be made with JSON files
 */
public class CraftingHandler {
	public static void registerSmelting() {
		GameRegistry.addSmelting(BlockInit.OVERWORLD_ORE_SILVER, new ItemStack(ItemInit.INGOT_SILVER), 10);
	}
}
