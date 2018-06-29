package huntersdream.util.handlers;

import huntersdream.Main;
import huntersdream.init.BlockInit;
import huntersdream.init.EntityInit;
import huntersdream.init.ItemInit;
import huntersdream.util.compat.OreDictionaryCompat;
import huntersdream.util.interfaces.IHasModel;
import huntersdream.world.gen.WorldGenCustomOres;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * In this class everythings gets registered (items, blocks, entities, biomes,
 * ores etc.)
 */
@EventBusSubscriber
public class RegistryHandler {

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
	}

	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		for (Item item : ItemInit.ITEMS) {
			if (item instanceof IHasModel) {
				((IHasModel) item).registerModels();
			}
		}

		for (Block block : BlockInit.BLOCKS) {
			if (block instanceof IHasModel) {
				((IHasModel) block).registerModels();
			}
		}

		RenderHandler.registerEntityRenders();
	}

	/**
	 * Is called in preInit stage before preInitRegistries
	 */
	public static void otherRegistries(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new WorldGenCustomOres(), 0);
		ConfigHandler.registerConfig(event);
	}

	public static void preInitRegistries(FMLPreInitializationEvent event) {
		Main.proxy.preInit();
		EntityInit.registerEntities();
	}

	public static void initRegistries(FMLInitializationEvent event) {
		Main.proxy.init();
		OreDictionaryCompat.registerOres();
		CraftingHandler.registerSmelting();
	}

	public static void postInitRegistries(FMLPostInitializationEvent event) {
		Main.proxy.postInit();
	}
}
