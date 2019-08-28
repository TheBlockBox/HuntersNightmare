package theblockbox.huntersdream.gui;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.container.ContainerSpinningWheel;

public class GuiSpinningWheel extends GuiContainer {
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    public static final ResourceLocation IMAGE = GeneralHelper.newResLoc("textures/gui/spinning_wheel.png");
    private final ContainerSpinningWheel container;
    private final String translationKey;

    public GuiSpinningWheel(ContainerSpinningWheel inventorySlotsIn, Block block) {
        super(inventorySlotsIn);
        this.translationKey = block.getTranslationKey() + ".name";
        this.xSize = GuiSpinningWheel.WIDTH;
        this.ySize = GuiSpinningWheel.HEIGHT;
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
        this.mc.getTextureManager().bindTexture(GuiSpinningWheel.IMAGE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(this.guiLeft + 79, this.guiTop + 34, 176, 14, 5 + (int) ((this.container.getTicks()
                - this.container.getWorkingSince()) * 15.0F / this.container.tileEntity.getNeededProcessingTime()), 17);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(GuiSpinningWheel.IMAGE);
        GeneralHelper.drawCenteredString(this.fontRenderer,
                I18n.format(this.translationKey), 0, this.xSize, 5, 4210752);
        this.fontRenderer.drawString(this.container.getPlayerInventory().getDisplayName().getUnformattedText(), 8,
                this.ySize - 96 + 4, 4210752);
    }
}