package theblockbox.huntersdream.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.exceptions.WrongSideException;
import theblockbox.huntersdream.util.handlers.RegistryHandler;

public class ServerProxy implements IProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        throw new WrongSideException("Can't register item renderer on server", Side.SERVER);
    }

    @Override
    public void preInit() {
        RegistryHandler.preInitServer();
    }

    @Override
    public void init() {
        RegistryHandler.initServer();
    }

    @Override
    public void postInit() {
        RegistryHandler.postInitServer();
    }

    @Override
    public void onRegisterModels(ModelRegistryEvent event) {
    }

    @Override
    public boolean isPhysicalClient() {
        return false;
    }

    @Override
    public EntityPlayer getPlayer() {
        return null;
    }

    @Override
    public <T extends Entity> T getEntityFromID(int id) {
        throw new UnsupportedOperationException("Can't get entity from server");
    }

    @Override
    public void openSkillTab(EntityPlayer player) {
    }
}
