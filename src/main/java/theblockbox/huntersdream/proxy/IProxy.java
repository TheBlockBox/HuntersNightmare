package theblockbox.huntersdream.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.util.HuntersJournalPage;

public interface IProxy {
    public void registerItemRenderer(Item item, int meta, String id);

    public void preInit();

    public void init();

    public void postInit();

    public boolean isPhysicalClient();

    public default Side getPhysicalSide() {
        return this.isPhysicalClient() ? Side.CLIENT : Side.SERVER;
    }

    public EntityPlayer getPlayer();

    public void openHuntersJournal(EntityPlayer player, HuntersJournalPage[] pages);

    public <T extends Entity> T getEntityFromID(int id);
}
