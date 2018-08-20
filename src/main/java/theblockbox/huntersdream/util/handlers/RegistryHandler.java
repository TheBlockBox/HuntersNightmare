package theblockbox.huntersdream.util.handlers;

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
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.RegistryBuilder;
import theblockbox.huntersdream.commands.CommandsMoonphase;
import theblockbox.huntersdream.commands.CommandsRitual;
import theblockbox.huntersdream.commands.CommandsTransformation;
import theblockbox.huntersdream.commands.CommandsTransformationLevel;
import theblockbox.huntersdream.commands.CommandsTransformationTexture;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.EntityInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.init.SoundInit;
import theblockbox.huntersdream.util.EffectiveAgainstTransformationRegistry;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.compat.OreDictionaryCompat;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.interfaces.effective.ArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.EffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.effective.EffectiveAgainstTransformation.ItemEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;
import theblockbox.huntersdream.world.gen.WorldGenCustomOres;

/**
 * In this class everythings gets registered (items, blocks, entities, biomes,
 * ores, event handlers etc.)
 */
@Mod.EventBusSubscriber(modid = Reference.MODID)
public class RegistryHandler {

	// Registry events

	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
	}

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
	}

	@SubscribeEvent
	public static void onRegistryRegister(RegistryEvent.NewRegistry event) {
		new RegistryBuilder<EffectiveAgainstTransformationRegistry>()
				.setName(GeneralHelper.newResLoc("effectiveagainsttransformation"))
				.setType(EffectiveAgainstTransformationRegistry.class).create();
	}

	@SubscribeEvent
	public static void onEffectiveAgainstTransformationRegister(
			RegistryEvent.Register<EffectiveAgainstTransformationRegistry> event) {
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
	public static void onPotionTypeRegister(RegistryEvent.Register<PotionType> event) {
		PotionInit.registerPotionTypes();
		event.getRegistry().registerAll(PotionInit.POTION_TYPES.toArray(new PotionType[0]));
	}

	@SubscribeEvent
	public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(SoundInit.SOUND_EVENTS.toArray(new SoundEvent[0]));
	}

	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		for (Item item : ItemInit.ITEMS)
			if (item instanceof IHasModel)
				((IHasModel) item).registerModels();

		for (Block block : BlockInit.BLOCKS)
			if (block instanceof IHasModel)
				((IHasModel) block).registerModels();

		EntityInit.registerEntityRenders();
	}

	// Common

	/**
	 * Is called on {@link FMLPreInitializationEvent} before
	 * {@link #preInitCommon(FMLPreInitializationEvent)}
	 */
	public static void gameRegistry(FMLPreInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new WorldGenCustomOres(), 0);
	}

	public static void preInitCommon(FMLPreInitializationEvent event) {
		CapabilitiesInit.registerCapabilities();
	}

	public static void initCommon(FMLInitializationEvent event) {
		PacketHandler.register();
		OreDictionaryCompat.registerOres();
		GameRegistry.addSmelting(BlockInit.ORE_SILVER, new ItemStack(ItemInit.INGOT_SILVER), 0.9F);
		MinecraftForge.addGrassSeed(new ItemStack(Item.getItemFromBlock(BlockInit.WOLFSBANE)), 5);
	}

	public static void postInitCommon(FMLPostInitializationEvent event) {
		// register items that are effective against a specific transformation
		OreDictionaryCompat.getSilver().forEach(item -> new ItemEffectiveAgainstTransformation(item,
				EffectiveAgainstTransformation.DEFAULT_EFFECTIVENESS, Transformations.WEREWOLF));
		OreDictionary.getOres("helmetSilver").parallelStream()
				.forEach(stack -> new ArmorEffectiveAgainstTransformation(stack.getItem(), 1.35F, 1.2F,
						Transformations.WEREWOLF));
		OreDictionary.getOres("chestplateSilver").parallelStream()
				.forEach(stack -> new ArmorEffectiveAgainstTransformation(stack.getItem(), 1.85F, 1.6F,
						Transformations.WEREWOLF));
		OreDictionary.getOres("leggingsSilver").parallelStream()
				.forEach(stack -> new ArmorEffectiveAgainstTransformation(stack.getItem(), 1.65F, 1.3F,
						Transformations.WEREWOLF));
		OreDictionary.getOres("bootsSilver").parallelStream().forEach(stack -> {
			new ArmorEffectiveAgainstTransformation(stack.getItem(), 1.25F, 1.1F, Transformations.WEREWOLF);
			System.out.println("oh one");
		});
	}

	// Client

	public static void preInitClient() {
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
	}

	public static void serverRegistries(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandsMoonphase());
		event.registerServerCommand(new CommandsTransformationLevel());
		event.registerServerCommand(new CommandsTransformation());
		event.registerServerCommand(new CommandsTransformationTexture());
		event.registerServerCommand(new CommandsRitual());
	}
}
