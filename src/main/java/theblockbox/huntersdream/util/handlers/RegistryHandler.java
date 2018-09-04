package theblockbox.huntersdream.util.handlers;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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
import theblockbox.huntersdream.commands.CommandsMoonphase;
import theblockbox.huntersdream.commands.CommandsRitual;
import theblockbox.huntersdream.commands.CommandsTransformation;
import theblockbox.huntersdream.commands.CommandsTransformationLevel;
import theblockbox.huntersdream.commands.CommandsTransformationTexture;
import theblockbox.huntersdream.entity.EntityGoblinTD;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.init.CapabilitiesInit;
import theblockbox.huntersdream.init.EntityInit;
import theblockbox.huntersdream.init.ItemInit;
import theblockbox.huntersdream.init.PotionInit;
import theblockbox.huntersdream.init.SoundInit;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.compat.OreDictionaryCompat;
import theblockbox.huntersdream.util.effectiveagainsttransformation.ArmorEffectiveAgainstTransformation;
import theblockbox.huntersdream.util.effectiveagainsttransformation.EffectiveAgainstTransformation;
import theblockbox.huntersdream.util.enums.Transformations;
import theblockbox.huntersdream.util.interfaces.functional.IHasModel;
import theblockbox.huntersdream.util.interfaces.transformation.IUntransformedCreatureExtraData;
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
		// new RegistryBuilder<?>();
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
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
	}

	public static void initCommon(FMLInitializationEvent event) {
		PacketHandler.register();
		OreDictionaryCompat.registerOres();
		GameRegistry.addSmelting(BlockInit.ORE_SILVER, new ItemStack(ItemInit.INGOT_SILVER), 0.9F);
		MinecraftForge.addGrassSeed(new ItemStack(Item.getItemFromBlock(BlockInit.WOLFSBANE)), 5);
	}

	@SuppressWarnings("deprecation")
	public static void postInitCommon(FMLPostInitializationEvent event) {
		// register objects that are effective against a specific transformation
		OreDictionaryCompat.getSilver().forEach(item -> new EffectiveAgainstTransformation(object -> {
			return (object == item || (object instanceof ItemStack && ((ItemStack) object).getItem() == item));
		}, true, new Transformations[] { Transformations.WEREWOLF },
				new float[] { EffectiveAgainstTransformation.DEFAULT_EFFECTIVENESS }));
		ArmorEffectiveAgainstTransformation.registerArmorSet("Silver",
				new float[][] { new float[] { 1.35F }, new float[] { 1.85F }, new float[] { 1.65F },
						new float[] { 1.25F } },
				new float[][] { new float[] { 1.2F }, new float[] { 1.6F }, new float[] { 1.3F },
						new float[] { 1.1F } },
				Transformations.WEREWOLF);
		new EffectiveAgainstTransformation(obj -> {
			if (obj instanceof EntityTippedArrow) {
				EntityTippedArrow arrow = (EntityTippedArrow) obj;
				// using AT to access fields
				for (PotionEffect effect : arrow.potion.getEffects())
					if (effect.getPotion() == MobEffects.POISON || effect.getPotion() == PotionInit.POTION_WOLFSBANE)
						return true;
				for (PotionEffect effect : arrow.customPotionEffects)
					if (effect.getPotion() == MobEffects.POISON || effect.getPotion() == PotionInit.POTION_WOLFSBANE)
						return true;
			}
			return false;
		}, false, new Transformations[] { Transformations.WEREWOLF },
				// new float[] { EffectiveAgainstTransformation.DEFAULT_EFFECTIVENESS });
				new float[] { 1F });

		// To avoid confusion: In the following code the field EntityVillager#careerId
		// and the method EntityVillager#populateBuyingList are accessed with Access
		// Transformers
		IUntransformedCreatureExtraData.<EntityVillager>ofWithBuffer((villager, buffer) -> {
			try {
				villager.setProfession(buffer.getInt());
				villager.careerId = buffer.getInt();
				villager.populateBuyingList();
			} catch (BufferUnderflowException e) {
				// just do nothing, villager automatically gets a profession
			}
		}, villager -> ByteBuffer.allocate(8).putInt(villager.getProfession()).putInt(villager.careerId),
				c -> (c instanceof EntityVillager) && !(c instanceof EntityGoblinTD), Transformations.WEREWOLF,
				Transformations.VAMPIRE);

		IUntransformedCreatureExtraData.<EntityGoblinTD>ofWithBuffer((goblin, buffer) -> {
			goblin.setProfession(buffer.getInt());
			goblin.setTexture(buffer.get());
			goblin.careerId = buffer.getInt();
			goblin.populateBuyingList();
		}, goblin -> ByteBuffer.allocate(9).putInt(goblin.getProfession()).put(goblin.getTexture())
				.putInt(goblin.careerId), c -> c instanceof EntityGoblinTD);
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
