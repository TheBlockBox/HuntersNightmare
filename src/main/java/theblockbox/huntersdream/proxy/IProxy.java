package theblockbox.huntersdream.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy {
    public void registerItemRenderer(Item item, int meta, String id);

    public void preInit();

    public void init();

    public void postInit();

    public void onRegisterModels(ModelRegistryEvent event);

    public boolean isPhysicalClient();

    public default Side getPhysicalSide() {
        return this.isPhysicalClient() ? Side.CLIENT : Side.SERVER;
    }

    public EntityPlayer getPlayer();

    public <T extends Entity> T getEntityFromID(int id);

    public void openSkillTab(EntityPlayer player);
}
