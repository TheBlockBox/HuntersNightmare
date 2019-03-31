package theblockbox.huntersdream.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import theblockbox.huntersdream.gui.GuiHuntersJournal;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.handlers.RegistryHandler;

public class ClientProxy implements IProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public void preInit() {
        RegistryHandler.preInitClient();
    }

    @Override
    public void init() {
        RegistryHandler.initClient();
    }

    @Override
    public void postInit() {
        RegistryHandler.postInitClient();
    }

    @Override
    public boolean isPhysicalClient() {
        return true;
    }

    @Override
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public void openHuntersJournal(EntityPlayer player, HuntersJournalPage[] pages) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiHuntersJournal(player, pages));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Entity> T getEntityFromID(int id) {
        World world = Minecraft.getMinecraft().world;
        Entity entity = world.getEntityByID(id);
        if (entity == null) {
            throw new IllegalArgumentException("No entity with the id " + id + " exists in the world "
                    + world.getProviderName() + " (Side: CLIENT)");
        }
        return (T) entity;
    }
}
