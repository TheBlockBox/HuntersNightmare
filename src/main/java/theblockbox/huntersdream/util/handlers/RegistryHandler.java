package theblockbox.huntersdream.util.handlers;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.blocks.tileentity.TileEntityCampfire;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySilverFurnace;
import theblockbox.huntersdream.commands.CommandMoonphase;
import theblockbox.huntersdream.commands.CommandSkill;
import theblockbox.huntersdream.commands.CommandTransformation;
import theblockbox.huntersdream.commands.CommandTransformationTexture;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.EntityInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.init.LootTableInit;
import theblockbox.huntersdream.init.OreDictionaryInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.init.SoundInit;
import theblockbox.huntersdream.init.StructureInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.SilverFurnaceRecipe;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.world.gen.WorldGenOres;

/**
 * In this class everythings gets registered (items, blocks, entities, biomes,
 * ores, event handlers etc.)
 */
@Mod.EventBusSubscriber(modid = Reference.MODID)
public class RegistryHandler {
	private static File directory;

	// Registry events

	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		BlockInit.onBlockRegister(event);
	}

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		ItemInit.onItemRegister(event);
	}

	@SubscribeEvent
	public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event) {
		EntityInit.registerEntities(event);
	}

	@SubscribeEvent
	public static void onPotionRegister(RegistryEvent.Register<Potion> event) {
		PotionInit.registerPotions(event);
	}

	@SubscribeEvent
	public static void onPotionTypeRegister(RegistryEvent.Register<PotionType> event) {
		PotionInit.registerPotionTypes(event);
	}

	@SubscribeEvent
	public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(SoundInit.SOUND_EVENTS.toArray(new SoundEvent[0]));
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		ItemInit.ITEMS.forEach(item -> Main.proxy.registerItemRenderer(item, 0, "inventory"));
		EntityInit.registerEntityRenders();
	}

	// Common

	public static void preInitCommon(FMLPreInitializationEvent event) {
		CapabilitiesInit.registerCapabilities();
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
		Transformation.preInit();
		Skill.preInit();
		GameRegistry.registerTileEntity(TileEntitySilverFurnace.class, GeneralHelper.newResLoc("furnace_silver"));
		GameRegistry.registerTileEntity(TileEntityCampfire.class, GeneralHelper.newResLoc("campfire"));
		RegistryHandler.directory = event.getModConfigurationDirectory();
	}

	public static void initCommon(FMLInitializationEvent event) {
		PacketHandler.register();
		GameRegistry.addSmelting(BlockInit.ORE_SILVER, new ItemStack(ItemInit.INGOT_SILVER), 0.9F);
		MinecraftForge.addGrassSeed(new ItemStack(BlockInit.WOLFSBANE), 5);
		LootTableInit.register();
		StructureInit.register();
		OreDictionaryInit.registerOres();
		GameRegistry.registerWorldGenerator(new WorldGenOres(), 0);
	}

	public static void postInitCommon(FMLPostInitializationEvent event) {
	}

	// Client

	public static void preInitClient() {
		KeyBindingInit.register();
	}

	public static void initClient() {
	}

	public static void postInitClient() {
	}

	// Server

	public static void preInitServer() {
	}

	public static void initServer() {
	}

	public static void postInitServer() {
		SilverFurnaceRecipe.setAndLoadFiles(RegistryHandler.directory);
	}

	public static void onServerStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandMoonphase());
		event.registerServerCommand(new CommandTransformation());
		event.registerServerCommand(new CommandTransformationTexture());
		event.registerServerCommand(new CommandSkill());
	}
}
