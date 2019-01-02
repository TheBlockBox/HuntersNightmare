package theblockbox.huntersdream.gui;

import com.google.common.primitives.Ints;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.util.helpers.ClientHelper;

import javax.annotation.Nullable;

public class GuiButtonSkill extends GuiButton {
    private final Skill skill;
    private final GuiButtonSkill childButton;
    private final int middleX;
    private final int middleY;
    // I'm not going to create a getter for every
    // of these, therefore they're package-private
    final String name;
    final String cost;
    final String description;
    final int nameWidth;
    final int nameHeight;
    final int costMaxWidth;
    final int costHeight;
    final int descriptionMaxWidth;
    final int descriptionHeight;
    final int highestWidth;

    public GuiButtonSkill(Skill skill, int buttonId, int x, int y, @Nullable GuiButtonSkill childButton,
                          FontRenderer fontRenderer, int textWidth) {
        super(buttonId, x, y, 16, 16, "");
        if(fontRenderer == null)
            fontRenderer = Minecraft.getMinecraft().fontRenderer;
        this.skill = skill;
        this.childButton = childButton;
        this.middleX = this.x + this.width / 2;
        this.middleY = this.y + this.height / 2;

        this.name = I18n.format(skill.getTranslationKeyName());
        this.cost = I18n.format("huntersdream.skillCost", skill.getNeededExperienceLevels());
        this.description = I18n.format(skill.getTranslationKeyDescription());
        this.nameWidth = fontRenderer.getStringWidth(this.name);
        this.nameHeight = fontRenderer.FONT_HEIGHT;
        this.costMaxWidth = ClientHelper.getMaxFontWidth(this.cost, textWidth, fontRenderer);
        this.costHeight = fontRenderer.getWordWrappedHeight(this.cost, textWidth);
        this.descriptionMaxWidth = ClientHelper.getMaxFontWidth(this.description, textWidth, fontRenderer);
        this.descriptionHeight = fontRenderer.getWordWrappedHeight(this.description, textWidth);
        this.highestWidth = Ints.max(this.nameWidth, this.costMaxWidth, this.descriptionMaxWidth);
    }

    public Skill getSkill() {
        return this.skill;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
                && mouseY < this.y + this.height;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return super.mousePressed(mc, mouseX, mouseY) && this.skill.canPlayerUnlockSkill(mc.player);
    }

    @Nullable
    public GuiButtonSkill getChildButton() {
        return this.childButton;
    }

    public int getMiddleX() {
        return this.middleX;
    }

    public int getMiddleY() {
        return this.middleY;
    }
}
