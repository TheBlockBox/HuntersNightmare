package theblockbox.huntersdream.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.api.Transformation;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;

public class GuiSkillTab extends GuiScreen {
	private static final ResourceLocation TEXTURE = GeneralHelper.newResLoc("textures/gui/skills/skill_window.png");
	private final Transformation transformation;

	public GuiSkillTab() {
		this.transformation = TransformationHelper.getTransformation(Minecraft.getMinecraft().player);
	}
}
