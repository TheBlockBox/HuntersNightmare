package theblockbox.huntersdream.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import theblockbox.huntersdream.Main;
import theblockbox.huntersdream.api.helpers.GeneralHelper;
import theblockbox.huntersdream.api.helpers.TransformationHelper;
import theblockbox.huntersdream.api.interfaces.transformation.ITransformationPlayer;
import theblockbox.huntersdream.api.skill.ParentSkill;
import theblockbox.huntersdream.api.skill.Skill;
import theblockbox.huntersdream.util.handlers.PacketHandler;

import java.io.IOException;

public class GuiSkillTab extends GuiScreen {
    public static final GuiSkillTab INSTANCE = new GuiSkillTab();
    public static final int TEXTURE_WIDTH = 256;
    public static final int TEXTURE_HEIGHT = 190;
    public static final int TOOLTIP_COLOR = 0;
    public static final int MAX_TEXT_WIDTH = 108;
    private static final ResourceLocation TEXTURE = GeneralHelper.newResLoc("textures/gui/skills/skill_window.png");
    private final GuiButton unlockButton1 = new GuiButton(0, 0, 0, "") {
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        }
    };
    private final GuiButton unlockButton2 = new GuiButton(0, 0, 0, "") {
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        }
    };
    // tp = TransformationPlayer
    private ITransformationPlayer tp;
    private int page;
    private int drawX;
    private int drawY;
    private ParentSkill leftPage;
    private ParentSkill rightPage;

    public GuiSkillTab() {
        this.refresh();
    }

    public GuiSkillTab refresh() {
        this.tp = TransformationHelper.getITransformationPlayer(Minecraft.getMinecraft().player);
        return this;
    }

    @Override
    public void initGui() {
        super.initGui();
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (!this.tp.getTransformation().hasAccessToSkillTab(player)) {
            Main.getLogger().warn("Player " + player + " tried to access the skill tab although they weren't allowed to");
            return;
        }
        this.drawX = (this.width - GuiSkillTab.TEXTURE_WIDTH) / 2;
        this.drawY = (this.height - GuiSkillTab.TEXTURE_HEIGHT) / 2;
        Skill.getAllSkills().stream().filter(s -> s.isParentSkill() && s.canBeBoughtWithExperience())
                .map(s -> s.getAsParentSkill().get()).filter(s -> s.isForTransformation(this.tp.getTransformation()))
                .forEach(s -> this.buttonList.add(new GuiSkillTab.GuiButtonBookmark(s, this.buttonList.size())));
        int id = this.buttonList.size();
        this.unlockButton1.id = id;
        this.unlockButton2.id = id + 1;
        this.unlockButton1.enabled = false;
        this.unlockButton2.enabled = false;
        this.buttonList.add(this.unlockButton1);
        this.buttonList.add(this.unlockButton2);
        this.setPage(this.page);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.buttonList.clear();
        this.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawX = (this.width - GuiSkillTab.TEXTURE_WIDTH) / 2;
        this.drawY = (this.height - GuiSkillTab.TEXTURE_HEIGHT) / 2;

        // draw background
        this.mc.getTextureManager().bindTexture(GuiSkillTab.TEXTURE);
        this.drawTexturedModalRect(this.drawX, this.drawY, 0, 0, GuiSkillTab.TEXTURE_WIDTH, GuiSkillTab.TEXTURE_HEIGHT);

        // draw foreground
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawSkill(this.leftPage, this.drawX + 12, this.drawY + 22, true);
        this.drawSkill(this.rightPage, this.drawX + 5 + (GuiSkillTab.TEXTURE_WIDTH / 2), this.drawY + 22, false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        int id = button.id;
        int buttons = this.buttonList.size();
        if (id == (buttons - 2)) {
            // left unlock button clicked
            this.tryUnlockSkill(this.leftPage);
        } else if (id == (buttons - 1)) {
            // right unlock button clicked
            this.tryUnlockSkill(this.rightPage);
        } else {
            this.setPage(id);
        }
    }

    public void drawSkill(ParentSkill parentSkill, int x, int y, boolean left) {
        if (parentSkill != null) {
            Skill skill = parentSkill.getSkillWithLevel(Math.min(this.tp.getSkillLevel(parentSkill) + 1, parentSkill.getMaximumLevel()));

            // draw name
            String name = I18n.format(skill.getTranslationKeyName());
            this.fontRenderer.drawSplitString(name, x + ((GuiSkillTab.MAX_TEXT_WIDTH - this.fontRenderer.getStringWidth(name)) / 2), y,
                    GuiSkillTab.MAX_TEXT_WIDTH, GuiSkillTab.TOOLTIP_COLOR);
            y += Math.max(this.fontRenderer.getWordWrappedHeight(name, GuiSkillTab.MAX_TEXT_WIDTH), this.fontRenderer.FONT_HEIGHT * 2);

            // draw description
            String description = I18n.format(skill.getTranslationKeyDescription());
            this.fontRenderer.drawSplitString(description, x, y, GuiSkillTab.MAX_TEXT_WIDTH, GuiSkillTab.TOOLTIP_COLOR);

            // add unlock text if the player doesn't have the skill
            if (!this.tp.hasSkill(skill)) {
                y += Math.max(this.fontRenderer.getWordWrappedHeight(name, GuiSkillTab.MAX_TEXT_WIDTH), this.fontRenderer.FONT_HEIGHT * 10);
                // draw text for unlocking the skill
                String unlockSkill = I18n.format("huntersdream.unlockSkill", skill.getNeededExperienceLevels());
                this.fontRenderer.drawSplitString(unlockSkill, x, y, GuiSkillTab.MAX_TEXT_WIDTH, skill.canPlayerUnlockSkill(this.mc.player) ? 47360 : 11141120);
                // add buttons here
                GuiButton unlockButton = left ? this.unlockButton1 : this.unlockButton2;
                unlockButton.enabled = true;
                unlockButton.x = x;
                unlockButton.y = y;
                unlockButton.width = Math.min(GuiSkillTab.MAX_TEXT_WIDTH, this.fontRenderer.getStringWidth(unlockSkill));
                unlockButton.height = this.fontRenderer.getWordWrappedHeight(unlockSkill, GuiSkillTab.MAX_TEXT_WIDTH);
            } else {
                (left ? this.unlockButton1 : this.unlockButton2).enabled = false;
            }
        }
    }

    public void setPage(int page) {
        this.page = Math.min((page / 2) * 2, page);
        this.leftPage = (this.page < (this.buttonList.size() - 2)) ? ((GuiSkillTab.GuiButtonBookmark) this.buttonList.get(this.page))
                .getSkill() : null;
        this.rightPage = (this.page < (this.buttonList.size() - 3)) ? ((GuiSkillTab.GuiButtonBookmark) this.buttonList.get(this.page + 1))
                .getSkill() : null;
    }

    public int getXFromButtonId(int buttonId) {
        return this.drawX + ((buttonId % 2 == 0) ? -20 : GuiSkillTab.TEXTURE_WIDTH - 2);
    }

    public int getYFromButtonId(int buttonId) {
        return ((buttonId / 2) * 20) + this.drawY + 17;
    }

    private boolean tryUnlockSkill(ParentSkill parentSkill) {
        Skill skill = parentSkill.getSkillWithLevel(Math.min(this.tp.getSkillLevel(parentSkill) + 1, parentSkill.getMaximumLevel()));
        if (skill.canPlayerUnlockSkill(this.mc.player)) {
            this.mc.displayGuiScreen(new GuiSkillBuyConfirmation(skill));
            return true;
        } else {
            return false;
        }
    }

    public class GuiButtonBookmark extends GuiButtonClickable {
        private final ParentSkill skill;

        public GuiButtonBookmark(ParentSkill skill, int buttonId) {
            super(buttonId, GuiSkillTab.this.getXFromButtonId(buttonId), GuiSkillTab.this.getYFromButtonId(buttonId), skill.getIconAsSprite());
            this.skill = skill;
        }

        @Override
        public void onClicked(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            GuiSkillTab.this.setPage(this.id);
        }

        public ParentSkill getSkill() {
            return this.skill;
        }
    }

    private class GuiSkillBuyConfirmation extends GuiYesNo {
        private final Skill skill;

        private GuiSkillBuyConfirmation(Skill skill) {
            super(GuiSkillTab.this, I18n.format("huntersdream.permissionUnlockSkill",
                    I18n.format(skill.getTranslationKeyName()), skill.getNeededExperienceLevels()), "", -1);
            this.skill = skill;
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if (button.id == 0) {
                PacketHandler.sendSkillUnlockMessage(this.mc.world, this.skill);
            }
            this.mc.displayGuiScreen(null);
        }
    }
}
