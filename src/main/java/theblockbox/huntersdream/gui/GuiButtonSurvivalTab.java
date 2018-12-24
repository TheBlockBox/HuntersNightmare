package theblockbox.huntersdream.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

public class GuiButtonSurvivalTab extends GuiButton {
	private boolean hasMouseBeenPressed = false;
	private final GuiScreen guiToOpen;
	private final TextureAtlasSprite sprite;

	public GuiButtonSurvivalTab(int buttonId, int x, int y, GuiScreen guiToOpen, TextureAtlasSprite sprite) {
		super(buttonId, x, y, 18, 18, "");
		this.guiToOpen = guiToOpen;
		this.sprite = sprite;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			super.drawButton(mc, mouseX, mouseY, partialTicks);
			if (this.mousePressed(mc, mouseX, mouseY) && this.hasMouseBeenPressed) {
				Minecraft.getMinecraft().displayGuiScreen(this.guiToOpen);
				this.hasMouseBeenPressed = false;
			}
			mc.getTextureMapBlocks();
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			this.drawTexturedModalRect(this.x + 1, this.y + 1, this.sprite, 16, 16);
		}
	}

	// TODO: Is there a better way to do this?
	@Override
	public void playPressSound(SoundHandler soundHandlerIn) {
		super.playPressSound(soundHandlerIn);
		this.hasMouseBeenPressed = true;
	}

	public boolean hasMouseBeenPressed() {
		return this.hasMouseBeenPressed;
	}
}