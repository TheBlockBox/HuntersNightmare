package theblockbox.huntersdream.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.init.BlockInit;
import theblockbox.huntersdream.container.ContainerSilverFurnace;

public class GuiSilverFurnace extends GuiContainer {
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    public static final ResourceLocation IMAGE = GeneralHelper.newResLoc("textures/gui/silver_furnace.png");
    private final ContainerSilverFurnace container;

    public GuiSilverFurnace(ContainerSilverFurnace inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = GuiSilverFurnace.WIDTH;
        this.ySize = GuiSilverFurnace.HEIGHT;
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
        this.mc.getTextureManager().bindTexture(GuiSilverFurnace.IMAGE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(GuiSilverFurnace.IMAGE);
        // draw fuel
        int burnTime = this.container.getBurnTime();
        if (burnTime > 0) {
            int fullBurnTime = this.container.getFullBurnTime();
            int fuel = MathHelper.ceil(12 / (fullBurnTime / (double) (fullBurnTime - burnTime)));
            this.drawTexturedModalRect(38, 36 + fuel, 176, fuel, 15, 14 - fuel);
        }

        // draw progress bar
        if (this.container.hasRecipe() && (burnTime > 0)) {
            double fullSmeltingTime = this.container.getFullNeededSmeltingTime();
            int progress = 24 - (int) (24
                    * ((this.container.getSmeltingRecipeSince() + fullSmeltingTime - this.container.getTicks())
                    / fullSmeltingTime));
            this.drawTexturedModalRect(80, 35, 177, 15, progress, 17);
        }

        GeneralHelper.drawCenteredString(this.fontRenderer,
                I18n.format(BlockInit.SILVER_FURNACE.getTranslationKey() + ".name"), 0, this.xSize, 5, 4210752);
        this.fontRenderer.drawString(this.container.getPlayerInventory().getDisplayName().getUnformattedText(), 8,
                this.ySize - 96 + 4, 4210752);
    }
}
