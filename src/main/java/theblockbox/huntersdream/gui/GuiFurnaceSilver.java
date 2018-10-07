package theblockbox.huntersdream.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.container.ContainerFurnaceSilver;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class GuiFurnaceSilver extends GuiContainer {
	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;
	public static final ResourceLocation IMAGE = GeneralHelper.newResLoc("textures/gui/furnace_silver.png");
	private final ContainerFurnaceSilver container;

	public GuiFurnaceSilver(ContainerFurnaceSilver inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
		this.container = inventorySlotsIn;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(IMAGE);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GeneralHelper.drawCenteredString(this.fontRenderer, I18n.format("tile.huntersdream.furnace_silver.name"), 0,
				this.xSize, 5, 4210752);
		this.fontRenderer.drawString(this.container.getPlayerInventory().getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 4, 4210752);
	}
}
