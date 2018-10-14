package theblockbox.huntersdream.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import theblockbox.huntersdream.container.ContainerSilverFurnace;
import theblockbox.huntersdream.init.BlockInit;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class GuiCampfire extends GuiContainer {
	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;
	public static final ResourceLocation IMAGE = GeneralHelper.newResLoc("textures/gui/campfire.png");
	private final ContainerSilverFurnace container;

	public GuiCampfire(ContainerSilverFurnace inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = WIDTH;
		this.ySize = HEIGHT;
		this.container = inventorySlotsIn;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(IMAGE);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(IMAGE);
		// draw fuel
		int burnTime = container.getBurnTime();
		if (burnTime > 0) {
			int fullBurnTime = container.getFullBurnTime();
			int fuel = MathHelper.ceil(12 / (fullBurnTime / (double) (fullBurnTime - burnTime)));
			this.drawTexturedModalRect(69, 38 + fuel, 177, fuel, 15, 14 - fuel);
		}

		GeneralHelper.drawCenteredString(this.fontRenderer,
				I18n.format(BlockInit.CAMPFIRE.getTranslationKey() + ".name"), 0, this.xSize, 5, 4210752);
		this.fontRenderer.drawString(this.container.getPlayerInventory().getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 4, 4210752);
	}
}