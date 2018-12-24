package theblockbox.huntersdream.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class GuiSkillTab extends GuiScreen {
	private static final ResourceLocation TEXTURE = GeneralHelper.newResLoc("textures/gui/skills/skill_window.png");
	// tp = TransformationPlayer
	private final ITransformationPlayer tp;
	private final Transformation transformation;
	private float fade = 0F;
	public static final int TEXTURE_WIDTH = 256;
	public static final int TEXTURE_HEIGHT = 190;

	public GuiSkillTab() {
		this.tp = TransformationHelper.getITransformationPlayer(Minecraft.getMinecraft().player);
		this.transformation = this.tp.getTransformation();
		this.transformation.validateIsTransformation();
	}

	@Override
	public void initGui() {
		super.initGui();
		int buttonId = 0;
		int drawX = (this.width - TEXTURE_WIDTH) / 2 + 16;
		final int y = (this.height - TEXTURE_HEIGHT) / 2 + 25;
		final int maxY = y + TEXTURE_HEIGHT - 42;
		int drawY = y;
		for (Skill skill : Skill.getAllSkills()) {
			if (skill.isParentSkill()) {
				this.addButton(new GuiButtonSkill(skill, buttonId++, drawX, drawY));
				for (Skill childSkill : skill.getChildSkills()) {
					this.addButton(
							new GuiButtonSkill(childSkill, buttonId++, drawX + (childSkill.getLevel() * 17), drawY));
				}
				if ((drawY += 20) >= maxY) {
					drawX += 60;
					drawY = y;
				}
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.buttonList.clear();
		this.initGui();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int drawX = (this.width - TEXTURE_WIDTH) / 2;
		int drawY = (this.height - TEXTURE_HEIGHT) / 2;
		// TODO: Store hovered button to not have to draw lots of extra rectangles for
		// fading?

		// draw background
		GlStateManager.enableBlend();
		this.drawTexturedModalRect(drawX, drawY, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		// TODO: Do they need to be disabled?
		GlStateManager.disableBlend();

		GuiButtonSkill hoveredButton = null;

		for (GuiButton guiButton : this.buttonList) {
			if ((guiButton instanceof GuiButtonSkill) && guiButton.visible) {
				GuiButtonSkill button = (GuiButtonSkill) guiButton;
				// "draw button" (actually is only used for calculating things like if the
				// button is being hovered)
				button.drawButton(this.mc, mouseX, mouseY, partialTicks);

				// if the player is hovering over the button
				if (button.isMouseOver()) {
					// set the hovered button to this button (it will be drawn later)
					hoveredButton = button;
				} else {
					// draw the actual skill
					this.drawSkillButton(button);
				}
			}
		}

		// make everything darker if player hovers over button (fade gets set to 0 when
		// the player doesn't hover over something and thus the rectangle will become
		// completely transparent)
		drawRect(drawX + 8, drawY + 17, drawX + TEXTURE_WIDTH - 12, drawY + TEXTURE_HEIGHT - 8,
				MathHelper.floor(this.fade * 255.0F) << 24);

		// if no button is being hovered,
		if (hoveredButton == null) {
			// make the fade go away
			this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
		} else {
			// otherwise draw the button and its extra information

			// draw the name and the cost of the skill
			String skillTranslation = I18n.format(hoveredButton.getSkill().toString().replace(':', '.'));
			this.fontRenderer.drawString(skillTranslation, hoveredButton.x + 20, hoveredButton.y, 0);
			this.fontRenderer.drawString(
					I18n.format("huntersdream.skillCost", hoveredButton.getSkill().getNeededExperienceLevels()),
					hoveredButton.x + 20, hoveredButton.y + this.mc.fontRenderer.FONT_HEIGHT, 0);

			// draw the actual skill
			this.drawSkillButton(hoveredButton);
			// change the fade so that everything becomes darker (except for the button)
			this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
		}
	}

	public void drawSkillButton(GuiButtonSkill button) {
		// if the player already has the skill, color it
		if (this.tp.hasSkill(button.getSkill())) {
			GlStateManager.color(208, 136, 0);
		} else {
			// otherwise set the color to 0 (the color actually gets reset)
			GlStateManager.color(255, 255, 255);
		}
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		this.drawTexturedModalRect(button.x, button.y, button.getSkill().getIconAsSprite(), 16, 16);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
	}
}
