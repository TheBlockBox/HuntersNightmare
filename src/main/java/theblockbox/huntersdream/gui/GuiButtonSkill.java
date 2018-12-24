package theblockbox.huntersdream.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

public class GuiButtonSkill extends GuiButton {
	private final Skill skill;
	private final ITransformationPlayer tp;

	public GuiButtonSkill(Skill skill, int buttonId, int x, int y) {
		super(buttonId, x, y, 16, 16, "");
		this.skill = skill;
		this.tp = TransformationHelper.getITransformationPlayer(Minecraft.getMinecraft().player);
	}

	public Skill getSkill() {
		return this.skill;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
				&& mouseY < this.y + this.height;
		// TODO: Uncomment?
		// super.drawButton(mc, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return super.mousePressed(mc, mouseX, mouseY) && !this.tp.hasSkill(this.skill);
	}
}
