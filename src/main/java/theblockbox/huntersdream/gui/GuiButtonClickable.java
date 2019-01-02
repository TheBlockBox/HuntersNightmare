package theblockbox.huntersdream.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

public abstract class GuiButtonClickable extends GuiButton {
    private boolean hasMouseBeenPressed = false;
    private final TextureAtlasSprite sprite;

    public GuiButtonClickable(int buttonId, int x, int y, TextureAtlasSprite sprite) {
        super(buttonId, x, y, 18, 18, "");
        this.sprite = sprite;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            super.drawButton(mc, mouseX, mouseY, partialTicks);
            if (this.mousePressed(mc, mouseX, mouseY) && this.hasMouseBeenPressed) {
                this.onClicked(mc, mouseX, mouseY, partialTicks);
                this.hasMouseBeenPressed = false;
            }
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            this.drawTexturedModalRect(this.x + 1, this.y + 1, this.sprite, 16, 16);
        }
    }

    public abstract void onClicked(Minecraft mc, int mouseX, int mouseY, float partialTicks);

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
