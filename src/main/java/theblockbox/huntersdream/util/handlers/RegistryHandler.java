package theblockbox.huntersdream.util.handlers;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CompoundIngredient;
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
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.*;
import theblockbox.huntersdream.api.skill.Skill;
import theblockbox.huntersdream.blocks.tileentity.TileEntityCampfire;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySpinningWheel;
import theblockbox.huntersdream.commands.CommandMoonphase;
import theblockbox.huntersdream.commands.CommandSkill;
import theblockbox.huntersdream.commands.CommandTransformation;
import theblockbox.huntersdream.commands.CommandTransformationTexture;
import theblockbox.huntersdream.items.RecipeAddHunterArmorEffects;
import theblockbox.huntersdream.items.RecipeHunterArmorDyes;
import theblockbox.huntersdream.items.RecipeRemoveHunterArmorEffects;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.world.gen.WorldGenOres;

import java.io.File;
import java.util.Arrays;

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
    public static void onIRecipeRegister(RegistryEvent.Register<IRecipe> event) {
        ResourceLocation registryName = GeneralHelper.newResLoc("wolfsbane_potion");
        event.getRegistry().registerAll(new ShapelessOreRecipe(registryName, PotionUtils.addPotionToItemStack(
                new ItemStack(Items.POTIONITEM), PotionInit.WOLFSBANE), new CompoundIngredient(Arrays.asList(
                new OreIngredient("aconite"), new OreIngredient("wolfsbane"), new OreIngredient("monkshood"))) {
                }, Items.GOLDEN_APPLE, Items.MAGMA_CREAM, Items.POTIONITEM).setRegistryName(registryName),
                new RecipeHunterArmorDyes().setRegistryName(GeneralHelper.newResLoc("hunter_armor_dyes")),
                new RecipeAddHunterArmorEffects().setRegistryName(GeneralHelper.newResLoc("hunter_armor_effects_add")),
                new RecipeRemoveHunterArmorEffects().setRegistryName(GeneralHelper.newResLoc("hunter_armor_effects_remove")));
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
        GameRegistry.registerTileEntity(TileEntityCampfire.class, GeneralHelper.newResLoc("campfire"));
        GameRegistry.registerTileEntity(TileEntitySpinningWheel.class, GeneralHelper.newResLoc("spinning_wheel"));
        RegistryHandler.directory = event.getModConfigurationDirectory();
        MinecraftForge.TERRAIN_GEN_BUS.register(EventHandler.class);
        WerewolfTransformationOverlayInit.preInit();
    }

    public static void initCommon(FMLInitializationEvent event) {
        PacketHandler.register();
        GameRegistry.addSmelting(BlockInit.SILVER_ORE, new ItemStack(ItemInit.SILVER_INGOT), 0.9F);
        LootTableInit.register();
        StructureInit.register();
        OreDictionaryInit.registerOres();
        ParticleCommonInit.init();
        GameRegistry.registerWorldGenerator(new WorldGenOres(), 0);
        for (Biome biome : Biome.REGISTRY) {
            biome.addFlower(BlockInit.ACONITE_FLOWER.getDefaultState(), 1);
            biome.addFlower(BlockInit.MONKSHOOD_FLOWER.getDefaultState(), 1);
        }
    }

    public static void postInitCommon(FMLPostInitializationEvent event) {
    }

    // Client

    public static void preInitClient() {
    }

    public static void initClient() {
        ParticleClientInit.registerParticles();
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

    public static void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMoonphase());
        event.registerServerCommand(new CommandTransformation());
        event.registerServerCommand(new CommandTransformationTexture());
        event.registerServerCommand(new CommandSkill());
    }
}
