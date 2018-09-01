package theblockbox.huntersdream.util.enums;

import java.util.function.Function;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theblockbox.huntersdream.Main;

public enum Containers {
	;

	private int id;
	private Function<EntityPlayer, ? extends GuiScreen> getGui;
	private Function<EntityPlayer, ? extends GuiContainer> getContainer;

	private Containers(Function<EntityPlayer, ? extends GuiScreen> guiFactory,
			Function<EntityPlayer, ? extends GuiContainer> containerFactory) {
		this.id = values().length;
		this.getGui = guiFactory;
		this.getContainer = containerFactory;
	}

	public int getID() {
		return id;
	}

	public void open(EntityPlayer player) {
		open(player, player.world, player.getPosition());
	}

	public void open(EntityPlayer player, World world, BlockPos pos) {
		player.openGui(Main.instance, getID(), world, pos.getX(), pos.getY(), pos.getZ());
	}

	public GuiScreen getGui(EntityPlayer player) {
		return getGui.apply(player);
	}

	public GuiContainer getContainer(EntityPlayer player) {
		return getContainer.apply(player);
	}
}
