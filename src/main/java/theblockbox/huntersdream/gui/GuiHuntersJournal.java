package theblockbox.huntersdream.gui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.util.HuntersJournalPage;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class GuiHuntersJournal extends GuiScreen {
	private final HuntersJournalPage[] pages;
	public final EntityPlayer player;
	private int currentPage = 0;
	private GuiButton nextPage;
	private GuiButton pageBefore;

	public static final ResourceLocation TEXTURE = GeneralHelper.newResLoc("textures/gui/hunters_journal.png");

	public GuiHuntersJournal(EntityPlayer player, HuntersJournalPage[] pages) {
		this.player = player;
		this.pages = pages;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.nextPage = new NextButton(0, true);
		this.pageBefore = new NextButton(1, false);
		this.buttonList.add(this.nextPage);
		this.buttonList.add(this.pageBefore);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.nextPage.visible = this.currentPage < this.pages.length - 1;
		this.pageBefore.visible = this.currentPage > 0;
	}

	public void drawInBounds(int x, int y, int drawWidth, HuntersJournalPage page) {
		// title
		this.drawCenteredString(this.fontRenderer, page.getTitle(), x + (drawWidth / 2), y, Color.BLUE.getRGB());

		y += (page.hasImage() ? 75 : 10);

		// description
		this.fontRenderer.drawSplitString(page.getDescription(), x, y, drawWidth, Color.BLACK.getRGB());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		HuntersJournalPage page = this.pages[this.currentPage];
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int middle = (this.width / 2);
		this.drawTexturedModalRect(middle - (this.width / 2), 3, 20, 0, this.width, this.height);
		this.pageBefore.x = (int) (middle - (this.width / 2.5D));
		this.nextPage.x = (int) (middle + (this.width / 2.5D)) - this.nextPage.width;
		super.drawScreen(mouseX, mouseY, partialTicks);
		int x = middle - (this.width / 2) + 15;
		int w = this.width - 28;
		if (page.hasImage()) {
			this.mc.getTextureManager().bindTexture(page.getImagePath());
			this.drawTexturedModalRect(x - 1, 24, 0, 0, w, 64);
		}
		// height = height - 20
		this.drawInBounds(x, 15, w, page);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof NextButton) {
			this.currentPage += ((NextButton) button).isNextPageButton ? 1 : -1;
			if (this.currentPage < 0) {
				this.currentPage = 0;
			}
			if (this.currentPage > this.pages.length - 1) {
				this.currentPage = this.pages.length - 1;
			}
		}
	}

	public static class NextButton extends GuiButton {
		private final boolean isNextPageButton;

		public NextButton(int buttonId, boolean isNextPageButton) {
			super(buttonId, 0, 163, 18, 10, "");
			this.visible = false;
			this.isNextPageButton = isNextPageButton;
		}

		@Override
		public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				boolean isButtonPressed = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
						&& mouseY < this.y + this.height);
				// 3, 194
				// when button is pressed, use red texture, otherwise use white one
				int textureX = isButtonPressed ? 26 : 3;
				int textureY = this.isNextPageButton ? 194 : 207;
				minecraft.getTextureManager().bindTexture(TEXTURE);
				// draw button
				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 18, 10);
			}
		}
	}
}
