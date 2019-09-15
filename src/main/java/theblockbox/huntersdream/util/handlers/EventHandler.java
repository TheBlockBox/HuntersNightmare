package theblockbox.huntersdream.util.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.ChanceHelper;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.LootTableInit;
import theblockbox.huntersdream.api.init.StructureInit;
import theblockbox.huntersdream.api.init.WorldGenInit;
import theblockbox.huntersdream.items.ItemHunterArmor;
import theblockbox.huntersdream.util.Reference;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        VampireEventHandler.onPlayerJoin(event);

        // in its own thread so that this won't block the whole main thred
        new Thread(Reference.MODID + ":getIsOutdatedVersion") {
            @Override
            public void run() {
                try {
                    EntityPlayer player = event.player;
                    JsonObject jsonObject = new JsonParser()
                            .parse(new InputStreamReader(new URL(Reference.UPDATE_JSON).openStream()))
                            .getAsJsonObject();
                    for (JsonElement jsonElement : jsonObject.get("supportedmcversions").getAsJsonArray())
                        if (jsonElement.getAsString().equals(Reference.MC_VERSION))
                            return;
                    ITextComponent tct = new TextComponentTranslation(
                            Reference.MODID + ".versionNotSupported", Reference.MC_VERSION);
                    tct.getStyle().setColor(TextFormatting.DARK_RED);
                    player.getServer().addScheduledTask(() -> player.sendMessage(tct));
                } catch (Exception e) {
                    Main.getLogger()
                            .error("Something went wrong while trying to test for supported minecraft versions");
                }
            }
        }.start();
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        // add lycanthropy book to chests
        // for a list of all loot tables see
        // net.minecraft.world.storage.loot.LootTableList
        if (event.getName().toString().startsWith("minecraft:chests/village")) {
            event.getTable().addPool(LootTableInit.VILLAGE_CHESTS_POOL);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBiomeDecoration(DecorateBiomeEvent.Decorate event) {
        WorldGenInit.generate(event);
    }

    @SubscribeEvent
    public static void onPopulateChunkPre(PopulateChunkEvent.Pre event) {
        World world = event.getWorld();
        if (BiomeDictionary.hasType(world.getBiome(new BlockPos(event.getChunkX() * 16, 60, event.getChunkZ() * 16)),
                BiomeDictionary.Type.FOREST)) {
            Random random = event.getRand();
            ResourceLocation structure = ChanceHelper.chanceOf(random, 50) ?
                    (ChanceHelper.chanceOf(random, ConfigHandler.server.huntersCampSpawnChance) ? StructureInit.HUNTERS_CAMP : null)
                    : (ChanceHelper.chanceOf(random, ConfigHandler.server.werewolfCabinSpawnChance) ? StructureInit.WEREWOLF_CABIN : null);
            if (structure != null) {
                GeneralHelper.trySpawnStructure(structure, event.getWorld(), event.getChunkX(), event.getChunkZ(), event.getRand());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockCauldron) {
                BlockCauldron cauldron = (BlockCauldron) state.getBlock();
                int level = state.getValue(BlockCauldron.LEVEL);
                ItemStack stack = event.getItemStack();
                if ((level > 0) && (stack.getItem() instanceof ItemHunterArmor)) {
                    ItemHunterArmor armor = (ItemHunterArmor) stack.getItem();
                    if (armor.hasColor(stack)) {
                        armor.removeColor(stack);
                        cauldron.setWaterLevel(world, pos, state, level - 1);
                        event.getEntityPlayer().addStat(StatList.ARMOR_CLEANED);
                        event.setCancellationResult(EnumActionResult.SUCCESS);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
