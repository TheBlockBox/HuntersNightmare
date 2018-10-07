package theblockbox.huntersdream.util.handlers;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import theblockbox.huntersdream.blocks.tileentity.TileEntitySilverFurnace;
import theblockbox.huntersdream.container.ContainerFurnaceSilver;
import theblockbox.huntersdream.gui.GuiFurnaceSilver;

public class GuiHandler implements IGuiHandler {
	public static final GuiHandler INSTANCE = new GuiHandler();

	@Override
	public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (id) {
		case 0:
			return new ContainerFurnaceSilver(player.inventory,
					(TileEntitySilverFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

	@Override
	public Gui getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		switch (id) {
		case 0:
			return new GuiFurnaceSilver((ContainerFurnaceSilver) getServerGuiElement(id, player, world, x, y, z));
		default:
			return null;
		}
	}
}
