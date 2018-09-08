package theblockbox.huntersdream.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy {
	public void registerItemRenderer(Item item, int meta, String id);

	public void preInit();

	public void init();

	public void postInit();

	public boolean physicalClient();

	default public Side getPhysicalSide() {
		return this.physicalClient() ? Side.CLIENT : Side.SERVER;
	}

	public EntityPlayer getPlayer();
}
