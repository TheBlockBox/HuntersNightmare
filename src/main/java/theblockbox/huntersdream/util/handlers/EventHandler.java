package theblockbox.huntersdream.util.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.ChanceHelper;
import theblockbox.huntersdream.api.init.BlockInit;
import theblockbox.huntersdream.api.init.LootTableInit;
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
                    TextComponentTranslation tct = new TextComponentTranslation(
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
        // add hunter's notes to chests
        // for a list of all loot tables see
        // net.minecraft.world.storage.loot.LootTableList
        if (event.getName().toString().startsWith("minecraft:chests/")) {
            event.getTable().addPool(LootTableInit.HUNTERS_JOURNAL_PAGE_POOL);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBiomeDecoration(DecorateBiomeEvent.Decorate event) {
        Event.Result result = event.getResult();
        Random random = event.getRand();
        // TODO: Better percentage
        if ((result == Event.Result.ALLOW || result == Event.Result.DEFAULT)
                && event.getType() == DecorateBiomeEvent.Decorate.EventType.FLOWERS
                && ChanceHelper.chanceOf(random, 1.0D)) {
            World world = event.getWorld();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(event.getChunkPos().getBlock(15, 0, 15));
            int xOffset = pos.getX();
            int zOffset = pos.getZ();
            int currentFlowers = 0;
            int maxFlowers = random.nextInt(5);

            for (int y = 256; y > 0; y--) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (BlockInit.ACONITE_FLOWER.canPlaceBlockAt(world, pos.setPos(x + xOffset, y, z + zOffset))) {
                            world.setBlockState(pos, BlockInit.ACONITE_FLOWER.getDefaultState(), 2);
                            if (++currentFlowers > maxFlowers)
                                return;
                            x = Math.min(15, x + random.nextInt(2));
                            z = Math.min(15, z + random.nextInt(2));
                        }
                    }
                }
            }
        }
    }
}