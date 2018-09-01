package theblockbox.huntersdream.util.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import theblockbox.huntersdream.util.enums.Containers;

public class GuiHandler implements IGuiHandler {
	public static final GuiHandler INSTANCE = new GuiHandler();

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return Containers.values()[id].getContainer(player);
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return Containers.values()[id].getGui(player);
	}
}
