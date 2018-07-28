package theblockbox.huntersdream.util.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.commands.CommandsMoonphase;
import theblockbox.huntersdream.commands.CommandsTransformation;
import theblockbox.huntersdream.commands.CommandsTransformationLevel;
import theblockbox.huntersdream.commands.CommandsTransformationTexture;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.EntityInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.util.compat.OreDictionaryCompat;
import theblockbox.huntersdream.util.interfaces.IHasModel;
import theblockbox.huntersdream.world.gen.WorldGenCustomOres;

/**
 * In this class everythings gets registered (items, blocks, entities, biomes,
 * ores etc.)
 */
@Mod.EventBusSubscriber
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
	public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event) {
		EntityInit.registerEntities(event);
	}

	@SubscribeEvent
	public static void onPotionRegister(RegistryEvent.Register<Potion> event) {
		event.getRegistry().registerAll(PotionInit.POTIONS.toArray(new Potion[0]));
	}

	@SubscribeEvent
	public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
		// event.getRegistry().registerAll(values);
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

		EntityInit.registerEntityRenders();
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
		CapabilitiesInit.registerCapabilities();
	}

	public static void initRegistries(FMLInitializationEvent event) {
		Main.proxy.init();
		OreDictionaryCompat.registerOres();
		PacketHandler.register();
	}

	public static void postInitRegistries(FMLPostInitializationEvent event) {
		Main.proxy.postInit();
	}

	public static void serverRegistries(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandsMoonphase());
		event.registerServerCommand(new CommandsTransformationLevel());
		event.registerServerCommand(new CommandsTransformation());
		event.registerServerCommand(new CommandsTransformationTexture());
	}
}
