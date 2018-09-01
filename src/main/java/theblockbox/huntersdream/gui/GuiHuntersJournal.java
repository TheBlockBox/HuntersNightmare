package theblockbox.huntersdream.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import theblockbox.huntersdream.util.helpers.GeneralHelper;

public class GuiHuntersJournal extends GuiScreen {
	public final EntityPlayer PLAYER;
	private int currentPage = 0;
	private HuntersJournalPage[] pages = {
			new HuntersJournalPage("Test page", null, "Please tell us when you see this screen") };
	private GuiButton nextPage;
	private GuiButton pageBefore;

	public static final ResourceLocation TEXTURE = GeneralHelper.newResLoc("textures/gui/hunters_journal.png");

	public GuiHuntersJournal(EntityPlayer player) {
		this.PLAYER = player;
	}

	@Override
	public void initGui() {
		buttonList.clear();
		Keyboard.enableRepeatEvents(true);
		this.nextPage = new NextButton(0, true);
		this.pageBefore = new NextButton(1, false);
		this.buttonList.add(nextPage);
		this.buttonList.add(pageBefore);
	}

	@Override
	public void updateScreen() {
		this.nextPage.visible = currentPage < pages.length - 1;
		this.pageBefore.visible = currentPage > 0;
	}

	public void drawInBounds(int x, int y, int width, int height) {
		// title
		this.drawCenteredString(this.fontRenderer, this.pages[currentPage].getTitle(), x + (width / 2), y,
				TextFormatting.BLUE.getColorIndex());

		y += (this.pages[currentPage].getImagePath() != null ? 75 : 10);

		// description
		this.fontRenderer.drawSplitString(this.pages[currentPage].getDescription(), x, y, width,
				TextFormatting.BLUE.getColorIndex());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(TEXTURE);
		int width = 146;
		int height = 180;
		int middle = (this.width / 2);
		this.drawTexturedModalRect(middle - (width / 2), 3, 20, 0, width, height);
		this.pageBefore.x = (int) (middle - (width / 2.5D));
		this.nextPage.x = (int) (middle + (width / 2.5D)) - this.nextPage.width;
		super.drawScreen(mouseX, mouseY, partialTicks);
		int x = middle - (width / 2) + 15;
		int w = width - 28;
		if (pages[currentPage].getImagePath() != null) {
			mc.getTextureManager().bindTexture(pages[currentPage].getImagePath());
			this.drawTexturedModalRect(x - 1, 24, 0, 0, w, 64);
		}
		this.drawInBounds(x, 15, w, height - 20);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof NextButton) {
			currentPage += ((NextButton) button).isNextPageButton ? 1 : -1;
			if (currentPage < 0) {
				currentPage = 0;
			}
			if (currentPage > pages.length - 1) {
				currentPage = pages.length - 1;
			}
		}
	}

	public class NextButton extends GuiButton {
		private boolean isNextPageButton;

		public NextButton(int buttonId, boolean isNextPageButton) {
			super(buttonId, 0, 163, 18, 10, "");
			this.visible = false;
			this.isNextPageButton = isNextPageButton;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				boolean isButtonPressed = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
						&& mouseY < this.y + this.height);
				// 3, 194
				// when button is pressed, use red texture, otherwise use white one
				int textureX = isButtonPressed ? 26 : 3;
				int textureY = this.isNextPageButton ? 194 : 207;
				mc.getTextureManager().bindTexture(TEXTURE);
				// draw button
				this.drawTexturedModalRect(this.x, this.y, textureX, textureY, 18, 10);
			}
		}
	}
}
