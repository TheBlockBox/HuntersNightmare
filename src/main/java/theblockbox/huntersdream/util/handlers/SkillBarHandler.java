package theblockbox.huntersdream.util.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import theblockbox.huntersdream.api.Skill;
import theblockbox.huntersdream.util.Reference;
import theblockbox.huntersdream.util.helpers.ClientHelper;
import theblockbox.huntersdream.util.helpers.GeneralHelper;
import theblockbox.huntersdream.util.helpers.TransformationHelper;
import theblockbox.huntersdream.util.interfaces.transformation.ITransformationPlayer;

import java.util.Optional;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.*;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Reference.MODID)
public class SkillBarHandler {
    public static final ResourceLocation WIDGETS = GeneralHelper.newResLoc("minecraft:textures/gui/widgets.png");
    /**
     * How much pixels the hotbar should be pushed up
     */
    public static final int HOTBAR_PUSH_HEIGHT = 30;
    /**
     * The z that is used to draw the skill bar
     */
    public static final float SKILL_BAR_Z_LEVEL = -90.0F;
    private static final Skill[] SKILL_BAR = new Skill[9];
    private static int currentSkill = 0;
    private static float skillBarShowStage = 0.0F;
    private static boolean isBarGoingUp = false;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindingInit.ACTIVATE_SKILL_BAR.isPressed()) {
            SkillBarHandler.showSkillBar();
        }
        if (SkillBarHandler.isSkillBarShown()) {
            Minecraft mc = Minecraft.getMinecraft();
            for (int i = 0; i < 9; ++i) {
                if (mc.gameSettings.keyBindsHotbar[i].isPressed()) {
                    SkillBarHandler.currentSkill = i;
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouse(MouseEvent event) {
        if (SkillBarHandler.isSkillBarShown()) {
            int dWheel = event.getDwheel();
            int newSkill = SkillBarHandler.currentSkill - MathHelper.clamp(dWheel, -1, 1);
            if (newSkill > 8) {
                newSkill = 0;
            } else if (newSkill < 0) {
                newSkill = 8;
            }

            if (newSkill != SkillBarHandler.currentSkill) {
                SkillBarHandler.currentSkill = newSkill;
                event.setCanceled(true);
            }

            if (event.getButton() == 1) {
                Skill skill = SkillBarHandler.SKILL_BAR[SkillBarHandler.currentSkill];
                SkillBarHandler.hideSkillBar();
                PacketHandler.sendActivateSkillMessage(Minecraft.getMinecraft().world, skill);
            }
        }
    }

    // we'll just hope nobody is crazy enough to cancel
    // RenderGameOverlayEvent.Pre on the lowest priority
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGameOverlayRenderPreLowest(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (SkillBarHandler.shouldDrawSkillBar(event) && (mc.getRenderViewEntity() instanceof EntityPlayer)) {
            // push matrix and translate
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, SkillBarHandler.skillBarShowStage, 0);

            // draw only when the type is hotbar to prevent texture issues and drawing although it's already been drawn
            if (event.getType() == HOTBAR) {
                // draw skill bar
                int scaledHeight = event.getResolution().getScaledHeight() + SkillBarHandler.HOTBAR_PUSH_HEIGHT - 4;
                GuiIngame guiIng = mc.ingameGUI;

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(SkillBarHandler.WIDGETS);
                int halfScaledWidth = event.getResolution().getScaledWidth() / 2;

                // draw skill bar and highlight chosen skill
                ClientHelper.drawTexturedModalRect(halfScaledWidth - 91, scaledHeight - 22, 0, 0, 182, 22, SkillBarHandler.SKILL_BAR_Z_LEVEL);
                ClientHelper.drawTexturedModalRect(halfScaledWidth - 92 + SkillBarHandler.currentSkill * 20, scaledHeight - 23, 0, 22, 24, 22, SkillBarHandler.SKILL_BAR_Z_LEVEL);

                // draw the skills
                mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                int y = scaledHeight - 19;
                for (int i = 0; i < SkillBarHandler.SKILL_BAR.length; i++) {
                    int x = halfScaledWidth - 88 + 20 * i;
                    Skill skill = SkillBarHandler.SKILL_BAR[i];
                    if (skill == null) {
                        // if null, break since there will
                        // only be null skills left
                        break;
                    } else {
                        guiIng.drawTexturedModalRect(x, y, skill.getIconAsSprite(), 16, 16);
                    }
                }
                mc.getTextureManager().bindTexture(SkillBarHandler.WIDGETS);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public static void onGameOverlayRenderPostHighestReceiveCanceled(RenderGameOverlayEvent.Post event) {
        if (SkillBarHandler.shouldDrawSkillBar(event)) {
            // pop matrix to not affect other stuff
            GlStateManager.popMatrix();
            // "animate" hotbar
            if (SkillBarHandler.isBarGoingUp) {
                // go up
                SkillBarHandler.skillBarShowStage = Math.max(SkillBarHandler.skillBarShowStage - 0.4F, -SkillBarHandler.HOTBAR_PUSH_HEIGHT);
            } else {
                // go down
                SkillBarHandler.skillBarShowStage = Math.min(SkillBarHandler.skillBarShowStage + 0.4F, 0.0F);
            }
        }
    }

    /**
     * Called from {@link TransformationClientEventHandler#onGameOverlayRenderPre(RenderGameOverlayEvent.Pre)}
     */
    public static void onGameOverlayRenderPre(RenderGameOverlayEvent.Pre event) {
        if (ConfigHandler.client.showActiveSkill && (event.getType() == HOTBAR)) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Optional<Skill> activeSkill = TransformationHelper.getITransformationPlayer(mc.player).getActiveSkill();
            // TODO: Change position and size?
            // TODO: Make config option for position and size?
            activeSkill.ifPresent(s -> mc.ingameGUI.drawTexturedModalRect(0, 0, s.getIconAsSprite(), 32, 32));
        }
    }

    public static boolean shouldDrawSkillBar(RenderGameOverlayEvent event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        return SkillBarHandler.isSkillBarShown() && ((type == HEALTH) || (type == FOOD) || (type == AIR)
                || (type == ARMOR) || (type == EXPERIENCE) || (type == HEALTHMOUNT)
                || (type == HOTBAR) || (type == JUMPBAR));
    }

    public static float getSkillBarShowStage() {
        return SkillBarHandler.skillBarShowStage;
    }

    public static void showSkillBar() {
        if (!SkillBarHandler.isSkillBarShown()) {
            SkillBarHandler.skillBarShowStage = -0.01F;
            SkillBarHandler.isBarGoingUp = true;
            SkillBarHandler.currentSkill = 0;
            // TODO: Make normal hotbar possibly not show current item?
            ITransformationPlayer tp = TransformationHelper.getITransformationPlayer(Minecraft.getMinecraft().player);

            // add skills
            int index = 0;
            for (Skill skill : tp.getSkills()) {
                if (!skill.isAlwaysActive() && (tp.getSkillLevel(skill.getParent().orElse(skill)) == skill.getLevel())) {
                    SkillBarHandler.SKILL_BAR[index++] = skill;
                }
            }
            // replace all old skills
            for (int i = index; i < SkillBarHandler.SKILL_BAR.length; i++) {
                SkillBarHandler.SKILL_BAR[i] = null;
            }
        }
    }

    public static void hideSkillBar() {
        SkillBarHandler.isBarGoingUp = false;
    }

    public static boolean isSkillBarShown() {
        return SkillBarHandler.skillBarShowStage < 0.0F;
    }
}
