package theblockbox.huntersdream.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class GuiButtonSurvivalTab extends GuiButtonClickable {
	private final GuiScreen guiToOpen;

	public GuiButtonSurvivalTab(int buttonId, int x, int y, GuiScreen guiToOpen, TextureAtlasSprite sprite) {
		super(buttonId, x, y, sprite);
		this.guiToOpen = guiToOpen;
	}

	@Override
	public void onClicked(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		Minecraft.getMinecraft().displayGuiScreen(this.guiToOpen);
	}
}